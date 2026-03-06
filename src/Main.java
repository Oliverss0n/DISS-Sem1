public class Main {

    public static void main(String[] args) {

        /* -----------------------------------------TESTY--------------------------------------------------
        Random seedGen = new Random();
        DistributionTester tester = new DistributionTester();


        double[] blackProbs = {0.1, 0.5, 0.2, 0.15, 0.05};
        double[] blackMins = {10, 20, 32, 45, 75};
        double[] blackMaxes = {20, 32, 45, 75, 85};

        ContinuousEmpiricDist blackDist = new ContinuousEmpiricDist(blackProbs, blackMaxes, blackMins, seedGen);

        tester.testContinuous(blackDist, 10, 85, 50_000_000);

        double[] blueProbs = {0.2, 0.4, 0.4};
        int[] blueMins = {15, 29, 45};
        int[] blueMaxes = {29, 45, 65};

        DiscreteEmpiricDist blueDist = new DiscreteEmpiricDist(blueProbs, blueMaxes, blueMins, seedGen);

        tester.testDiscrete(blueDist, 15, 65, 50_000_000);*/

        SimulationModel[] varianty = { new Variant1(), new Variant2(), new Variant3(), new Variant4(),new Variant5(), new Variant6() };

        for (SimulationModel variant : varianty) {
            variant.runSimulation(1000000);
        }

    }

}