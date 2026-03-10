public interface ISimulationView {
    int getReplications();
    double getSkipPercentage();
    void addPointToGraph(int variantIndex, double x, double y);
    void clearGraphs();
    void setSimulationRunning(boolean running);
    void appendToConsole(String text);
    void clearConsole();
}