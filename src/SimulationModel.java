import Distributions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class SimulationModel extends MonteCarloCore{

    /* suhrn variantov:
        1,Žilina - Divinka - Strečno - RT - Žilina
        2,Žilina - RT - Strečno - Divinka -Žilina
        3,Žilina - RT - Divinka - Strečno - Žilina
        4,Žilina - Strečno - Divinka - RT - Žilina
        5,Žilina - Divinka - RT - Strečno - Žilina
        6,Žilina - Strečno - RT - Divinka - Žilina
     */

    protected DiscreteUniformDist red;
    protected ContinuousUniformDist green;
    protected ContinuousEmpiricDist black;

    protected DiscreteEmpiricDist blue;

    protected ContinuousUniformDist slowingGen;
    protected TimeManager timeManager = new TimeManager(0);

    protected double timeSum;

    private int replications;

    private static final int DELAY_HOUR = 6;
    private static final int DELAY_MINUTE = 30;
    private static final int START_HOUR = 10;
    private static final int START_MINUTE = 0;

    private final double startSeconds = (START_HOUR * 3600) + (START_MINUTE * 60);
    //private final double limitSeconds = (DELAY_HOUR * 3600) + (DELAY_MINUTE * 60);

    protected ArrayList<Double> graphPoints =new ArrayList<>();
    protected double skipPercentage = 0;
    protected int maxPoints = 1000;
    protected int currentReplication;

    // --- Úloha 2 ---
    private LinkedList<Double> resultsPart2 = new LinkedList<>();
    private boolean part2Active = false;
    private final double targetArrivalTask2 = (7 * 3600) + (35 * 60); // 7:35 v sekundách
    //------------------

    @Override
    public void runSimulation(int replications) {
        this.replications = replications;
        super.runSimulation(replications);
    }

    @Override
    protected void beforeSimulation() {
        this.running = true;
        Random seedGen = new Random();
        this.timeManager = new TimeManager(startSeconds);

        red = new DiscreteUniformDist(55, 75, seedGen);
        green = new ContinuousUniformDist(50, 80, seedGen);

        double[] blackProbs = {0.1, 0.5, 0.2, 0.15, 0.05};
        double[] blackMins = {10, 20, 32, 45, 75};
        double[] blackMaxes = {20, 32, 45, 75, 85};
        black = new ContinuousEmpiricDist(blackProbs, blackMaxes, blackMins, seedGen);

        double[] blueProbs = {0.2, 0.4, 0.4};
        int[] blueMins = {15, 29, 45};
        int[] blueMaxes = {29, 45, 65};
        blue = new DiscreteEmpiricDist(blueProbs, blueMaxes, blueMins, seedGen);

        slowingGen = new ContinuousUniformDist(10, 25, seedGen);

        this.timeSum = 0;
        this.currentReplication = 0;
        this.graphPoints.clear();
    }

    protected double calculateLeavingKTime(double distance, double speed, double delayBeforeK) {
        double finalSpeed = speed;
        double slowPercent = slowingGen.sample();
        double predicted = timeManager.getTotalSeconds() + delayBeforeK;

        double limitSeconds = (DELAY_HOUR * 3600) + (DELAY_MINUTE * 60);

        if (predicted >= limitSeconds) {
            finalSpeed = speed * ((100.0 - slowPercent) / 100.0);
        }

        return timeManager.calcTime(distance, finalSpeed);
    }



    //------------------------------------ DIVINKA - ZILINA - STRECNO------------------------------------------------
    protected void driveZilinaToDivinka() {
        double timeToK = timeManager.calcTime(2, black.sample());
        double timeFromK = calculateLeavingKTime(2, red.sample(), timeToK);
        double throughK = timeToK + timeFromK;

        double throughRed = timeManager.calcTime(4, red.sample());
        double throughGreen = timeManager.calcTime(4, green.sample());

        double winner = Math.min(throughK, Math.min(throughRed, throughGreen));

        timeManager.addSeconds(winner);
    }
    protected void driveDivinkaToZilina() {
        double timeToK = timeManager.calcTime(2, red.sample());
        double timeFromK = calculateLeavingKTime(2, black.sample(), timeToK);
        double throughK = timeToK + timeFromK;

        double throughRed = timeManager.calcTime(4, red.sample());
        double throughGreen = timeManager.calcTime(4, green.sample());

        double winner = Math.min(throughK, Math.min(throughRed, throughGreen));

        timeManager.addSeconds(winner);
    }
    protected void driveZilinaToStrecno() {
        double timeToK = timeManager.calcTime(2, black.sample());
        double timeFromK = calculateLeavingKTime(4, blue.sample(), timeToK);
        double throughK = timeToK + timeFromK;

        double throughRed = timeManager.calcTime(3, red.sample()) + timeManager.calcTime(4, red.sample()); //MOZEM TOTO???
        double throughGreen_Black = timeManager.calcTime(4, green.sample())+ timeManager.calcTime(3, black.sample());

        double winner = Math.min(throughK, Math.min(throughRed, throughGreen_Black));

        timeManager.addSeconds(winner);
    }

    protected void driveStrecnoToZilina() {
        double timeToK = timeManager.calcTime(4, blue.sample());
        double timeFromK = calculateLeavingKTime(2, black.sample(), timeToK);
        double throughK = timeToK + timeFromK;

        double throughRed = timeManager.calcTime(4, red.sample()) + timeManager.calcTime(3, red.sample()); //MOZEM TOTO???
        double throughGreen_Black = timeManager.calcTime(3, black.sample()) + timeManager.calcTime(4, green.sample());

        double winner = Math.min(throughK, Math.min(throughRed, throughGreen_Black));

        timeManager.addSeconds(winner);
    }


    //-------------------------------STRECNO - RT----------------------------

    protected void driveStrecnoToRT() {
        double timeToK = timeManager.calcTime(4, blue.sample());
        double timeFromK = calculateLeavingKTime(2, green.sample(), timeToK);
        double throughK = timeToK + timeFromK;

        double throughBlue= timeManager.calcTime(5, blue.sample()) + timeManager.calcTime(8, blue.sample()); //MOZEM TOTO???
        double throughBlue_Black = timeManager.calcTime(5, black.sample()) + timeManager.calcTime(8, blue.sample());

        double winner = Math.min(throughK, Math.min(throughBlue, throughBlue_Black));

        timeManager.addSeconds(winner);
    }

    protected void driveRTtoStrecno() {
        double timeToK = timeManager.calcTime(2, green.sample());
        double timeFromK = calculateLeavingKTime(4, blue.sample(), timeToK);
        double throughK = timeToK + timeFromK;

        double throughBlue= timeManager.calcTime(8, blue.sample()) + timeManager.calcTime(5, blue.sample()); //MOZEM TOTO???
        double throughBlue_Black = timeManager.calcTime(8, blue.sample()) + timeManager.calcTime(5, black.sample());

        double winner = Math.min(throughK, Math.min(throughBlue, throughBlue_Black));

        timeManager.addSeconds(winner);
    }

    //------------------------------- RT - DIVINKA ---------------------------
    protected void driveRTToDivinka() {
        double timeToK = timeManager.calcTime(2, green.sample());
        double timeFromK = calculateLeavingKTime(2, red.sample(), timeToK);
        double throughK = timeToK + timeFromK;

        double throughBlue_Red_Black = timeManager.calcTime(1, blue.sample()) + timeManager.calcTime(3, red.sample()) + timeManager.calcTime(1, black.sample()); //MOZEM TOTO???
        double throughBlue_Red_Blue_Black =
                timeManager.calcTime(1, blue.sample()) + timeManager.calcTime(2, red.sample()) + timeManager.calcTime(1, blue.sample()) + timeManager.calcTime(1, black.sample());

        double winner = Math.min(throughK, Math.min(throughBlue_Red_Black, throughBlue_Red_Blue_Black));

        timeManager.addSeconds(winner);
    }

    protected void driveDivinkaToRT() {
        double timeToK = timeManager.calcTime(2, red.sample());
        double timeFromK = calculateLeavingKTime(2, green.sample(), timeToK);
        double throughK = timeToK + timeFromK;

        double throughBlue_Red_Black = timeManager.calcTime(1, blue.sample()) + timeManager.calcTime(3, red.sample()) + timeManager.calcTime(1, black.sample()); //MOZEM TOTO???
        double throughBlue_Red_Blue_Black =
                timeManager.calcTime(1, blue.sample()) + timeManager.calcTime(2, red.sample()) + timeManager.calcTime(1, blue.sample()) + timeManager.calcTime(1, black.sample());

        double winner = Math.min(throughK, Math.min(throughBlue_Red_Black, throughBlue_Red_Blue_Black));

        timeManager.addSeconds(winner);
    }


    protected void driveZilinaToRT() {
        double timeToK = timeManager.calcTime(2, black.sample());
        double timeFromK = calculateLeavingKTime(2, green.sample(), timeToK);

        timeManager.addSeconds(timeToK + timeFromK);
    }

    protected void driveRTtoZilina() {
        double timeToK = timeManager.calcTime(2, green.sample());
        double timeFromK = calculateLeavingKTime(2, black.sample(), timeToK);

        timeManager.addSeconds(timeToK + timeFromK);
    }

    protected void driveDivinkaToStrecno() {
        double timeToK = timeManager.calcTime(2, red.sample());
        double timeFromK = calculateLeavingKTime(4, blue.sample(), timeToK);

        timeManager.addSeconds(timeToK + timeFromK);
    }

    protected void driveStrecnoToDivinka() {
        double timeToK = timeManager.calcTime(4, blue.sample());
        double timeFromK = calculateLeavingKTime(2, red.sample(), timeToK);

        timeManager.addSeconds(timeToK + timeFromK);
    }




    @Override
    protected void beforeReplication() {
        timeManager.reset();
    }

    @Override
    protected void doReplication() {}

    @Override
    protected void afterReplication() {
        currentReplication++;
        double duration = timeManager.getTotalSeconds() - this.startSeconds;
        timeSum += duration;

        if(part2Active) {
            resultsPart2.add(duration);
        }
    }

    @Override
    protected void afterSimulation() {
        double averageSeconds = this.timeSum / (double)this.replications;

        timeManager.reset();
        timeManager.addSeconds(averageSeconds);

        System.out.println(this.toString());
        System.out.println("Cas prichodu: " + timeManager.toString());
        System.out.println("-----------------------------------------------");
        System.out.println(this.toString() + " | Priemer: " + getAverageArrivalSeconds());
    }

    private void evaluateTask2() {
        Collections.sort(resultsPart2);

        int index80 = (int) (resultsPart2.size() * 0.80);
        if (index80 >= resultsPart2.size()) index80 = resultsPart2.size() - 1;

        double travelTime80 = resultsPart2.get(index80);
        double departureTime = targetArrivalTask2 - travelTime80;

        // String builder pre prehľadný výpis
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- VÝSLEDOK ÚLOHY 2 (80% PRAVDEPODOBNOSŤ) ---\n");
        sb.append("Cieľ príchodu: 07:35:00\n");
        sb.append("Odsimulované replikácie: ").append(resultsPart2.size()).append("\n");
        sb.append("80% ciest bolo kratších ako: ").append(formatTime(travelTime80)).append("\n");
        sb.append("NAJNESKORŠÍ ODCHOD: ").append(formatTime(departureTime)).append("\n");
        sb.append("----------------------------------------------\n");

        // Ak máš v modeli prístup k view (cez Presenter), pošli to tam
        // Inak to vypíš aspoň do System.out, kým to neprepojíš
        System.out.println(sb.toString());
    }

    public double getAverageArrivalSeconds() {
        if (currentReplication == 0) return startSeconds;
        return (timeSum / currentReplication) + startSeconds;
    }

    public void setPart2Active(boolean active) {
        this.part2Active = active;
    }


    private String formatTime(double totalSecs) {
        int s = (int) Math.abs(totalSecs);
        int h = s / 3600;
        int m = (s % 3600) / 60;
        int sec = s % 60;
        return String.format("%02d:%02d:%02d", h, m, sec);
    }




}
