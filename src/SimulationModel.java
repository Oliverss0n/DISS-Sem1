import Distributions.*;

import java.util.Random;

public class SimulationModel extends MonteCarloCore{

    protected DiscreteUniformDist red;
    protected ContinuousUniformDist green;
    protected ContinuousEmpiricDist black;

    protected DiscreteEmpiricDist blue;

    protected TimeManager timeManager = new TimeManager(0);

    protected double timeSum;


    @Override
    protected void beforeSimulation() {
        Random seedGen = new Random();

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

        timeSum= 0;
    }

    @Override
    protected void beforeReplication() {
        timeManager.reset();
    }

    @Override
    protected void doReplication() {}

    @Override
    protected void afterReplication() {
        timeSum += timeManager.getTotalSeconds();
    }

    @Override
    protected void afterSimulation() {
        System.out.println("Simulácia dobehla.");
    }


}
