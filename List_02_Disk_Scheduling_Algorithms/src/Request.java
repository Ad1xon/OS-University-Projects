public class Request {
    private int cylinder;
    private boolean isRealTime;
    private int deadline;
    private int arrivalTime;
    private int completionTime;

    public Request(int cylinder, boolean isRealTime, int deadline, int arrivalTime) {
        this.cylinder = cylinder;
        this.isRealTime = isRealTime;
        this.deadline = deadline;
        this.arrivalTime = arrivalTime;
    }

    public int getCylinder() {
        return cylinder;
    }

    public boolean isRealTime() {
        return isRealTime;
    }

    public int getDeadline() {
        return deadline;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setCompletionTime(int time) { this.completionTime = time; }
    public int getCompletionTime() { return completionTime; }

    @Override
    public String toString() {
        return "Request{" +
                "cylinder=" + cylinder +
                ", isRealTime=" + isRealTime +
                ", deadline=" + deadline +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}