import java.util.*;

public class Simulator {
    private final int totalFrames;
    private final List<Process> processes;
    private Map<Process, Integer> suspendedProcesses;
    private int freeFrames;

    public static int PPF_WINDOW;
    public static int WSS_WINDOW;
    public static double PPF_L;
    public static double PPF_U;
    public static double PPF_H;

    public Simulator(int totalFrames, List<Process> processes, int ppf_window, int wss_window,
                     double ppf_l, double ppf_u, double ppf_h) {
        this.totalFrames = totalFrames;
        this.processes = processes;
        this.freeFrames = totalFrames;
        PPF_WINDOW = ppf_window;
        WSS_WINDOW = wss_window;
        PPF_L = ppf_l;
        PPF_U = ppf_u;
        PPF_H = ppf_h;
        suspendedProcesses = new HashMap<>();
        for(Process p : processes) {
            suspendedProcesses.put(p, 0);
        }
    }

    public void equalAllocation(List<Pair<Integer, Integer>> globalRefs) {
        int framesPerProcess = totalFrames / processes.size();
        for (Process p : processes) {
            p.setFrames(framesPerProcess);
        }
        freeFrames = totalFrames - framesPerProcess * processes.size();
        runGlobal(globalRefs, AllocationStrategy.EQUAL);
    }

    public void proportionalAllocation(List<Pair<Integer, Integer>> globalRefs, Map<Integer, Integer> pagesUsed) {
        proportionalAllocationMethod(globalRefs, pagesUsed);
        runGlobal(globalRefs, AllocationStrategy.PROPORTIONAL);
    }

    public void proportionalAllocationMethod(List<Pair<Integer, Integer>> globalRefs, Map<Integer, Integer> pagesUsed) {
        int total = pagesUsed.values().stream().mapToInt(i -> i).sum();
        for (Process p : processes) {
            int frames = Math.max(1, totalFrames * pagesUsed.get(p.id) / total);
            p.setFrames(frames);
        }
        freeFrames = totalFrames - processes.stream().mapToInt(p -> p.lru.getCapacity()).sum();
    }

    public void ppfAllocation(List<Pair<Integer, Integer>> globalRefs, Map<Integer, Integer> pagesUsed) {
        proportionalAllocationMethod(globalRefs, pagesUsed);
        runGlobal(globalRefs, AllocationStrategy.PPF);
    }

    public void wssAllocation(List<Pair<Integer, Integer>> globalRefs, Map<Integer, Integer> pagesUsed) {
        proportionalAllocationMethod(globalRefs, pagesUsed);
        runGlobal(globalRefs, AllocationStrategy.WSS);
    }

    private void runGlobal(List<Pair<Integer, Integer>> globalRefs, AllocationStrategy strategy) {
        int time = 0;

        for (Pair<Integer, Integer> entry : globalRefs) {
            int pid = entry.getKey();
            int page = entry.getValue();
            Process p = processes.get(pid);

            if (p.active && !p.isFinished()) {
                p.executePage(page);
            }

            time++;

            if (strategy == AllocationStrategy.PPF && time % PPF_WINDOW/2 == 0) {
                for (Process proc : processes) {
                    double ppf = proc.getPPF();

                    if (ppf > PPF_H) {
                        proc.active = false;
                        freeFrames += proc.lru.getCapacity();
                        suspendedProcesses.put(proc, suspendedProcesses.getOrDefault(proc, 0) + 1);
                        //System.out.printf("Proces %d wstrzymany (PPF=%.2f)%n", proc.id, ppf);
                    } else if (ppf > PPF_U && freeFrames > 0) {
                        proc.setFrames(proc.lru.getCapacity() + 1);
                        freeFrames--;
                    } else if (ppf < PPF_L && proc.lru.getCapacity() > 1) {
                        proc.setFrames(proc.lru.getCapacity() - 1);
                        freeFrames++;
                    }

                    proc.resetWindow();
                }

                for (Process proc : processes) {
                    if (!proc.active) {
                        int resumeFrames = 5;
                        if (freeFrames >= resumeFrames) {
                            proc.setFrames(resumeFrames);
                            freeFrames -= resumeFrames;
                            proc.active = true;
                            //System.out.printf("Proces " + proc.id + " wznowiony");
                            break;
                        }
                    }
                }
            }



            if (strategy == AllocationStrategy.WSS && time % (WSS_WINDOW / 2) == 0) {
                Map<Process, Integer> wssMap = new HashMap<>();
                int totalDemand = 0;

                for (Process proc : processes) {
                    if(proc.active) {
                        int wss = proc.getWorkingSet().size();
                        wssMap.put(proc, wss);
                        totalDemand += wss;
                    }

                }

                if (totalDemand > totalFrames) {
                    Process toSuspend = processes.stream()
                            .filter(proc -> proc.active)
                            .max(Comparator.comparingInt(wssMap::get))
                            .orElse(null);

                    if (toSuspend != null) {
                        toSuspend.active = false;
                        freeFrames += toSuspend.lru.getCapacity();
                        toSuspend.lru.setCapacity(0);
                        suspendedProcesses.put(toSuspend, suspendedProcesses.getOrDefault(toSuspend, 0) + 1);
                        //System.out.printf("Proces %d wstrzymany (WSS overflow)%n", toSuspend.id);
                    }
                } else {
                    for (Process proc : processes) {
                        if (proc.active) {
                            int wss = wssMap.getOrDefault(proc, 0);
                            int current = proc.lru.getCapacity();
                            if (wss > current && freeFrames > 0) {
                                int add = Math.min(freeFrames, wss - current);
                                proc.lru.setCapacity(current + add);
                                freeFrames -= add;
                            }
                        }
                    }

                }

                if(time % WSS_WINDOW/2 == 0) {
                    for(Process proc : processes) {
                        if(!proc.active) {
                            proc.active = true;
                            //System.out.printf("Proces " + proc.id + " wznowiony");
                        }
                    }
                }
            }
        }
    }

    public void printResults(AllocationStrategy strategy) {
        int pageFaults = 0;
        int thrashings = 0;
        int suspended = 0;

        for (Process p : processes) {
            pageFaults += p.getPageFaults();
            thrashings += p.getThrashings();
            suspended += suspendedProcesses.get(p);
            if(strategy == AllocationStrategy.PPF || strategy == AllocationStrategy.WSS) {
                System.out.printf("Proces %d: błędy = %d, szamotania = %d, wstrzymania = %d\n",
                        p.id, p.getPageFaults(), p.getThrashings(),  + suspendedProcesses.get(p));
            } else{
                System.out.printf("Proces %d: błędy = %d, szamotania = %d, ramki = %d\n",
                        p.id, p.getPageFaults(), p.getThrashings(), p.lru.getCapacity());
            }

        }

        System.out.println("-----------------------");
        System.out.println("Błędy stron: " + pageFaults);
        System.out.println("Szamotania: " + thrashings);
        if(strategy == AllocationStrategy.PPF || strategy == AllocationStrategy.WSS) {
            System.out.println("Wstrzymania: " + suspended);
        }
    }

    public enum AllocationStrategy {
        EQUAL, PROPORTIONAL, PPF, WSS
    }
}
