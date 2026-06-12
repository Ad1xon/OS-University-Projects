import java.util.*;
import java.util.stream.Collectors;

public class DiskScheduler {
    private int headPosition;
    private int originalHeadPosition;
    private int diskSize;
    private int totalMovement;
    private int currentTime;
    private List<Request> allRequests;
    private List<Request> originalRequests;
    private List<Request> completedRequests;
    private List<Request> starvedRequests;
    private List<Request> missedDeadlineRequests;
    private int maxWaitTime;
    private int cScanPasses;

    public DiskScheduler(int initialHeadPosition, int diskSize, int maxWaitTime, List<Request> requests) {
        this.headPosition = initialHeadPosition;
        this.originalHeadPosition = initialHeadPosition;
        this.diskSize = diskSize;
        this.maxWaitTime = maxWaitTime;
        this.allRequests = new ArrayList<>();
        this.originalRequests = new ArrayList<>();
        for(Request request : requests) {
            allRequests.add(new Request(request.getCylinder(), request.isRealTime(), request.getDeadline(), request.getArrivalTime()));
            originalRequests.add(new Request(request.getCylinder(), request.isRealTime(), request.getDeadline(), request.getArrivalTime()));
        }
        this.completedRequests = new ArrayList<>();
        this.starvedRequests = new ArrayList<>();
        this.missedDeadlineRequests = new ArrayList<>();
        this.totalMovement = 0;
        this.currentTime = 0;
        this.cScanPasses = 0;
    }

    private void addArrivedRequests(List<Request> queue) {

        Iterator<Request> iterator = allRequests.iterator();
        while (iterator.hasNext()) {
            Request req = iterator.next();
            if (req.getArrivalTime() <= currentTime) {
                queue.add(req);
                iterator.remove();
            }
        }

        /*allRequests.removeIf(req -> {
            if(req.getArrivalTime() <= currentTime){
                queue.add(req);
                return true;
            }
            return false;
        });*/
    }

    private void addArrivedRequestsSeparate(List<Request> normalQueue, List<Request> realTimeQueue) {
        Iterator<Request> iterator = allRequests.iterator();
        while (iterator.hasNext()) {
            Request req = iterator.next();
            if (req.getArrivalTime() <= currentTime) {
                if (req.isRealTime()) {
                    realTimeQueue.add(req);
                } else {
                    normalQueue.add(req);
                }
                iterator.remove();
            }
        }
    }

    private Request findClosestRequest(List<Request> requests) {
        Request closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Request req : requests) {
            int distance = Math.abs(req.getCylinder() - headPosition);
            if (distance < minDistance) {
                minDistance = distance;
                closest = req;
            }
        }
        return closest;
    }

    private void processRequest(Request request) {
        int distance = Math.abs(request.getCylinder() - headPosition);

        totalMovement += distance;
        headPosition = request.getCylinder();
        currentTime += distance;
        int waitTime = currentTime - request.getArrivalTime();

        if (request.isRealTime()) {
            if (currentTime > request.getDeadline()) {
                missedDeadlineRequests.add(request);
            }
            request.setCompletionTime(currentTime);
            completedRequests.add(request);
        } else {
            if (waitTime > maxWaitTime) {
                starvedRequests.add(request);
            }
            request.setCompletionTime(currentTime);
            completedRequests.add(request);
        }
    }

    private void serveRequestsAtCurrentCylinder(List<Request> queue) {
        queue.removeIf(req -> {
            if(req.getCylinder() == headPosition){
                processRequest(req);
                return true;
            }
            return false;
        });
    }

    private void reset() {
        this.headPosition = originalHeadPosition;
        this.totalMovement = 0;
        this.currentTime = 0;
        this.completedRequests.clear();
        this.starvedRequests.clear();
        this.cScanPasses = 0;
        this.allRequests.clear();
        for(Request request : originalRequests) {
            allRequests.add(new Request(request.getCylinder(), request.isRealTime(), request.getDeadline(), request.getArrivalTime()));
        }
    }

    public void FCFS() {
        reset();
        List<Request> queue = new ArrayList<>();

        while (!allRequests.isEmpty() || !queue.isEmpty()) {
            addArrivedRequests(queue);

            if (!queue.isEmpty()) {
                Request current = queue.remove(0);
                processRequest(current);
            } else {
                currentTime++;
            }
        }
    }

    public void SSTF() {
        reset();
        List<Request> queue = new ArrayList<>();

        while (!allRequests.isEmpty() || !queue.isEmpty()) {
            addArrivedRequests(queue);

            if (!queue.isEmpty()) {
                Request current = findClosestRequest(queue);
                queue.remove(current);
                processRequest(current);
            } else {
                currentTime++;
            }
        }
    }

    public void SCAN(boolean startRight) {
        reset();
        List<Request> queue = new ArrayList<>();
        boolean directionRight = startRight;

        while (!allRequests.isEmpty() || !queue.isEmpty()) {
            addArrivedRequests(queue);
            serveRequestsAtCurrentCylinder(queue);

            if(directionRight){
                headPosition++;
            } else{
                headPosition--;
            }
            totalMovement++;
            currentTime++;

            if(headPosition == diskSize-1 || headPosition == 0){
                directionRight = !directionRight;
            }
        }
    }


    public void CSCAN() {
        reset();
        List<Request> queue = new ArrayList<>();

        while (!allRequests.isEmpty() || !queue.isEmpty()) {
            addArrivedRequests(queue);
            serveRequestsAtCurrentCylinder(queue);

            headPosition++;
            totalMovement++;
            currentTime++;

            if(headPosition == diskSize){
                headPosition = 0;
                cScanPasses++;
            }
        }
    }

    public void EDF() {
        reset();
        List<Request> queue = new ArrayList<>();
        List<Request> realTimeQueue = new ArrayList<>();

        while (!allRequests.isEmpty() || !queue.isEmpty() || !realTimeQueue.isEmpty()) {
            addArrivedRequestsSeparate(queue, realTimeQueue);

            if (!realTimeQueue.isEmpty()) {
                realTimeQueue.sort(Comparator.comparingInt(Request::getDeadline));
                Request current = realTimeQueue.remove(0);

                processRequest(current);

            } else if (!queue.isEmpty()) {
                Request current = findClosestRequest(queue);
                queue.remove(current);
                processRequest(current);
            } else {
                currentTime++;
            }
        }
    }

    public void FDSCAN() {
        reset();
        List<Request> normalQueue = new ArrayList<>();
        List<Request> realTimeQueue = new ArrayList<>();
        boolean directionRight = true;

        while (!allRequests.isEmpty() || !normalQueue.isEmpty() || !realTimeQueue.isEmpty()) {
            addArrivedRequestsSeparate(normalQueue, realTimeQueue);

            List<Request> feasibleRT = realTimeQueue.stream().filter(req -> {
                int seekTime = Math.abs(req.getCylinder() - headPosition);
                if((currentTime + seekTime) <= req.getDeadline()){
                    System.out.println("test_00");
                    return true;
                }
                realTimeQueue.remove(req);
                return false;
            }).sorted(Comparator.comparingInt(Request::getDeadline)).toList();

            if (!feasibleRT.isEmpty()) {
                Request target = feasibleRT.get(0);
                int targetPos = target.getCylinder();
                int step = targetPos > headPosition ? 1 : -1;

                System.out.println("test_1");
                if(targetPos == headPosition) {
                    processRequest(target);
                }
                else {
                    while (headPosition != targetPos) {
                        headPosition += step;
                        totalMovement++;
                        currentTime++;
                        System.out.println("test_22");
                        addArrivedRequestsSeparate(normalQueue, realTimeQueue);
                        serveRequestsAtCurrentCylinder(normalQueue);
                        serveAll_RT_RequestsAtCurrentPosition(realTimeQueue);
                    }
                }

                System.out.println("test_333");
            } else {
                System.out.println("test_4444");
                serveRequestsAtCurrentCylinder(normalQueue);

                if(directionRight){
                    headPosition++;
                } else{
                    headPosition--;
                }
                totalMovement++;
                currentTime++;

                if(headPosition == diskSize-1 || headPosition == 0){
                    directionRight = !directionRight;
                }
            }
        }
    }

    private void serveAll_RT_RequestsAtCurrentPosition(List<Request> realTimeQueue) {

        realTimeQueue.removeIf(req -> {
            if(req.getCylinder() == headPosition) {
                processRequest(req);
                return true;
            }
            return false;
        });
    }

    public int getTotalMovement() {
        return totalMovement;
    }

    public double getAverageWaitingTime() {
        if (completedRequests.isEmpty()) return 0;
        int totalWaitingTime = 0;
        for (Request req : completedRequests) {
            int wait = req.getCompletionTime() - req.getArrivalTime();
            totalWaitingTime += wait;
        }
        return (double) totalWaitingTime / completedRequests.size();
    }

    public int getStarvedRequestsCount() {
        return starvedRequests.size();
    }

    public int getMissedDeadlineRequestsCount() {
        return missedDeadlineRequests.size();
    }

    public int getRealTimeRequestsCount() {
        return (int) allRequests.stream().filter(Request::isRealTime).count() +
                (int) completedRequests.stream().filter(Request::isRealTime).count() +
                (int) missedDeadlineRequests.stream().filter(Request::isRealTime).count();
    }

    public int getCScanPasses() {
        return cScanPasses;
    }
}