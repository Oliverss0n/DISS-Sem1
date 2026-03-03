public class TimeManager {

    //s/v * 60
    private double totalSeconds;


    public TimeManager(double seconds) {
        this.totalSeconds = seconds;
    }


    public boolean isAfter(int h, int m)
    {
        double s = h * 3600 + m * 60;
        return totalSeconds > s;
    }

    public void addSeconds(double s) {
        totalSeconds += s;
    }

    //v km
    public void addTravelTime(double distance, double speed) {
        if (speed > 0) {
            double hours = distance / speed;
            this.totalSeconds += (hours * 3600);
        }
    }
    public void reset() {
        this.totalSeconds = 0;
    }



    @Override
    public String toString() {

        int currentTotalSeconds = (int) totalSeconds;
        int hours = 6 + (currentTotalSeconds / 3600);
        int minutes = (currentTotalSeconds % 3600) / 60;
        int seconds = currentTotalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
