import java.util.*;

public class LRU implements PageReplacementAlgorithm {
    private final int capacity;
    private final Map<Integer, Integer> memory; // strona -> czas ostatniego użycia
    private int pageFaults;
    private final List<Boolean> faultHistory;


    public LRU(int capacity) {
        this.capacity = capacity;
        this.memory = new HashMap<>();
        this.pageFaults = 0;
        this.faultHistory = new ArrayList<>();
    }

    @Override
    public void simulate(List<Integer> referenceString) {
        memory.clear();
        pageFaults = 0;

        for (int time = 0; time < referenceString.size(); time++) {
            int page = referenceString.get(time);

            if (!memory.containsKey(page)) {
                pageFaults++;
                faultHistory.add(true);

                if (memory.size() >= capacity) {
                    int oldestPage = Collections.min(memory.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
                    memory.remove(oldestPage);
                }
            } else{
                faultHistory.add(false);
            }

            memory.put(page, time);
        }
    }

    @Override
    public int getPageFaultCount() {
        return pageFaults;
    }

    @Override
    public String getName() {
        return "LRU";
    }

    @Override
    public List<Boolean> getFaultHistory() {
        return faultHistory;
    }
}
