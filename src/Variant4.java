public class Variant4 extends SimulationModel{


    @Override
    protected void doReplication() {
        driveZilinaToRT();
        driveRTtoStrecno();
        driveStrecnoToDivinka();
        driveDivinkaToZilina();
    }
    @Override
    public String toString() {
        return "V4: Zilina - RT - Strecno - Divinka - Zilina";
    }

}
