import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        XYSeries series = new XYSeries("Data");

        for (int i = 0; i < 50; i++) {
            series.add(i, Math.random() * 10);
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Test Graf",
                "X",
                "Y",
                dataset
        );

        JFrame frame = new JFrame("JFreeChart Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}