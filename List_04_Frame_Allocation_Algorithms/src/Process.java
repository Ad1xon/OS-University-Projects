import java.util.*;

public class Process {
    public final int id;
    public final List<Integer> referenceString;
    public int currentTime = 0;
    public int pageFaults = 0;
    public LRU lru;
    public boolean active = true;

    private final Queue<Boolean> ppfWindow = new LinkedList<>();
    private final Queue<Boolean> thrashWindow = new LinkedList<>();
    private final Queue<Integer> recentPages = new LinkedList<>();

    private int thrashWindowSize = 30;
    private int thrashThreshold = 15;

    public int thrashings;

    public Process(int id, List<Integer> ref, int frameCount) {
        this.id = id;
        this.referenceString = ref;
        this.lru = new LRU(frameCount);
        this.thrashings = 0;
    }

    public void setFrames(int count) {
        this.lru = new LRU(count);
    }

    public boolean executePage(int page) {
        if (!active && isFinished()) return false;

        boolean fault = lru.accessPage(page);

        ppfWindow.add(fault);
        if (ppfWindow.size() > Simulator.PPF_WINDOW) {
            ppfWindow.poll();
        }

        thrashWindow.add(fault);
        if (thrashWindow.size() > thrashWindowSize) {
            thrashWindow.poll();
        }

        long faultsInWindow = thrashWindow.stream().filter(f -> f).count();
        if (faultsInWindow > thrashThreshold) {
            thrashings++;
            thrashWindow.clear();
        }

        if (fault) pageFaults++;

        recentPages.add(page);
        if (recentPages.size() > Simulator.WSS_WINDOW) {
            recentPages.poll();
        }

        currentTime++;
        return true;
    }

    public boolean isFinished() {
        return currentTime >= referenceString.size();
    }

    public double getPPF() {
        if (ppfWindow.isEmpty()) return 0;
        return (double) ppfWindow.stream().filter(b -> b).count() / ppfWindow.size();
    }

    public void resetWindow() {
        ppfWindow.clear();
    }

    public int getPageFaults() {
        return pageFaults;
    }

    public int getThrashings() {
        return thrashings;
    }

    public Set<Integer> getWorkingSet() {
        return new HashSet<>(recentPages);
    }

    public void reset() {
        currentTime = 0;
        pageFaults = 0;
        thrashings = 0;
        active = true;
        lru.clear();
        ppfWindow.clear();
        thrashWindow.clear();
        recentPages.clear();
    }
}
