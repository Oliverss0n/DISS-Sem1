public interface ISimulationView {
    int getReplications();
    double getSkipPercentage();
    int getMaxPoints();
    void addPointToGraph(int variantIndex, double x, double y);
    void clearGraphs();
    void setSimulationRunning(boolean running);
}