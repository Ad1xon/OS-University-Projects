
import java.util.List;

public class Main {
    public static void main(String[] args) {

        int diskSize = 200;
        int initialHeadPosition = diskSize/2;
        int requestCount = 10*diskSize;
        double realTimeRatio = 0.2;
        int maxWaitTime = 2*diskSize;
        int addIncrement = (int)(0.25 * diskSize);
        int begin = 90, end = 110;

        System.out.println("\n====== TEST 1: Klasyczny generator (losowy) ======");
        List<Request> test1 = RequestGenerator.generateRequests(requestCount, diskSize, realTimeRatio, addIncrement);
        runAllAlgorithms(test1, initialHeadPosition, diskSize, maxWaitTime);

        System.out.println("\n====== TEST 2: Generator 65/35 lewa/prawa strona ======");
        List<Request> test2 = RequestGenerator.generateRequestsWeighted65Left35Right(requestCount, diskSize, realTimeRatio, addIncrement);
        runAllAlgorithms(test2, initialHeadPosition, diskSize, maxWaitTime);

        System.out.println("\n====== TEST 3: Generator close (na danym obszarze) ======");
        List<Request> test3 = RequestGenerator.closeGenrator(begin, end, requestCount, diskSize, realTimeRatio, addIncrement);
        runAllAlgorithms(test3, initialHeadPosition, diskSize, maxWaitTime);

        System.out.println("\n====== TEST 4: Ręczna tablica cylindrów ======");
        int[] cylindry = {98, 183, 37, 122, 14, 124, 65, 67};
        List<Request> test4 = RequestGenerator.generateFromManualCylinders(cylindry);
        runAllAlgorithms(test4, 53, 200, maxWaitTime);
    }

    private static void runAllAlgorithms(List<Request> requests, int headPos, int diskSize, int maxWait) {
        testAlgorithm("FCFS", new DiskScheduler(headPos, diskSize, maxWait, requests));
        testAlgorithm("SSTF", new DiskScheduler(headPos, diskSize, maxWait, requests));
        testScanInBothDirections(new DiskScheduler(headPos, diskSize, maxWait, requests));
        testAlgorithm("C-SCAN", new DiskScheduler(headPos, diskSize, maxWait, requests));
        testAlgorithm("EDF", new DiskScheduler(headPos, diskSize, maxWait, requests));
        //testAlgorithm("FD-SCAN", new DiskScheduler(headPos, diskSize, maxWait, requests));
    }

    private static void testAlgorithm(String name, DiskScheduler scheduler) {
        System.out.println("\nTestowanie algorytmu " + name + "...");

        switch(name) {
            case "FCFS": scheduler.FCFS(); break;
            case "SSTF": scheduler.SSTF(); break;
            //case "SCAN": scheduler.SCAN(); break;
            case "C-SCAN": scheduler.CSCAN(); break;
            case "EDF": scheduler.EDF(); break;
            case "FD-SCAN": scheduler.FDSCAN(); break;
        }

        printResults(name, scheduler);
    }

    private static void testScanInBothDirections(DiskScheduler scheduler) {
        System.out.println("\nTestowanie algorytmu SCAN (-> prawo)...");
        scheduler.SCAN(true);
        printResults("SCAN (right)", scheduler);

        System.out.println("\nTestowanie algorytmu SCAN (<- lewo)...");
        scheduler.SCAN(false);
        printResults("SCAN (left)", scheduler);
    }


    private static void printResults(String algorithmName, DiskScheduler scheduler) {
        System.out.println("=== Wyniki dla " + algorithmName + " ===");
        System.out.printf("1. Średni czas oczekiwania: %.2f\n", scheduler.getAverageWaitingTime());
        System.out.println("2. Łączny ruch głowicy: " + scheduler.getTotalMovement());
        System.out.println("3. Zagłodzone zwykłe żądania: " + scheduler.getStarvedRequestsCount());
        if (algorithmName.equals("EDF") || algorithmName.equals("FD-SCAN")) {
            System.out.println("4. Przegapione real-time: " + scheduler.getMissedDeadlineRequestsCount());
            System.out.println("5. Ilość wszystkich żądań real-time: " + scheduler.getRealTimeRequestsCount());
        }

        if (algorithmName.equals("C-SCAN")) {
            System.out.println("6. Liczba przejść C-SCAN: " + scheduler.getCScanPasses());
        }
    }
}
