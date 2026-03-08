import javax.swing.SwingWorker;
import java.util.ArrayList;
import java.util.List;

public class Presenter {
    private final ISimulationView view;
    private final List<SwingWorker<Void, ?>> activeWorkers = new ArrayList<>();
    private SimulationModel[] variants;

    private SimulationModel task2Model;

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
        //variants = new SimulationModel[]{
         //       new Variant1(), new Variant2(), new Variant3(),
           //     new Variant4(), new Variant5(), new Variant6()};

        for (int i = 0; i < variants.length; i++) {
            runVariant(i, variants[i], totalReps, false);
        }
    }

    // Pomocná metóda na vytvorenie správnej triedy podľa indexu z ComboBoxu
    private SimulationModel createVariant(int index) {
        switch (index) {
            case 0: return new Variant1();
            case 1: return new Variant2();
            case 2: return new Variant3();
            case 3: return new Variant4();
            case 4: return new Variant5();
            case 5: return new Variant6();
            default: return new Variant1();
        }
    }
    // NOVÁ METÓDA PRE ÚLOHU 2
    public void startTask2(int variantIndex) {
        stopSimulation(); // Vyčistíme staré behy
        view.clearConsole();

        // --- ZMENA TU: Priraď model PRED spustením vlákna ---
        this.task2Model = createVariant(variantIndex);
        this.task2Model.setPart2Active(true);
        int reps = view.getReplications();

        new Thread(() -> {
            view.setSimulationRunning(true);
            view.appendToConsole("Výpočet Úlohy 2 spustený...");

            // Spustíme ten model, ktorý už je uložený v "this.task2Model"
            this.task2Model.runSimulation(reps);

            // Získame výsledok (ak bol stopnutý, vypíše priebežný)
            if (this.task2Model != null) {
                String result = this.task2Model.getPart2Result();
                view.appendToConsole(result);
            }

            view.setSimulationRunning(false);
            this.task2Model = null;
        }).start();
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
            for (SimulationModel v : variants)  {
                if (v != null){
                    v.stop();
                }
            }
        }

        if (task2Model != null) {
            task2Model.stop();
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