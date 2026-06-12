import java.util.*;

public class RAND implements PageReplacementAlgorithm {
    private final int capacity;
    private final List<Integer> memory;
    private final Set<Integer> pages;
    private final Random rand;
    private int pageFaults;
    private final List<Boolean> faultHistory;

    public RAND(int capacity) {
        this.capacity = capacity;
        this.memory = new ArrayList<>();
        this.pages = new HashSet<>();
        this.rand = new Random();
        this.pageFaults = 0;
        this.faultHistory = new ArrayList<>();
    }

    @Override
    public void simulate(List<Integer> referenceString) {
        memory.clear();
        pages.clear();
        pageFaults = 0;

        for (int page : referenceString) {
            if (!pages.contains(page)) {
                pageFaults++;
                faultHistory.add(true);

                if (memory.size() >= capacity) {
                    int victimIndex = rand.nextInt(memory.size());
                    int removedPage = memory.get(victimIndex);
                    pages.remove(removedPage);
                    memory.set(victimIndex, page);
                } else {
                    memory.add(page);
                }

                pages.add(page);
            } else{
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
        return "RAND";
    }

    @Override
    public List<Boolean> getFaultHistory() {
        return faultHistory;
    }
}
