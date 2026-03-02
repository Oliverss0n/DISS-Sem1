public abstract class MonteCarloCore {

    private boolean running;

    public void runSimulation(int replicationCount) {
        int currReplication = 1;
        running = true;

        beforeSimulation();

        while(currReplication <= replicationCount && running) {
            beforeReplication();
            doReplication();
            afterReplication();
            currReplication++;
        }

        afterSimulation();
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    protected abstract void beforeSimulation();
    protected abstract void beforeReplication();
    protected abstract void doReplication();
    protected abstract void afterReplication();
    protected abstract void afterSimulation();
}