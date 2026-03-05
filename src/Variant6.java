public class Variant6 extends SimulationModel {
    @Override
    protected void doReplication() {
        driveZilinaToStrecno();
        driveStrecnoToDivinka();
        driveDivinkaToRT();
        driveRTtoZilina();
    }

    @Override
    public String toString() {
        return "V6: Zilina - Strecno - Divinka - RT - Zilina";
    }
}