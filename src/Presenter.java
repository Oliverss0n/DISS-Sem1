import javax.swing.SwingWorker;
import java.util.ArrayList;
import java.util.List;

public class Presenter {
    private final ISimulationView view;
    private final List<SwingWorker<Void, ?>> activeWorkers = new ArrayList<>();
    private SimulationModel[] variants;
    private Uloha2 task2Model;

    // Potrebujeme referenciu na vlákno úlohy 2, aby sme ho vedeli stopnúť
    private Thread task2Thread;

    public Presenter(ISimulationView view) {
        this.view = view;
    }

    public void startSimulation() {
        stopSimulation();
        view.clearConsole();
        view.clearGraphs();

        int totalReps = view.getReplications();
        variants = new SimulationModel[]{
                new Variant1(), new Variant2(), new Variant3(),
                new Variant4(), new Variant5(), new Variant6()};

        for (int i = 0; i < variants.length; i++) {
            runVariant(i, variants[i], totalReps);
        }
    }

    public void startTask2(int variantIndex) {
        stopSimulation();
        view.clearConsole();

        this.task2Model = new Uloha2();
        this.task2Model.setVariantIndex(variantIndex);
        int reps = view.getReplications();

        task2Thread = new Thread(() -> {
            try {
                view.setSimulationRunning(true);
                view.appendToConsole("Výpočet Úlohy 2 spustený pre variant " + (variantIndex + 1) + "...");

                this.task2Model.runSimulation(reps);

                // ZMENA: Výsledok vypíšeme vždy, ak máme aspoň jednu odbehnutú replikáciu
                // getFinalResult() už v sebe má kontrolu na durations.isEmpty()
                String result = this.task2Model.getFinalResult();

                if (!result.equals("Žiadne dáta.")) {
                    if (!this.task2Model.isRunning()) {
                        view.appendToConsole("\n(Simulácia zastavená - výsledok z neúplných dát)");
                    }
                    view.appendToConsole(result);
                }

            } finally {
                view.setSimulationRunning(false);
                this.task2Model = null;
                this.task2Thread = null;
            }
        });
        task2Thread.start();
    }

    private void runVariant(int index, SimulationModel model, int totalReps) {
        // 1. Výpočet limitu - od ktorej replikácie začať vykresľovať
        int skipLimit = (int) (totalReps * (view.getSkipPercentage() / 100.0));

        SwingWorker<Void, double[]> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Spustíme updater v samostatnom vlákne
                startGraphUpdater(model, this, skipLimit);

                // Spustíme samotnú simuláciu
                model.runSimulation(totalReps);
                return null;
            }

            private void startGraphUpdater(SimulationModel model, SwingWorker<?,?> worker, int skipLimit) {
                new Thread(() -> {
                    int lastIndex = 0;
                    while (model.isRunning() && !worker.isCancelled()) {
                        int size = model.graphPoints.size();
                        while (lastIndex < size) {
                            double[] point = model.graphPoints.get(lastIndex);

                            // 2. FILTROVANIE: bod pošleme do grafu len ak prekročil skipLimit
                            if (point[0] >= skipLimit) {
                                publish(point);
                            }
                            lastIndex++;
                        }
                        try { Thread.sleep(50); } catch (InterruptedException e) { break; }
                    }
                    // Dočerpanie zvyšných bodov po skončení modelu
                    for (int i = lastIndex; i < model.graphPoints.size(); i++) {
                        double[] point = model.graphPoints.get(i);
                        if (point[0] >= skipLimit) {
                            publish(point);
                        }
                    }
                }).start();
            }

            @Override
            protected void process(List<double[]> chunks) {
                for (double[] point : chunks) {
                    view.addPointToGraph(index, point[0], point[1]);
                }
            }

            @Override
            protected void done() {
                String status = isCancelled() ? "[STOP] " : "[OK] ";
                view.appendToConsole(status + "V" + (index + 1) + ": " +
                        formatToTime(model.getAverageArrivalSeconds()));
                view.setSimulationRunning(false);
            }
        };

        activeWorkers.add(worker);
        worker.execute();
    }

    public void stopSimulation() {
        // 1. Zastavíme modely pre grafy
        if (variants != null) {
            for (SimulationModel v : variants) if (v != null) v.stop();
        }

        // 2. Zastavíme model Úlohy 2
        if (task2Model != null) {
            task2Model.stop();
        }

        // 3. Prerušíme samotné vlákno Úlohy 2, ak beží
        if (task2Thread != null) {
            task2Thread.interrupt();
        }

        // 4. Zastavíme SwingWorkery (grafy)
        for (SwingWorker<?, ?> w : activeWorkers) {
            w.cancel(true);
        }
        activeWorkers.clear();

        view.setSimulationRunning(false);
    }

    private String formatToTime(double totalSeconds) {
        if (Double.isNaN(totalSeconds)) return "00:00:00";
        int h = (int) (totalSeconds / 3600);
        int m = (int) ((totalSeconds % 3600) / 60);
        int s = (int) (totalSeconds % 60);
        return String.format("%02d:%02d:%02d", h, m, s);
    }


}