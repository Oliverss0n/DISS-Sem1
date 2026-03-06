import javax.swing.SwingWorker;
import java.util.List;
import java.util.ArrayList;

public class Presenter {
    private final ISimulationView view;
    private final List<SwingWorker<Void, double[]>> activeWorkers = new ArrayList<>();
    private SimulationModel[] variants;

    public Presenter(ISimulationView view) {
        this.view = view;
    }

    public void startSimulation() {
        stopSimulation();
        view.clearConsole();
        view.clearGraphs();

        final int totalReps = view.getReplications();
        final double skipPercent = view.getSkipPercentage();
        final int maxPoints = view.getMaxPoints();

        view.appendToConsole("Štartujem simuláciu: " + totalReps + " replikácií.");

        // Inicializácia tvojich 6 variantov
        variants = new SimulationModel[]{
                new Variant1(), new Variant2(), new Variant3(),
                new Variant4(), new Variant5(), new Variant6()
        };

        // Spustíme každý variant v samostatnom vlákne, aby bežali naraz a grafy sa hýbali spolu
        for (int i = 0; i < variants.length; i++) {
            runVariant(i, variants[i], totalReps, skipPercent, maxPoints);
        }
    }

    private void runVariant(int index, SimulationModel model, int totalReps, double skipPercent, int maxPoints) {
        SwingWorker<Void, double[]> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // Toto presne robí tvoja runSimulation na začiatku
                model.beforeSimulation();

                int skipCount = (int) (totalReps * (skipPercent / 100.0));
                // Výpočet, po koľkých replikáciách pridáme bod do grafu
                int interval = Math.max(1, (totalReps - skipCount) / maxPoints);

                // RUČNÝ CYKLUS (nahrádza runSimulation bez zmeny jadra)
                for (int i = 1; i <= totalReps; i++) {
                    if (isCancelled() || !model.isRunning()) break;

                    model.beforeReplication();
                    model.doReplication();    // Tvoja biznis logika z MonteCarloCore
                    model.afterReplication(); // Tvoja štatistika z MonteCarloCore

                    // POSIELANIE DÁT DO GRAFU
                    if (i > skipCount && (i - skipCount) % interval == 0) {
                        publish(new double[]{(double) i, model.getAverageArrivalSeconds()});
                    }
                }

                // Toto robí runSimulation na konci
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
                // Keď variant skončí, vypíšeme finálny výsledok do konzoly
                double finalAvg = model.getAverageArrivalSeconds();
                view.appendToConsole(String.format("Variant %d dokončený: %s",
                        (index + 1), formatToTime(finalAvg)));
            }
        };

        activeWorkers.add(worker);
        worker.execute();
    }

    public void stopSimulation() {
        if (variants != null) {
            for (SimulationModel v : variants) if (v != null) v.stop();
        }
        for (SwingWorker<Void, double[]> w : activeWorkers) {
            if (!w.isDone()) w.cancel(true);
        }
        activeWorkers.clear();
    }

    private String formatToTime(double totalSeconds) {
        if (Double.isNaN(totalSeconds)) return "00:00:00";
        int h = (int) (totalSeconds / 3600);
        int m = (int) ((totalSeconds % 3600) / 60);
        int s = (int) (totalSeconds % 60);
        return String.format("%02d:%02d:%02d (%.2f s)", h, m, s, totalSeconds);
    }
}