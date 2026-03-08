import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class SimulationGUI extends JFrame implements ISimulationView {
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTextField repsIn = new JTextField("1000000", 8);
    private JTextField skipIn = new JTextField("10", 4);
    private JTextField pointsIn = new JTextField("1000", 4);

    private JButton runBtn = new JButton("Spustiť všetko (Grafy)");
    private JButton stopBtn = new JButton("Zastaviť");

    // PRIDANÉ PRE ÚLOHU 2
    private JComboBox<String> variantCombo = new JComboBox<>(new String[]{
            "Variant 1", "Variant 2", "Variant 3", "Variant 4", "Variant 5", "Variant 6"
    });
    private JButton runTask2Btn = new JButton("Spustiť Úlohu 2 (80%)");

    private JTextArea resultsArea = new JTextArea(10, 50);
    private XYSeries[] seriesArray = new XYSeries[6];
    private Presenter presenter;

    public SimulationGUI() {
        setTitle("Monte Carlo Simulation - MVP");
        setSize(1200, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        presenter = new Presenter(this);

        // Horný panel - Nastavenia
        JPanel settingsPanel = new JPanel();
        settingsPanel.add(new JLabel("Replikácie:"));
        settingsPanel.add(repsIn);
        settingsPanel.add(new JLabel("Vynechať %:"));
        settingsPanel.add(skipIn);
        settingsPanel.add(new JLabel("Max. bodov:"));
        settingsPanel.add(pointsIn);
        settingsPanel.add(runBtn);
        settingsPanel.add(stopBtn);

        // Panel pre Úlohu 2
        JPanel task2Panel = new JPanel();
        task2Panel.setBorder(BorderFactory.createTitledBorder("Nastavenia pre Úlohu 2"));
        task2Panel.add(new JLabel("Vyber variant:"));
        task2Panel.add(variantCombo);
        task2Panel.add(runTask2Btn);

        // Spojenie panelov na sever
        JPanel northContainer = new JPanel(new GridLayout(2, 1));
        northContainer.add(settingsPanel);
        northContainer.add(task2Panel);
        add(northContainer, BorderLayout.NORTH);

        // Event Listeners
        runBtn.addActionListener(e -> presenter.startSimulation());
        stopBtn.addActionListener(e -> presenter.stopSimulation());
        runTask2Btn.addActionListener(e -> {
            presenter.startTask2(variantCombo.getSelectedIndex());
        });

        initTabs();
        add(tabbedPane, BorderLayout.CENTER);
        setupResultsArea();
    }

    private void setupResultsArea() {
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Konzola a výsledky"));

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setPreferredSize(new Dimension(1200, 200));
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

            // --- ŠKÁLOVANIE OSI X ---
            NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
            xAxis.setAutoRange(true);
            xAxis.setLowerMargin(0.0); // 1% medzera na začiatku
            xAxis.setUpperMargin(0.0); // 1% medzera na konci

            // --- ŠKÁLOVANIE OSI Y ---
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            yAxis.setAutoRange(true);
            yAxis.setAutoRangeIncludesZero(false); // Dôležité: nezobrazuje 00:00:00, ak nemusí

            // Tieto marginy zabezpečia, že čiara nebude na hornom/dolnom okraji
            yAxis.setLowerMargin(0.0); // 5% miesto pod čiarou
            yAxis.setUpperMargin(0.0); // 5% miesto nad čiarou

            // Formátovač času
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

    @Override public void addPointToGraph(int idx, double x, double y) {
        seriesArray[idx].add(x, y);
    }
    @Override public void setSimulationRunning(boolean running) {
        runBtn.setEnabled(!running);
        runTask2Btn.setEnabled(!running);
        stopBtn.setEnabled(true); // Necháme stop stále aktívny
    }

    @Override public int getReplications() { return Integer.parseInt(repsIn.getText()); }
    @Override public double getSkipPercentage() { return Double.parseDouble(skipIn.getText()); }
    @Override public int getMaxPoints() { return Integer.parseInt(pointsIn.getText()); }
    @Override public void clearGraphs() { for (XYSeries s : seriesArray) s.clear(); }
    @Override public void clearConsole() { resultsArea.setText(""); }

    @Override
    public boolean isPart2Enabled() {
        return false;
    }

    @Override public void appendToConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            resultsArea.append(text + "\n");
            resultsArea.setCaretPosition(resultsArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulationGUI().setVisible(true));
    }
}