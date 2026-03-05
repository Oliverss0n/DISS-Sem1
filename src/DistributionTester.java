import Distributions.ContinuousEmpiricDist;
import Distributions.DiscreteEmpiricDist;

import java.io.PrintWriter;

public class DistributionTester {

    //trieda implementovana s pomocou pseodokodu vid. dokumentacia AI
    public void testDiscrete(DiscreteEmpiricDist dist, int min, int max, int samples) {
        int[] values = new int[max - min];

        for (int i = 0; i < samples; i++) {
            int val = dist.sample();
            int index = (int) (val - min);
            values[index]++;
        }

        saveToFile("diskretne.txt", min, values);
    }

    public void testContinuous(ContinuousEmpiricDist dist, int min, int max, int samples) {
        int[] values = new int[max - min];

        for (int i = 0; i < samples; i++) {
            double val = dist.sample();
            int index = (int) (val - min);
            values[index]++;
        }

        saveToFile("spojite.txt", min, values);
    }

    private void saveToFile(String fileName, int start, int[] data) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.println("Hodnota;Pocetnost");
            for (int i = 0; i < data.length; i++) {
                writer.println((start + i) + ";" + data[i]);
            }
        } catch (Exception e) {}
    }
}