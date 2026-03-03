import Distributions.ContinuousEmpiricDist;
import Distributions.DiscreteEmpiricDist;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        Random seedGen = new Random();
        DistributionTester tester = new DistributionTester();


        double[] blackProbs = {0.1, 0.5, 0.2, 0.15, 0.05};
        double[] blackMins = {10, 20, 32, 45, 75};
        double[] blackMaxes = {20, 32, 45, 75, 85};

        ContinuousEmpiricDist blackDist = new ContinuousEmpiricDist(
                blackProbs,
                blackMaxes,
                blackMins,
                seedGen
        );

        tester.testContinuous(blackDist, 10, 85, 50_000_000);

        double[] blueProbs = {0.2, 0.4, 0.4};
        int[] blueMins = {15, 29, 45};
        int[] blueMaxes = {29, 45, 65};

        DiscreteEmpiricDist blueDist = new DiscreteEmpiricDist(blueProbs, blueMaxes, blueMins, seedGen);

        tester.testDiscrete(blueDist, 15, 65, 50_000_000);

    }
}