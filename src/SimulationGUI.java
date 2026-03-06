import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.*;

public class SimulationGUI extends JFrame implements ISimulationView {
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTextField repsIn = new JTextField("1000000", 8);
    private JTextField skipIn = new JTextField("10", 4);
    private JTextField pointsIn = new JTextField("1000", 4);
    private JButton runBtn = new JButton("Spustiť všetko");

    private XYSeries[] seriesArray = new XYSeries[6];
    private Presenter presenter;

    public SimulationGUI() {
        setTitle("Monte Carlo - MVP Ready");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        presenter = new Presenter(this);

        JPanel top = new JPanel();
        top.add(new JLabel("Replikácie:")); top.add(repsIn);
        top.add(new JLabel("Vynechať %:")); top.add(skipIn);
        top.add(new JLabel("Bodiek:")); top.add(pointsIn);

        runBtn.addActionListener(e -> presenter.startSimulation());
        top.add(runBtn);

        add(top, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        initTabs();
    }

    private void initTabs() {
        String[] names = {"Variant 1", "Variant 2", "Variant 3", "Variant 4", "Variant 5", "Variant 6"};
        for (int i = 0; i < 6; i++) {
            seriesArray[i] = new XYSeries("Priemerný čas");
            XYSeriesCollection dataset = new XYSeriesCollection(seriesArray[i]);

            JFreeChart chart = ChartFactory.createXYLineChart(names[i], "Replikácie", "Čas [s]", dataset);
            XYPlot plot = chart.getXYPlot();

            // --- KĽÚČOVÁ OPRAVA ŠKÁLOVANIA ---
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            yAxis.setAutoRangeIncludesZero(false); // NEZAČÍNAŤ od nuly
            yAxis.setAutoRange(true);              // Automaticky škálovať
            yAxis.setLowerMargin(0.1);             // Rezerva 10% dole
            yAxis.setUpperMargin(0.1);             // Rezerva 10% hore

            tabbedPane.addTab("V" + (i+1), new ChartPanel(chart));
        }
    }

    // ISimulationView metódy
    @Override public int getReplications() { return Integer.parseInt(repsIn.getText()); }
    @Override public double getSkipPercentage() { return Double.parseDouble(skipIn.getText()); }
    @Override public int getMaxPoints() { return Integer.parseInt(pointsIn.getText()); }

    @Override public void addPointToGraph(int idx, double x, double y) {
        seriesArray[idx].add(x, y); // JFreeChart sa sám prekreslí
    }

    @Override public void clearGraphs() {
        for (XYSeries s : seriesArray) s.clear();
    }

    @Override public void setSimulationRunning(boolean running) {
        runBtn.setEnabled(!running);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulationGUI().setVisible(true));
    }
}