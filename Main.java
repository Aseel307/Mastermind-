/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class Main extends JFrame {
    private final String[] colors = {"Red", "Green", "Blue", "Yellow"};
    private final String[] secretCode = new String[4];
    private int attemptsLeft = 10;

    private ArrayList<JComboBox<String>> guessComboBoxes = new ArrayList<>();
    private JButton submitButton, resetButton;
    private JTextArea feedbackArea;
    private JPanel feedbackPanel, colorDisplayPanel;
    private JLabel attemptsLabel;

    public Main() {
        setTitle("Mastermind");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // تغيير لون الخلفية
        getContentPane().setBackground(new Color(230, 230, 250));

        generateSecretCode();

        // Panel for guesses
        JPanel guessPanel = new JPanel(new FlowLayout());
        guessPanel.setBackground(new Color(220, 220, 250));
        for (int i = 0; i < 4; i++) {
            JComboBox<String> comboBox = new JComboBox<>(colors);
            comboBox.setPreferredSize(new Dimension(100, 40));
            comboBox.setFont(new Font("Verdana", Font.BOLD, 14));
            guessComboBoxes.add(comboBox);
            guessPanel.add(comboBox);
        }

        // Submit button
        submitButton = new JButton("Submit Guess");
        styleButton(submitButton, new Color(72, 61, 139), Color.WHITE);
        submitButton.addActionListener(new SubmitButtonListener());

        // Reset button
        resetButton = new JButton("Reset Game");
        styleButton(resetButton, new Color(139, 0, 0), Color.WHITE);
        resetButton.addActionListener(new ResetButtonListener());

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(220, 220, 250));
        buttonPanel.add(submitButton);
        buttonPanel.add(resetButton);

        // Feedback area
        feedbackArea = new JTextArea();
        feedbackArea.setEditable(false);
        feedbackArea.setFont(new Font("Verdana", Font.PLAIN, 14));
        feedbackArea.setBackground(new Color(245, 245, 245));
        feedbackArea.setBorder(BorderFactory.createLineBorder(new Color(169, 169, 169)));
        feedbackArea.setPreferredSize(new Dimension(500, 120));

        // Attempts label
        attemptsLabel = new JLabel("Attempts Left: " + attemptsLeft, SwingConstants.CENTER);
        attemptsLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        attemptsLabel.setForeground(new Color(178, 34, 34));

        // Panel for feedback display
        colorDisplayPanel = new JPanel(new FlowLayout());
        colorDisplayPanel.setBackground(new Color(230, 230, 250));
        for (int i = 0; i < 4; i++) {
            JLabel label = new JLabel();
            label.setPreferredSize(new Dimension(50,50 ));
            label.setOpaque(true);
            label.setBackground(Color.LIGHT_GRAY);
            colorDisplayPanel.add(label);
        }

        // Adding components to frame
        add(guessPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(attemptsLabel, BorderLayout.SOUTH);
        add(new JScrollPane(feedbackArea), BorderLayout.WEST);
        add(colorDisplayPanel, BorderLayout.EAST);

        setVisible(true);
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setFont(new Font("Verdana", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
    }

    private void generateSecretCode() {
        Random rand = new Random();
        for (int i = 0; i < 4; i++) {
            secretCode[i] = colors[rand.nextInt(colors.length)];
        }
    }

    private String evaluateGuess() {
        StringBuilder feedback = new StringBuilder();
        String[] guess = new String[4];
        for (int i = 0; i < 4; i++) {
            guess[i] = (String) guessComboBoxes.get(i).getSelectedItem();
        }

        int correctPosition = 0, correctColor = 0;
        boolean[] secretUsed = new boolean[4];
        boolean[] guessUsed = new boolean[4];
                   Component[] labels = colorDisplayPanel.getComponents();
                   for (Component label : labels){
                       ((JLabel) label).setBackground(Color.LIGHT_GRAY);
                   }
        for (int i = 0; i < 4; i++) {
            if (secretCode[i].equals(guess[i])) {
                correctPosition++;
                secretUsed[i] = guessUsed[i] = true;
                ((JLabel) labels[i]).setBackground(Color.BLACK);
            }
        }

        for (int i = 0; i < 4; i++) {
            if (!guessUsed[i]) {
                for (int j = 0; j < 4; j++) {
                    if (!secretUsed[j] && secretCode[j].equals(guess[i])) {
                        correctColor++;
                        secretUsed[j] = true;
                        ((JLabel) labels[i]).setBackground(Color.WHITE);
                        break;
                    }
                }
            }
        }

        feedback.append("Correct positions: ").append(correctPosition).append("\n");
        feedback.append("Correct colors but wrong positions: ").append(correctColor).append("\n");

        if (correctPosition == 4) {
            feedback.append("Congratulations! You guessed the correct code!");
            submitButton.setEnabled(false);
        } else if (attemptsLeft == 1) {
            feedback.append("Game Over! The secret code was: ").append(Arrays.toString(secretCode));
            submitButton.setEnabled(false);
            revealSecretCode();
        }
        return feedback.toString();
    }

    private void revealSecretCode() {
        Component[] labels = colorDisplayPanel.getComponents();
        for (int i = 0; i < 4; i++) {
            JLabel jLabel = (JLabel) labels[i];
            jLabel.setBackground(getColor(secretCode[i]));
        }
    }

    private Color getColor(String colorName) {
        switch (colorName) {
            case "Red":
                return Color.RED;
            case "Green":
                    return Color.GREEN;
                    case"Blue":
                        return Color.BLUE;
                        case"Yellow":
                            return Color.YELLOW;
                        default:
                            return Color.GRAY;
        }
    }


    private class SubmitButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            feedbackArea.setText(evaluateGuess());
            attemptsLeft--;
            attemptsLabel.setText("Attempts Left: " + attemptsLeft);
        }
    }

    private class ResetButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            attemptsLeft = 10;
            generateSecretCode();
            guessComboBoxes.forEach(comboBox -> comboBox.setSelectedIndex(0));
            feedbackArea.setText("");
            submitButton.setEnabled(true);
            attemptsLabel.setText("Attempts Left: " + attemptsLeft);
            colorDisplayPanel.getComponents();
            for (Component label : colorDisplayPanel.getComponents()) {
                ((JLabel) label).setBackground(Color.LIGHT_GRAY);
            }
        }
    }

 
    public static void main(String[] args) {
        new Main();
    }
    
}
