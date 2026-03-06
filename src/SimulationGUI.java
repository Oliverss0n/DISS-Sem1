import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class SimulationGUI extends JFrame implements ISimulationView {
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTextField repsIn = new JTextField("1000000", 8);
    private JTextField skipIn = new JTextField("10", 4);
    private JTextField pointsIn = new JTextField("1000", 4);

    private JButton runBtn = new JButton("Spustiť všetko");
    private JButton stopBtn = new JButton("Zastaviť");

    private JCheckBox part2Cb = new JCheckBox("Úloha 2");
    private JTextArea resultsArea = new JTextArea(10, 50);
    private XYSeries[] seriesArray = new XYSeries[6];
    private Presenter presenter;

    public SimulationGUI() {
        setTitle("Monte Carlo Simulation - MVP");
        setSize(1200, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        presenter = new Presenter(this);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Replikácie:"));
        topPanel.add(repsIn);
        topPanel.add(new JLabel("Vynechať %:"));
        topPanel.add(skipIn);
        topPanel.add(new JLabel("Max. bodov:"));
        topPanel.add(pointsIn);

        runBtn.addActionListener(e -> presenter.startSimulation());
        stopBtn.addActionListener(e -> presenter.stopSimulation());

        // DÔLEŽITÉ: Stop musí byť na začiatku vypnutý, ale funkčný
        stopBtn.setEnabled(true);

        topPanel.add(runBtn);
        topPanel.add(stopBtn);
        add(topPanel, BorderLayout.NORTH);
        topPanel.add(part2Cb);

        initTabs();
        add(tabbedPane, BorderLayout.CENTER);

        setupResultsArea();
    }

    private void setupResultsArea() {
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Výsledky a štatistiky"));

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setPreferredSize(new Dimension(1200, 150));
        southPanel.add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void initTabs() {
        String[] names = {"Variant 1", "Variant 2", "Variant 3", "Variant 4", "Variant 5", "Variant 6"};
        for (int i = 0; i < 6; i++) {
            seriesArray[i] = new XYSeries("Priemerný čas");
            XYSeriesCollection dataset = new XYSeriesCollection(seriesArray[i]);

            JFreeChart chart = ChartFactory.createXYLineChart(names[i], "Replikácie", "Čas príchodu", dataset);
            XYPlot plot = chart.getXYPlot();

            // --- FIX PRE OSI ---
            NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
            xAxis.setAutoRange(true);
            xAxis.setLowerMargin(0.0);
            xAxis.setUpperMargin(0.0); // 5% rezerva vpravo

            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            yAxis.setAutoRange(true);
            yAxis.setAutoRangeIncludesZero(false);
            yAxis.setLowerMargin(0); // 10% rezerva pod čiarou
            yAxis.setUpperMargin(0); // 10% rezerva nad čiarou

            // Formátovač času ponecháme, ten je správne
            yAxis.setNumberFormatOverride(new java.text.NumberFormat() {
                private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                @Override
                public StringBuffer format(double number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                    long ms = (long) number * 1000;
                    long offset = java.util.TimeZone.getDefault().getRawOffset();
                    return sdf.format(new java.util.Date(ms - offset), toAppendTo, pos);
                }
                @Override public StringBuffer format(long number, StringBuffer toAppendTo, java.text.FieldPosition pos) { return format((double) number, toAppendTo, pos); }
                @Override public Number parse(String source, java.text.ParsePosition parsePosition) { return null; }
            });

            tabbedPane.addTab("V" + (i + 1), new ChartPanel(chart));
        }
    }

    @Override
    public void addPointToGraph(int idx, double x, double y) {
        // JFreeChart pri add automaticky vyvolá prekreslenie, ak je autoRange zapnutý
        seriesArray[idx].add(x, y);
    }

    @Override
    public void setSimulationRunning(boolean running) {
        // Ak beží, runBtn je vypnutý (alebo zapnutý podľa tvojej vôle),
        // ale stopBtn MUSÍ byť aktívny, aby sa dalo kliknúť.
        runBtn.setEnabled(running);
        stopBtn.setEnabled(running);
    }

    // Ostatné metódy (getters, clearConsole, atď.) zostávajú rovnaké
    @Override public int getReplications() { return Integer.parseInt(repsIn.getText()); }
    @Override public double getSkipPercentage() { return Double.parseDouble(skipIn.getText()); }
    @Override public int getMaxPoints() { return Integer.parseInt(pointsIn.getText()); }
    @Override public void clearGraphs() { for (XYSeries s : seriesArray) s.clear(); }
    @Override public void clearConsole() { resultsArea.setText(""); }
    @Override public void appendToConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            resultsArea.append(text + "\n");
            resultsArea.setCaretPosition(resultsArea.getDocument().getLength());
        });
    }

    @Override
    public boolean isPart2Enabled() {
        return part2Cb.isSelected();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulationGUI().setVisible(true));
    }
}