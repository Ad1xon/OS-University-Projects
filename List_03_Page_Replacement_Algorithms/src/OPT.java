import java.util.*;

public class OPT implements PageReplacementAlgorithm {
    private final int capacity;
    private int pageFaults;
    private final List<Boolean> faultHistory;

    public OPT(int capacity) {
        this.capacity = capacity;
        this.pageFaults = 0;
        this.faultHistory = new ArrayList<>();
    }

    @Override
    public void simulate(List<Integer> references) {
        Set<Integer> memory = new HashSet<>();
        pageFaults = 0;

        for (int i = 0; i < references.size(); i++) {
            int page = references.get(i);
            if (!memory.contains(page)) {
                pageFaults++;
                faultHistory.add(true);

                if (memory.size() >= capacity) {
                    int victim = selectVictim(memory, references, i + 1);
                    memory.remove(victim);
                }

                memory.add(page);
            } else{
                faultHistory.add(false);
            }
        }
    }

    private int selectVictim(Set<Integer> memory, List<Integer> references, int startIndex) {
        Map<Integer, Integer> nextUse = new HashMap<>();

        for (int page : memory) {
            nextUse.put(page, Integer.MAX_VALUE);
            for (int i = startIndex; i < references.size(); i++) {
                if (references.get(i) == page) {
                    nextUse.put(page, i);
                    break;
                }
            }
        }

        int victim = -1;
        int farthest = -1;
        for (Map.Entry<Integer, Integer> entry : nextUse.entrySet()) {
            if (entry.getValue() > farthest) {
                farthest = entry.getValue();
                victim = entry.getKey();
            }
        }

        return victim;
    }

    @Override
    public int getPageFaultCount() {
        return pageFaults;
    }

    @Override
    public String getName() {
        return "OPT";
    }

    @Override
    public List<Boolean> getFaultHistory() {
        return faultHistory;
    }
}
