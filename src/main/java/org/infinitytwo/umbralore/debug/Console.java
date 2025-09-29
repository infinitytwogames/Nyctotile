package org.infinitytwo.umbralore.debug;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;

/**
 * The dedicated JFrame for our game's debug console.
 */
@Deprecated
public class Console extends JFrame {
    private JTextArea consoleOutputArea;
    private PrintStream originalOut;
    private PrintStream originalErr;

    public Console() {
        super("Game Debug Console"); // Window title
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Hide on close, don't exit app
        setSize(700, 500); // Console window size
        setLocation(100, 100); // Initial position

        consoleOutputArea = new JTextArea();
        consoleOutputArea.setEditable(false);
        consoleOutputArea.setBackground(Color.BLACK); // Make it black
        consoleOutputArea.setForeground(Color.WHITE); // White text
        consoleOutputArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Monospaced font for console look

        JScrollPane scrollPane = new JScrollPane(consoleOutputArea);
        add(scrollPane, BorderLayout.CENTER); // Add scrollable text area to the frame

        // Optional: An input field for console commands
        JTextField commandInput = new JTextField();
        commandInput.setBackground(Color.DARK_GRAY);
        commandInput.setForeground(Color.LIGHT_GRAY);
        commandInput.setCaretColor(Color.WHITE); // Make caret visible on dark background
        commandInput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        commandInput.addActionListener(e -> {
            String command = commandInput.getText();
            if (!command.trim().isEmpty()) {
                appendLog(">>> " + command); // Echo the command
                // In a real game, you'd parse and execute the command here
                System.out.println("Command received: " + command);
                if (command.equalsIgnoreCase("exitconsole")) {
                    setVisible(false); // Hide the console
                } else if (command.equalsIgnoreCase("hello")) {
                    System.out.println("Console says: Hello there!");
                }
            }
            commandInput.setText(""); // Clear input
        });
        add(commandInput, BorderLayout.SOUTH);
    }

    /**
     * Redirects System.out and System.err to this console's JTextArea.
     */
    public void setupRedirectedOutput() {
        originalOut = System.out; // Save original System.out
        originalErr = System.err; // Save original System.err

        PrintStream consolePrintStream = new PrintStream(new TextAreaOutputStream(consoleOutputArea), true); // true for auto-flush

        System.setOut(consolePrintStream);
        System.setErr(consolePrintStream);
    }

    /**
     * Restores original System.out and System.err.
     */
    public void restoreOriginalOutput() {
        if (originalOut != null) {
            System.setOut(originalOut);
        }
        if (originalErr != null) {
            System.setErr(originalErr);
        }
    }

    /**
     * Appends a log message to the console area.
     * Use System.out.println() for general logging, but this is here for direct appending.
     */
    public void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            consoleOutputArea.append(message + "\n");
            consoleOutputArea.setCaretPosition(consoleOutputArea.getDocument().getLength());
        });
    }
}
