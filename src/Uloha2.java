import java.util.Collections;
import java.util.LinkedList;

public class Uloha2 extends SimulationModel {

    private int chosenVariant = 0;

    private static final int U2_HOUR = 7;
    private static final int U2_MINUTE = 35;
    private static final int U2_INIT_HOUR = 7;
    private static final int U2_INIT_MINUTE = 0;
    private final double TARGET_ARRIVAL = (U2_HOUR * 3600) + (U2_MINUTE * 60);

    private final double initialTime = (U2_INIT_HOUR * 3600) + (U2_INIT_MINUTE * 60);

    private final LinkedList<Double> durations = new LinkedList<>();

    public Uloha2() {
        this.startSeconds = initialTime;
    }

    @Override
    protected void beforeSimulation() {
        super.beforeSimulation();
        durations.clear();
    }

    @Override
    protected void doReplication() {
        // Na základe indexu z Presentera zavoláme správnu trasu
        switch (chosenVariant) {
            case 0: // Variant1: Zilina - Divinka - RT - Strecno - Zilina
                driveZilinaToDivinka();
                driveDivinkaToRT();
                driveRTtoStrecno();
                driveStrecnoToZilina();
                break;
            case 1: // Variant2: Zilina - Strecno - RT - Divinka - Zilina
                driveZilinaToStrecno();
                driveStrecnoToRT();
                driveRTToDivinka();
                driveDivinkaToZilina();
                break;
            case 2: // Variant3: Zilina - RT - Divinka - Strecno - Zilina
                driveZilinaToRT();
                driveRTToDivinka();
                driveDivinkaToStrecno();
                driveStrecnoToZilina();
                break;
            case 3: // Variant4: Zilina - RT - Strecno - Divinka - Zilina
                driveZilinaToRT();
                driveRTtoStrecno();
                driveStrecnoToDivinka();
                break;
            case 4: // Variant5: Zilina - Divinka - Strecno - RT - Zilina
                driveZilinaToDivinka();
                driveDivinkaToStrecno();
                driveStrecnoToRT();
                driveRTtoZilina();
                break;
            case 5: // Variant6: Zilina - Strecno - Divinka - RT - Zilina
                driveZilinaToStrecno();
                driveStrecnoToDivinka();
                driveDivinkaToRT();
                driveRTtoZilina();
                break;
        }
    }

    @Override
    protected void afterReplication() {
        super.afterReplication();
        double duration = timeManager.getTotalSeconds() - startSeconds;
        durations.add(duration);
    }

    public String getFinalResult() {
        if (durations.isEmpty()) return "Žiadne dáta.";

        Collections.sort(durations);

        int index = (int) (durations.size() * 0.8);
        if (index >= durations.size()) index = durations.size() - 1;

        double duration = durations.get(index);
        double departure = TARGET_ARRIVAL - duration;

        return String.format(
                "\n--- VÝSLEDOK ÚLOHY 2 ---\n" +
                        "80%% jázd trvalo max: %s\n" +
                        "ODPORÚČANÝ ODCHOD: %s\n" +
                        "------------------------",
                formatTime(duration), formatTime(departure)
        );
    }

    public void setVariantIndex(int index) {
        this.chosenVariant = index;
    }
}