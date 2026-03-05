public class Variant1 extends SimulationModel {
    @Override
    protected void doReplication() {
        driveZilinaToDivinka();
        driveDivinkaToRT();
        driveRTtoStrecno();
        driveStrecnoToZilina();
    }

    @Override
    public String toString() {
        return "V1: Zilina - Divinka - RT - Strecno - Zilina";
    }
}