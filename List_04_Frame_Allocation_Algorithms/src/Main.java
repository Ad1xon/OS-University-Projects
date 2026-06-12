import java.util.*;

public class Main {
    public static void main(String[] args) {
        //int[] pagesPerProcessArray = {6, 7, 10, 6, 6, 3, 8};
        //int totalFrames = 21;
        int[] pagesPerProcessArray = {10, 7, 4, 8, 9, 6, 5, 8};
        int totalFrames = 25;
        int processCount = pagesPerProcessArray.length;
        int refsPerProcess = 1000;

        int PPF_WINDOW = 50;
        double PPF_L = 0.1;
        double PPF_U = 0.4;
        double PPF_H = 0.55;

        int WSS_WINDOW = 50;

        Map<Integer, List<Integer>> referenceData = new HashMap<>();
        Map<Integer, Integer> pagesUsed = new HashMap<>();

        for (int i = 0; i < processCount; i++) {
            int pagesPerProcess = pagesPerProcessArray[i];
            int base = i * 50;
            List<Integer> ref = ReferenceGenerator.generateReferenceString(refsPerProcess, base, pagesPerProcess);
            referenceData.put(i, ref);
            pagesUsed.put(i, pagesPerProcess);
        }

        List<Pair<Integer, Integer>> globalRefStream = interleaveReferences(referenceData, refsPerProcess);

        for (Simulator.AllocationStrategy strategy : Simulator.AllocationStrategy.values()) {
            System.out.println("\n====== SYMULACJA: " + strategy + " ======");
            List<Process> processes = createProcesses(referenceData, 1);
            Simulator sim = new Simulator(totalFrames, processes, PPF_WINDOW, WSS_WINDOW, PPF_L, PPF_U, PPF_H);

            switch (strategy) {
                case EQUAL -> sim.equalAllocation(globalRefStream);
                case PROPORTIONAL -> sim.proportionalAllocation(globalRefStream, pagesUsed);
                case PPF -> sim.ppfAllocation(globalRefStream, pagesUsed);
                case WSS -> sim.wssAllocation(globalRefStream, pagesUsed);
            }

            sim.printResults(strategy);
        }
    }

    private static List<Process> createProcesses(Map<Integer, List<Integer>> refData, int initialFrames) {
        List<Process> processes = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : refData.entrySet()) {
            int id = entry.getKey();
            List<Integer> refs = new ArrayList<>(entry.getValue());
            Process p = new Process(id, refs, initialFrames);
            processes.add(p);
        }
        return processes;
    }

    private static List<Pair<Integer, Integer>> interleaveReferences(Map<Integer, List<Integer>> refs, int perProcess) {
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        for (int i = 0; i < perProcess; i++) {
            for (Map.Entry<Integer, List<Integer>> entry : refs.entrySet()) {
                int pid = entry.getKey();
                int page = entry.getValue().get(i);
                result.add(new Pair<>(pid, page));
            }
        }
        return result;
    }
}
