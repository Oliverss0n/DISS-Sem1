public class Main {

    public static void main(String[] args) {
        SimulationModel model = new Variant1();
        model.setPart2Active(true);
        model.runSimulation(1000000);

        String result = model.getPart2Result();

        System.out.println(result);
    }

}
