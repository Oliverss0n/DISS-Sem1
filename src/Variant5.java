public class Variant5 extends SimulationModel{

    @Override
    protected void doReplication() {
        driveZilinaToDivinka();
        driveDivinkaToStrecno();
        driveStrecnoToRT();
        driveRTtoZilina();
    }

    @Override
    public String toString() {
        return "V5: Zilina - Divinka - Strecno - RT - Zilina";
    }

}
