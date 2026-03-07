import javax.swing.SwingWorker;
import java.util.ArrayList;
import java.util.List;

public class Presenter {
    private final ISimulationView view;
    private final List<SwingWorker<Void, ?>> activeWorkers = new ArrayList<>();
    private SimulationModel[] variants;

    public Presenter(ISimulationView view) {
        this.view = view;
    }

    // PÔVODNÁ SIMULÁCIA (GRAFY)
    public void startSimulation() {
        stopSimulation();
        view.clearConsole();
        view.clearGraphs();

        int totalReps = view.getReplications();
        variants = createVariants();

        for (int i = 0; i < variants.length; i++) {
            runVariant(i, variants[i], totalReps, false);
        }
    }

    // NOVÁ METÓDA PRE ÚLOHU 2
    public void startTask2(int selectedIdx) {
        stopSimulation();
        view.clearConsole();
        view.clearGraphs();

        int totalReps = view.getReplications();
        variants = createVariants();

        SimulationModel selectedModel = variants[selectedIdx];
        selectedModel.setPart2Active(true);

        view.appendToConsole("Spúšťam Úlohu 2 pre Variant " + (selectedIdx + 1) + "...");
        runVariant(selectedIdx, selectedModel, totalReps, true);
    }

    private void runVariant(int index, SimulationModel model, int totalReps, boolean isTask2) {
        SwingWorker<Void, double[]> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // 1. Spustíme samotnú simuláciu (synchrónne volanie tvojej metódy)
                // Toto vlákno tu "zostane", kým runSimulation neskončí

                // Ak chceme grafy počas behu, musíme vytvoriť pomocné vlákno na odosielanie dát
                if (!isTask2) {
                    startGraphUpdater(model, totalReps, this);
                }

                model.setPart2Active(isTask2);
                model.runSimulation(totalReps);

                return null;
            }

            // Pomocná metóda, ktorá beží paralelne a "kradne" dáta z modelu pre graf
            private void startGraphUpdater(SimulationModel model, int totalReps, SwingWorker worker) {
                new Thread(() -> {
                    int lastPublishedRep = 0;
                    int skipCount = (int) (totalReps * (view.getSkipPercentage() / 100.0));
                    int interval = Math.max(1, (totalReps - skipCount) / view.getMaxPoints());

                    while (model.isRunning() && !worker.isCancelled()) {
                        int currentRep = model.currentReplication; // Priamy prístup k tvojej premennej

                        // Ak model postúpil o dostatočný počet replikácií, pošleme bod do grafu
                        if (currentRep > skipCount && currentRep > lastPublishedRep + interval) {
                            publish(new double[]{(double) currentRep, model.getAverageArrivalSeconds()});
                            lastPublishedRep = currentRep;
                        }

                        // Malá pauza, aby sme nezahltili procesor neustálym pýtaním sa
                        try { Thread.sleep(50); } catch (InterruptedException e) { break; }
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
                if (isTask2) {
                    //view.appendToConsole(model.getPart2Result());
                } else {
                    view.appendToConsole("Variant " + (index + 1) +
                            " dokončený. Priemer: " + formatToTime(model.getAverageArrivalSeconds()));
                }
            }
        };

        activeWorkers.add(worker);
        worker.execute();
    }

    public void stopSimulation() {
        if (variants != null) {
            for (SimulationModel v : variants) if (v != null) v.stop();
        }
        for (SwingWorker<?, ?> w : activeWorkers) w.cancel(true);
        activeWorkers.clear();
    }

    private SimulationModel[] createVariants() {
        return new SimulationModel[]{
                new Variant1(), new Variant2(), new Variant3(),
                new Variant4(), new Variant5(), new Variant6()
        };
    }

    private String formatToTime(double totalSeconds) {
        if (Double.isNaN(totalSeconds)) return "00:00:00";
        int h = (int) (totalSeconds / 3600);
        int m = (int) ((totalSeconds % 3600) / 60);
        int s = (int) (totalSeconds % 60);
        return String.format("%02d:%02d:%02d (%.2f s)", h, m, s, totalSeconds);
    }
}