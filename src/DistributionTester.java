import Distributions.ContinuousEmpiricDist;
import Distributions.DiscreteEmpiricDist;

import java.util.Random;

public class DistributionTester {

    public static void main(String[] args) {
        Random seedGen = new Random();
        int replications = 1000000;

        System.out.println("--- Test Spojitého Empirického Rozdelenia (Čierny úsek) ---");
        testContinuous(seedGen, replications);

        System.out.println("\n--- Test Diskrétneho Empirického Rozdelenia (Modrý úsek) ---");
        testDiscrete(seedGen, replications);
    }

    private static void testContinuous(Random seedGen, int reps) {
        // Dáta zo zadania pre čierny úsek
        double[] p = {0.1, 0.5, 0.2, 0.15, 0.05};
        double[] mins = {10.0, 20.0, 32.0, 45.0, 75.0};
        double[] maxs = {20.0, 32.0, 45.0, 75.0, 85.0};

        ContinuousEmpiricDist dist = new ContinuousEmpiricDist(p, maxs, mins, seedGen);
        int[] counts = new int[p.length];

        for (int i = 0; i < reps; i++) {
            double val = dist.sample();
            for (int j = 0; j < p.length; j++) {
                if (val >= mins[j] && val < maxs[j]) {
                    counts[j]++;
                    break;
                }
            }
        }

        printResults(p, counts, reps);
    }

    private static void testDiscrete(Random seedGen, int reps) {
        // Dáta zo zadania pre modrý úsek
        double[] p = {0.2, 0.4, 0.4};
        int[] mins = {15, 29, 45};
        int[] maxs = {29, 45, 65};

        DiscreteEmpiricDist dist = new DiscreteEmpiricDist(p, maxs, mins, seedGen);
        int[] counts = new int[p.length];

        for (int i = 0; i < reps; i++) {
            int val = dist.sample();
            for (int j = 0; j < p.length; j++) {
                if (val >= mins[j] && val < maxs[j]) {
                    counts[j]++;
                    break;
                }
            }
        }

        printResults(p, counts, reps);
    }

    private static void printResults(double[] expectedP, int[] counts, int total) {
        System.out.printf("%-15s | %-15s | %-15s%n", "Interval", "Očakávané p", "Skutočné p");
        System.out.println("------------------------------------------------------------");
        for (int i = 0; i < expectedP.length; i++) {
            double actualP = (double) counts[i] / total;
            System.out.printf("Interval %d      | %-15.4f | %-15.4f%n", i + 1, expectedP[i], actualP);
        }
    }
}