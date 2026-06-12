import java.util.*;

public class FIFO implements PageReplacementAlgorithm {
    private final int capacity;
    private final Queue<Integer> memory;
    private final Set<Integer> pages;
    private int pageFaults;
    private final List<Boolean> faultHistory;

    public FIFO(int capacity) {
        this.capacity = capacity;
        this.memory = new LinkedList<>();
        this.pages = new HashSet<>();
        this.pageFaults = 0;
        this.faultHistory = new ArrayList<>();
    }

    @Override
    public void simulate(List<Integer> referenceString) {
        memory.clear();
        pages.clear();
        faultHistory.clear();
        pageFaults = 0;

        for (int page : referenceString) {
            if (!pages.contains(page)) {
                pageFaults++;
                faultHistory.add(true);

                if (memory.size() >= capacity) {
                    int removed = memory.poll();
                    pages.remove(removed);
                }
                memory.offer(page);
                pages.add(page);
            } else {
                faultHistory.add(false);
            }
        }
    }

    @Override
    public int getPageFaultCount() {
        return pageFaults;
    }

    @Override
    public String getName() {
        return "FIFO";
    }

    @Override
    public List<Boolean> getFaultHistory() {
        return faultHistory;
    }
}
