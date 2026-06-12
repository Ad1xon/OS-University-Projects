import java.util.*;

public class ALRU implements PageReplacementAlgorithm {
    private final int capacity;
    private final Queue<Integer> memoryQueue;
    private final Map<Integer, Boolean> referenceBits;
    private final List<Boolean> faultHistory;
    private int pageFaults;

    public ALRU(int capacity) {
        this.capacity = capacity;
        this.memoryQueue = new LinkedList<>();
        this.referenceBits = new HashMap<>();
        this.faultHistory = new ArrayList<>();
    }

    @Override
    public void simulate(List<Integer> referenceString) {
        memoryQueue.clear();
        referenceBits.clear();
        faultHistory.clear();
        pageFaults = 0;

        for (int page : referenceString) {
            if (referenceBits.containsKey(page)) {
                faultHistory.add(false);
                referenceBits.put(page, true);
            } else {
                pageFaults++;
                faultHistory.add(true);

                if (memoryQueue.size() >= capacity) {
                    while (true) {
                        int candidate = memoryQueue.poll();
                        if (referenceBits.get(candidate)) {
                            referenceBits.put(candidate, false);
                            memoryQueue.offer(candidate);
                        } else {
                            referenceBits.remove(candidate);
                            break;
                        }
                    }
                }

                memoryQueue.offer(page);
                referenceBits.put(page, true);
            }
        }
    }

    @Override public int getPageFaultCount() { return pageFaults; }
    @Override public String getName() { return "aLRU (Second Chance)"; }
    @Override public List<Boolean> getFaultHistory() { return faultHistory; }
}
