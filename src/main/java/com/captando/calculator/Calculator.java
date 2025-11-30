package com.captando.calculator;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

public class Calculator extends JFrame {
    private final JTextField display = new JTextField("0");
    private AngleMode angleMode = AngleMode.DEGREES;
    private double storedValue = 0.0;
    private String pendingOperator = "";
    private boolean shouldResetDisplay = false;
    private GraphFrame graphFrame;

    public Calculator() {
        super("Calculadora Científica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(display.getFont().deriveFont(Font.BOLD, 28f));
        display.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        container.add(display, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.add(createUtilityPanel(), BorderLayout.NORTH);
        controlPanel.add(createScientificPanel(), BorderLayout.CENTER);
        controlPanel.add(createStandardPanel(), BorderLayout.SOUTH);

        container.add(controlPanel, BorderLayout.CENTER);
        add(container);
    }

    private JPanel createUtilityPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.add(createButton("AC", e -> clearAll()));
        panel.add(createButton("π", e -> setDisplayValue(Math.PI)));
        panel.add(createButton("e", e -> setDisplayValue(Math.E)));
        return panel;
    }

    private JPanel createScientificPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 4, 10, 10));
        panel.add(createButton("sin", e -> applyTrigonometric(Math::sin)));
        panel.add(createButton("cos", e -> applyTrigonometric(Math::cos)));
        panel.add(createButton("tan", e -> applyTrigonometric(Math::tan)));
        panel.add(createButton("√", e -> applySquareRoot()));

        panel.add(createButton("x²", e -> applyUnaryOperation(value -> value * value)));
        panel.add(createButton("xʸ", e -> applyOperator("^")));
        panel.add(createButton("1/x", e -> applyInverse()));
        panel.add(createButton("%", e -> applyPercentage()));

        panel.add(createButton("log", e -> applyLog10()));
        panel.add(createButton("ln", e -> applyNaturalLog()));
        panel.add(createButton("exp", e -> applyUnaryOperation(Math::exp)));
        panel.add(createButton("+/-", e -> toggleSign()));

        JToggleButton angleToggle = new JToggleButton("DEG");
        angleToggle.addActionListener(e -> toggleAngleMode(angleToggle));
        panel.add(angleToggle);
        panel.add(createButton("Gráfico", e -> openGraphFrame()));
        panel.add(createButton("CE", e -> clearEntry()));
        panel.add(new JLabel());

        return panel;
    }

    private JPanel createStandardPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 4, 10, 10));
        String[] keys = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+"
        };

        for (String key : keys) {
            switch (key) {
                case "+":
                case "-":
                case "*":
                case "/":
                    panel.add(createButton(key, e -> applyOperator(key)));
                    break;
                case "=":
                    panel.add(createButton(key, e -> handleEquals()));
                    break;
                case ".":
                    panel.add(createButton(key, e -> appendNumber(".")));
                    break;
                default:
                    panel.add(createButton(key, e -> appendNumber(key)));
                    break;
            }
        }
        return panel;
    }

    private JButton createButton(String label, java.awt.event.ActionListener listener) {
        JButton button = new JButton(label);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 16f));
        button.addActionListener(listener);
        return button;
    }

    private void appendNumber(String value) {
        if (shouldResetDisplay || "Erro".equalsIgnoreCase(display.getText())) {
            display.setText("");
            shouldResetDisplay = false;
        }

        if (".".equals(value) && display.getText().contains(".")) {
            return;
        }

        String current = display.getText();
        if ("0".equals(current) && !".".equals(value)) {
            display.setText(value);
        } else {
            display.setText(current + value);
        }
    }

    private void toggleSign() {
        double value = getDisplayValue();
        setDisplayValue(-value);
    }

    private void clearAll() {
        storedValue = 0.0;
        pendingOperator = "";
        shouldResetDisplay = false;
        display.setText("0");
    }

    private void clearEntry() {
        display.setText("0");
        shouldResetDisplay = false;
    }

    private void applyOperator(String operator) {
        double currentValue = getDisplayValue();

        if (!pendingOperator.isEmpty() && !shouldResetDisplay) {
            double result = evaluate(storedValue, currentValue, pendingOperator);
            setDisplayValue(result);
            storedValue = result;
        } else {
            storedValue = currentValue;
        }

        pendingOperator = operator;
        shouldResetDisplay = true;
    }

    private void handleEquals() {
        if (pendingOperator.isEmpty()) {
            return;
        }

        double currentValue = getDisplayValue();
        double result = evaluate(storedValue, currentValue, pendingOperator);
        setDisplayValue(result);
        pendingOperator = "";
        storedValue = result;
        shouldResetDisplay = true;
    }

    private double evaluate(double first, double second, String operator) {
        switch (operator) {
            case "+":
                return first + second;
            case "-":
                return first - second;
            case "*":
                return first * second;
            case "/":
                if (second == 0) {
                    showError();
                    return 0;
                }
                return first / second;
            case "^":
                return Math.pow(first, second);
            default:
                return second;
        }
    }

    private void applyTrigonometric(DoubleUnaryOperator operator) {
        double value = getDisplayValue();
        double angle = angleMode == AngleMode.DEGREES ? Math.toRadians(value) : value;
        double result = operator.applyAsDouble(angle);
        setDisplayValue(result);
        shouldResetDisplay = true;
    }

    private void applyUnaryOperation(DoubleUnaryOperator operator) {
        double value = getDisplayValue();
        double result = operator.applyAsDouble(value);
        setDisplayValue(result);
        shouldResetDisplay = true;
    }

    private void applySquareRoot() {
        double value = getDisplayValue();
        if (value < 0) {
            showError();
            return;
        }
        setDisplayValue(Math.sqrt(value));
        shouldResetDisplay = true;
    }

    private void applyInverse() {
        double value = getDisplayValue();
        if (value == 0) {
            showError();
            return;
        }
        setDisplayValue(1 / value);
        shouldResetDisplay = true;
    }

    private void applyPercentage() {
        double value = getDisplayValue();
        if (!pendingOperator.isEmpty()) {
            value = storedValue * (value / 100.0);
        } else {
            value = value / 100.0;
        }
        setDisplayValue(value);
        shouldResetDisplay = true;
    }

    private void applyLog10() {
        double value = getDisplayValue();
        if (value <= 0) {
            showError();
            return;
        }
        setDisplayValue(Math.log10(value));
        shouldResetDisplay = true;
    }

    private void applyNaturalLog() {
        double value = getDisplayValue();
        if (value <= 0) {
            showError();
            return;
        }
        setDisplayValue(Math.log(value));
        shouldResetDisplay = true;
    }

    private void toggleAngleMode(JToggleButton toggle) {
        if (toggle.isSelected()) {
            angleMode = AngleMode.RADIANS;
            toggle.setText("RAD");
        } else {
            angleMode = AngleMode.DEGREES;
            toggle.setText("DEG");
        }
    }

    private void openGraphFrame() {
        if (graphFrame == null) {
            graphFrame = new GraphFrame();
        }
        graphFrame.setVisible(true);
        graphFrame.toFront();
    }

    private double getDisplayValue() {
        String text = display.getText();
        if (text.isEmpty() || "Erro".equalsIgnoreCase(text)) {
            return 0.0;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private void setDisplayValue(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            showError();
            return;
        }
        String formatted = formatValue(value);
        display.setText(formatted);
    }

    private String formatValue(double value) {
        String formatted = String.format(Locale.US, "%.12f", value);
        if (formatted.contains(".")) {
            formatted = formatted.replaceAll("0+$", "");
            formatted = formatted.replaceAll("\\.$", "");
        }
        if (formatted.isEmpty() || "-0".equals(formatted)) {
            formatted = "0";
        }
        return formatted;
    }

    private void showError() {
        display.setText("Erro");
        pendingOperator = "";
        shouldResetDisplay = true;
    }

    private enum AngleMode {
        DEGREES,
        RADIANS
    }

    private static class GraphFrame extends JFrame {
        private final GraphPanel graphPanel = new GraphPanel();

        GraphFrame() {
            super("Gráficos");
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            setSize(600, 450);
            setLocationRelativeTo(null);

            setLayout(new BorderLayout(10, 10));
            JPanel topPanel = new JPanel(new BorderLayout(10, 10));

            JComboBox<FunctionType> comboBox = new JComboBox<>(FunctionType.values());
            comboBox.addActionListener(e -> graphPanel.setFunction((FunctionType) comboBox.getSelectedItem()));
            topPanel.add(new JLabel("Função:"), BorderLayout.WEST);
            topPanel.add(comboBox, BorderLayout.CENTER);

            add(topPanel, BorderLayout.NORTH);
            add(graphPanel, BorderLayout.CENTER);
        }
    }

    private enum FunctionType {
        SENO("sen(x)", Math::sin),
        COSSENO("cos(x)", Math::cos),
        TANGENTE("tan(x)", Math::tan),
        LOG("log(x)", x -> x <= 0 ? Double.NaN : Math.log10(x)),
        LN("ln(x)", x -> x <= 0 ? Double.NaN : Math.log(x)),
        EXP("e^x", Math::exp),
        QUADRADO("x²", x -> x * x),
        CUBO("x³", x -> x * x * x);

        private final String label;
        private final DoubleUnaryOperator function;

        FunctionType(String label, DoubleUnaryOperator function) {
            this.label = label;
            this.function = function;
        }

        public DoubleUnaryOperator getFunction() {
            return function;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class GraphPanel extends JPanel {
        private static final double MIN_X = -10;
        private static final double MAX_X = 10;
        private static final double MIN_Y = -10;
        private static final double MAX_Y = 10;

        private FunctionType function = FunctionType.SENO;

        GraphPanel() {
            setPreferredSize(new Dimension(580, 360));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        }

        void setFunction(FunctionType function) {
            this.function = function;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            drawAxes(g2d, width, height);
            drawGraph(g2d, width, height);

            g2d.dispose();
        }

        private void drawAxes(Graphics2D g2d, int width, int height) {
            g2d.setColor(new Color(220, 220, 220));
            for (int i = 1; i < 10; i++) {
                int x = width * i / 10;
                int y = height * i / 10;
                g2d.drawLine(x, 0, x, height);
                g2d.drawLine(0, y, width, y);
            }

            g2d.setColor(Color.GRAY);
            int zeroX = (int) mapValue(0, MIN_X, MAX_X, 0, width);
            int zeroY = (int) mapValue(0, MIN_Y, MAX_Y, height, 0);

            g2d.setStroke(new BasicStroke(2f));
            g2d.drawLine(zeroX, 0, zeroX, height);
            g2d.drawLine(0, zeroY, width, zeroY);
        }

        private void drawGraph(Graphics2D g2d, int width, int height) {
            g2d.setColor(new Color(0, 100, 255));
            g2d.setStroke(new BasicStroke(2f));

            DoubleUnaryOperator func = function.getFunction();
            boolean previousValid = false;
            int prevX = 0;
            int prevY = 0;

            for (int pixel = 0; pixel < width; pixel++) {
                double xValue = MIN_X + (pixel / (double) width) * (MAX_X - MIN_X);
                double yValue = func.applyAsDouble(xValue);

                if (Double.isNaN(yValue) || Double.isInfinite(yValue)) {
                    previousValid = false;
                    continue;
                }

                if (yValue < MIN_Y || yValue > MAX_Y) {
                    previousValid = false;
                    continue;
                }

                int screenX = pixel;
                int screenY = (int) mapValue(yValue, MIN_Y, MAX_Y, height, 0);

                if (previousValid) {
                    g2d.drawLine(prevX, prevY, screenX, screenY);
                }

                prevX = screenX;
                prevY = screenY;
                previousValid = true;
            }
        }

        private double mapValue(double value, double sourceMin, double sourceMax, double targetMin, double targetMax) {
            double proportion = (value - sourceMin) / (sourceMax - sourceMin);
            return targetMin + proportion * (targetMax - targetMin);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calculator().setVisible(true));
    }
}
