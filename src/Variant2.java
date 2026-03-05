public class Variant2 extends SimulationModel {
    @Override
    protected void doReplication() {
        driveZilinaToStrecno();
        driveStrecnoToRT();
        driveRTToDivinka();
        driveDivinkaToZilina();
    }


    @Override
    public String toString() {
        return "V2: Zilina - Strecno - RT - Divinka - Zilina";
    }
}