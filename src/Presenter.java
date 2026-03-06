import javax.swing.SwingWorker;
import java.util.ArrayList;
import java.util.List;

public class Presenter {
    private final ISimulationView view;
    private final List<SwingWorker<Void, double[]>> activeWorkers = new ArrayList<>();
    private SimulationModel[] variants;

    public Presenter(ISimulationView view) {
        this.view = view;
    }

    public void startSimulation() {
        // 1. Zastavíme predchádzajúci beh, aby sa vlákna nebili,
        // ale v GUI nič nezasedujeme
        stopSimulation();

        view.clearConsole();
        view.clearGraphs();
        view.appendToConsole("Nové spustenie simulácie: " + view.getReplications() + " replikácií.");

        // 2. Vytvorenie nových inštancií
        variants = new SimulationModel[]{
                new Variant1(), new Variant2(), new Variant3(),
                new Variant4(), new Variant5(), new Variant6()
        };

        // 3. Spustíme nové výpočty
        for (int i = 0; i < variants.length; i++) {
            runVariant(i, variants[i]);
        }
    }

    public void stopSimulation() {
        // Povieme modelom, aby prestali (ak bežia)
        if (variants != null) {
            for (SimulationModel m : variants) {
                if (m != null) m.stop();
            }
        }

        // Zrušíme všetky bežiace SwingWorkery
        for (SwingWorker<Void, double[]> worker : activeWorkers) {
            if (!worker.isDone()) {
                worker.cancel(true);
            }
        }
        activeWorkers.clear();

        // Výpis do konzoly
        view.appendToConsole("Simulácia prerušená/zastavená.");
        if (variants != null) {
            for (int i = 0; i < variants.length; i++) {
                if (variants[i] != null) {
                    view.appendToConsole(String.format("V%d: %.2f s", (i + 1), variants[i].getAverageArrivalSeconds()));
                }
            }
        }
    }

    private void runVariant(int index, SimulationModel model) {
        final int totalReps = view.getReplications();
        final double skipPercent = view.getSkipPercentage();
        final int maxPoints = view.getMaxPoints();

        SwingWorker<Void, double[]> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // Vynútime, aby model vedel, že má bežať (reset flagu)
                model.beforeSimulation();

                int skipCount = (int) (totalReps * (skipPercent / 100.0));
                int interval = Math.max(1, (totalReps - skipCount) / maxPoints);

                for (int i = 1; i <= totalReps; i++) {
                    // Kontrola, či sme neklikli na Stop alebo znova na Štart
                    if (isCancelled() || !model.isRunning()) {
                        break;
                    }

                    model.beforeReplication();
                    model.doReplication();
                    model.afterReplication();

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
                // Tu nerobíme nič so zasedovaním tlačidiel,
                // nechávame ich stále v takom stave, v akom sú.
            }
        };

        activeWorkers.add(worker);
        worker.execute();
    }
}