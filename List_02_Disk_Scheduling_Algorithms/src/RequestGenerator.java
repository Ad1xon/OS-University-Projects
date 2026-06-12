import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RequestGenerator {
    private static final Random random = new Random();

    private static int generateDeadline(int diskSize) {
        return random.nextInt((int)(0.8*diskSize), (int)(diskSize));
    }

    // Domyślny generator (równomiernie rozłożone)
    public static List<Request> generateRequests(int count, int diskSize, double realTimeRatio, int addIncrement) {
        List<Request> requests = new ArrayList<>();
        int time = 0;

        for (int i = 0; i < count; i++) {
            int cylinder = random.nextInt(diskSize);
            boolean isRealTime = random.nextDouble() < realTimeRatio;
            int deadline = 0;
            time += addIncrement;

            if (isRealTime) {
                int distance = generateDeadline(diskSize);
                deadline = time + distance;
            }

            requests.add(new Request(cylinder, isRealTime, deadline, time));
        }
        return requests;
    }

    // Generator z rozkładem 65% po lewej stronie, 35% po prawej
    public static List<Request> generateRequestsWeighted65Left35Right(int count, int diskSize, double realTimeRatio, int addIncrement) {
        List<Request> requests = new ArrayList<>();
        int time = 0;

        for (int i = 0; i < count; i++) {
            int cylinder;

            if (random.nextDouble() < 0.65) {
                cylinder = random.nextInt(diskSize / 2);
            } else {
                cylinder = random.nextInt(diskSize / 2) + diskSize / 2;
            }

            boolean isRealTime = random.nextDouble() < realTimeRatio;
            int deadline = 0;
            time += addIncrement;

            if (isRealTime) {
                int distance = generateDeadline(diskSize);
                deadline = time + distance;
            }

            requests.add(new Request(cylinder, isRealTime, deadline, time));
        }
        return requests;
    }

    // Generator ze stałą tablicą cylindrów (dla testów)
    public static List<Request> generateFromManualCylinders(int[] cylinders) {
        List<Request> requests = new ArrayList<>();
        for (int cylinder : cylinders) {
            requests.add(new Request(cylinder, false, -1, 0));
        }
        return requests;
    }

    public static List<Request> closeGenrator(int begin, int end, int count, int diskSize, double realTimeRatio, int addIncrement) {
        List<Request> requests = new ArrayList<>();
        int time = 0;

        for (int i = 0; i < count; i++) {
            int cylinder = random.nextInt(begin, end);
            boolean isRealTime = random.nextDouble() < realTimeRatio;
            int deadline = 0;
            time += addIncrement;

            if (isRealTime) {
                int distance = generateDeadline(diskSize);
                deadline = time + distance;
            }

            requests.add(new Request(cylinder, isRealTime, deadline, time));
        }
        return requests;
    }
}
