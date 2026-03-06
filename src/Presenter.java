import javax.swing.SwingWorker;
import java.util.List;

public class Presenter {
    private final ISimulationView view;

    public Presenter(ISimulationView view) {
        this.view = view;
    }

    public void startSimulation() {
        view.clearGraphs();
        view.setSimulationRunning(true);

        SimulationModel[] variants = {
                new Variant1(), new Variant2(), new Variant3(),
                new Variant4(), new Variant5(), new Variant6()
        };

        for (int i = 0; i < variants.length; i++) {
            runVariant(i, variants[i]);
        }
    }

    private void runVariant(int index, SimulationModel model) {
        final int totalReps = view.getReplications();
        final double skipPercent = view.getSkipPercentage();
        final int maxPoints = view.getMaxPoints();

        new SwingWorker<Void, double[]>() {
            @Override
            protected Void doInBackground() {
                model.beforeSimulation();

                int skipCount = (int) (totalReps * (skipPercent / 100.0));
                int remaining = totalReps - skipCount;
                int interval = Math.max(1, remaining / maxPoints);

                for (int i = 1; i <= totalReps; i++) {
                    model.beforeReplication();
                    model.doReplication();
                    model.afterReplication();

                    // Posielame dáta do process()
                    if (i > skipCount && (i - skipCount) % interval == 0) {
                        publish(new double[]{(double) i, model.getAverageArrivalSeconds()});
                    }
                }
                model.afterSimulation();
                return null;
            }

            @Override
            protected void process(List<double[]> chunks) {
                for (double[] point : chunks) {
                    view.addPointToGraph(index, point[0], point[1]);
                }
            }

            @Override
            protected void done() {
                view.setSimulationRunning(false);
            }
        }.execute();
    }
}