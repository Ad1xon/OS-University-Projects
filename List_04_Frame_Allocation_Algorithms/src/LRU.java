import java.util.*;

public class LRU {
    private int capacity;
    private Deque<Integer> frames;

    public LRU(int capacity) {
        this.capacity = capacity;
        this.frames = new LinkedList<>();
    }

    public boolean accessPage(int page) {
        boolean pageFault = false;

        if (!frames.contains(page)) {
            pageFault = true;
            if (frames.size() == capacity) {
                frames.removeFirst();
            }
        } else {
            frames.remove(page);
        }
        frames.addLast(page);
        return pageFault;
    }

    public void clear() {
        frames.clear();
    }

    public int getSize() {
        return frames.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int _capacity) {
        this.capacity = _capacity;
    }

    public Set<Integer> getFrames() {
        return new HashSet<>(frames);
    }
}
