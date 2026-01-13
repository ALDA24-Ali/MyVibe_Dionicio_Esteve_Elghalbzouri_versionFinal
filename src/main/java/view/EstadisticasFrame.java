package view;

import dao.JDBCTransactionDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class EstadisticasFrame extends JFrame {

    private final JDBCTransactionDAO dao;
    private final JPanel chartContainer = new JPanel(new BorderLayout());

    private final JComboBox<Integer> cbYear = new JComboBox<>();
    private final JComboBox<Integer> cbMonth = new JComboBox<>();

    private static final List<String> MOODS_ORDER = List.of(
            "felicidad", "tristeza", "ansiedad", "ira", "enamorad@", "miedo", "nostalgia"
    );

    public EstadisticasFrame(JDBCTransactionDAO dao) {
        this.dao = dao;

        setTitle("Estadísticas mensuales");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel top = new JPanel();

        YearMonth now = YearMonth.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        for (int y = currentYear - 3; y <= currentYear + 1; y++) cbYear.addItem(y);
        for (int m = 1; m <= 12; m++) cbMonth.addItem(m);

        cbYear.setSelectedItem(currentYear);
        cbMonth.setSelectedItem(currentMonth);

        JButton btnLoad = new JButton("Cargar");

        top.add(new JLabel("Año:"));
        top.add(cbYear);
        top.add(new JLabel("Mes:"));
        top.add(cbMonth);
        top.add(btnLoad);

        add(top, BorderLayout.NORTH);
        add(chartContainer, BorderLayout.CENTER);

        btnLoad.addActionListener(e -> loadChart());

        loadChart();
    }

    private void loadChart() {
        int year = (Integer) cbYear.getSelectedItem();
        int month = (Integer) cbMonth.getSelectedItem();

        Map<String, Integer> counts = dao.getMoodCountsByMonth(year, month);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (String mood : MOODS_ORDER) {
            int total = counts.getOrDefault(mood, 0);
            int capped = Math.min(total, 100);
            dataset.addValue(capped, "Veces", mood);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Mood mensual - " + year + "/" + month,
                "Emoción",
                "Veces (0–100)",
                dataset,
                PlotOrientation.VERTICAL,
                false,  // legend
                true,   // tooltips
                false   // urls
        );

        CategoryPlot plot = chart.getCategoryPlot();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, 100.0);

        BarRenderer renderer = new BarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                String mood = MOODS_ORDER.get(column);
                return moodColor(mood);
            }
        };

        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.10);

        renderer.setSeriesItemLabelsVisible(0, true);
        renderer.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator());
        renderer.setSeriesItemLabelPaint(0, Color.WHITE);



        plot.setRenderer(renderer);

        ChartPanel panel = new ChartPanel(chart);

        chartContainer.removeAll();
        chartContainer.add(panel, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private Color moodColor(String mood) {
        switch (mood) {
            case "felicidad": return new Color(255, 193, 7);
            case "tristeza": return new Color(33, 150, 243);
            case "ansiedad": return new Color(255, 152, 0);
            case "ira": return new Color(244, 67, 54);
            case "enamorad@": return new Color(233, 30, 99);
            case "miedo": return new Color(103, 58, 183);
            case "nostalgia": return new Color(0, 188, 212);
            default: return Color.GRAY;
        }
    }
}
