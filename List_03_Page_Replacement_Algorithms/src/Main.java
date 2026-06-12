import java.util.*;

public class Main {
    public static void main(String[] args) {
        int totalPages = 25;
        int memorySize = 10;
        int referenceLength = 10000;
        int localityWindow = 12;
        int phaseLength = 50;

        int windowSize = 6;
        int threshold = 4;

        List<Integer> referenceString = PageReferenceGenerator.generate(referenceLength, totalPages, localityWindow, phaseLength);
        //List<Integer> referenceString = List.of(1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5);
        List<PageReplacementAlgorithm> algorithms = List.of(
                new FIFO(memorySize),
                new RAND(memorySize),
                new OPT(memorySize),
                new LRU(memorySize),
                new ALRU(memorySize)
        );

        for (PageReplacementAlgorithm algo : algorithms) {
            algo.simulate(referenceString);
            System.out.println("======" + algo.getName() + "======");
            System.out.println("błędy stron = " + algo.getPageFaultCount());

            ThrashingDetector detector = new ThrashingDetector(windowSize, threshold);
            ThrashingDetector.ThrashingResult result = detector.analyze(algo.getFaultHistory());

            System.out.println("liczba szamotań: " + result.totalThrashes);
            System.out.println("najdłuższe szamotanie: " + result.maxStreak);
            System.out.println();

        }
    }
}
