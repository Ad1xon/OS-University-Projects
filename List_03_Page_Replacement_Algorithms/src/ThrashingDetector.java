import java.util.List;

public class ThrashingDetector {
    private final int windowSize;
    private final int threshold;

    public ThrashingDetector(int windowSize, int threshold) {
        this.windowSize = windowSize;
        this.threshold = threshold;
    }

    public ThrashingResult analyze(List<Boolean> pageFaults) {
        int totalThrashes = 0;
        int maxThrashStreak = 0;
        int currentStreak = 0;

        for (int i = 0; i <= pageFaults.size() - windowSize; i += windowSize / 2) {
            int faults = 0;
            for (int j = 0; j < windowSize; j++) {
                if (pageFaults.get(i + j)) faults++;
            }

            if (faults >= threshold) {
                totalThrashes++;
                currentStreak++;
                maxThrashStreak = Math.max(maxThrashStreak, currentStreak);
            } else {
                currentStreak = 0;
            }
        }

        return new ThrashingResult(totalThrashes, maxThrashStreak);
    }

    public static class ThrashingResult {
        public final int totalThrashes;
        public final int maxStreak;

        public ThrashingResult(int totalThrashes, int maxStreak) {
            this.totalThrashes = totalThrashes;
            this.maxStreak = maxStreak;
        }
    }
}
