import Distributions.ContinuousEmpiricDist;
import Distributions.DiscreteEmpiricDist;

import java.io.PrintWriter;

public class DistributionTester {

    public void testDiscrete(DiscreteEmpiricDist dist, int min, int max, int samples) {
        int[] counts = new int[max - min];

        for (int i = 0; i < samples; i++) {
            int val = dist.sample();
            counts[val - min]++;
        }

        saveToFile("discrete_results.txt", min, 1, counts, samples);
    }

    public void testContinuous(ContinuousEmpiricDist dist, int min, int max, int samples) {
        int[] bins = new int[max - min];

        for (int i = 0; i < samples; i++) {
            double val = dist.sample();
            int index = (int) (val - min);
            if (index >= 0 && index < bins.length) {
                bins[index]++;
            }
        }

        saveToFile("continuous_results.txt", min, 1, bins, samples);
    }

    private void saveToFile(String fileName, int startVal, int step, int[] data, int total) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.println("Value_Interval;Count;Relative_Frequency");
            for (int i = 0; i < data.length; i++) {
                double freq = (double) data[i] / total;
                writer.println((startVal + i * step) + ";" + data[i] + ";" + freq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}