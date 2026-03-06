public class TimeManager {

    //s/v * 60
    private double totalSeconds;
    private double startSeconds;


    public TimeManager(double startSeconds) {
        this.startSeconds = startSeconds;
        this.totalSeconds = startSeconds;
    }


    public void addSeconds(double s) {
        totalSeconds += s;
    }

    public void addTravelTime(double distance, double speed) {
        if (speed > 0) {
            double hours = distance / speed;
            this.totalSeconds += (hours * 3600);
        }
    }

    public double calcTime(double distance, double speed) {
            return (distance / speed) * 3600;
    }


    public void reset() {
        this.totalSeconds = startSeconds;
    }

    public double getTotalSeconds() {
        return totalSeconds;
    }


    @Override
    public String toString() {
        int currentTotalSeconds = (int) totalSeconds;
        int hours = (currentTotalSeconds / 3600) % 24;
        int minutes = (currentTotalSeconds % 3600) / 60;
        int seconds = currentTotalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
