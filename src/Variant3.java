public class Variant3 extends SimulationModel {
    @Override
    protected void doReplication() {
        driveZilinaToRT();
        driveRTToDivinka();
        driveDivinkaToStrecno();
        driveStrecnoToZilina();
    }

    @Override
    public String toString() {
        return "V3: Zilina - RT - Divinka - Strecno - Zilina";
    }
}