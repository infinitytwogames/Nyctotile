package org.infinitytwo.umbralore.core.debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.util.function.Function;

public class TerminalFrame extends JFrame {

    private JTextArea textArea;
    private final String PROMPT = ">> ";
    private int inputStart = 0; // Tracks the start position of the user's current input line
    private Function<String, Boolean> runnable;

    public TerminalFrame() {
        super("Simple Java Terminal");

        // --- 1. Setup the JTextArea (The 'Screen') ---
        textArea = new JTextArea();
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.GREEN);
        textArea.setCaretColor(Color.WHITE); // Blinking cursor color
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Wrap the JTextArea in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);

        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 400);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        // --- 2. Redirect System.out and initial prompt ---
        redirectSystemStreams();

        // Initial prompt
//        System.out.print(PROMPT);
//        inputStart = textArea.getDocument().getLength();

        // --- 3. Handle Keyboard Input ---
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume(); // Consume the 'Enter' event to prevent a newline character from appearing immediately
                    processCommand();
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    // Prevent deleting the prompt
                    if (textArea.getCaretPosition() <= inputStart) {
                        e.consume();
                    }
                }
            }
        });

        // Ensure the JTextArea gets focus so the user can type right away
        textArea.requestFocusInWindow();
    }

    // Inside TerminalFrame.java (Updated processCommand)
    // Inside TerminalFrame.java (Updated processCommand)
    private void processCommand() {
        String commandLine = "";
        try {
            int end = textArea.getDocument().getLength();
            String fullText = textArea.getText();

            // 1. Extract the raw input line from the user
            commandLine = fullText.substring(inputStart, end);

            // 2. Remove the input from the screen and print the executed command line
            // This is crucial: It clears the input area and prints the command as history.
            textArea.replaceRange("", inputStart, end);
            textArea.append(commandLine + "\n");

            // Clean and process the command line
            String processedCommand = commandLine.trim();
            if (processedCommand.startsWith(PROMPT.trim())) {
                processedCommand = processedCommand.substring(PROMPT.trim().length()).trim();
            }

            // 3. Command Execution: Use a separate thread for the command handler!
            // This prevents network output from blocking the GUI thread and corrupting the input flow.
            String finalCommand = processedCommand;
            new Thread(() -> {
                try {
                    if (finalCommand.equalsIgnoreCase("exit")) {
                        System.out.println("Exiting application...");
                        System.exit(0);
                    } else if (runnable != null && !finalCommand.isEmpty()) {
                        if (!runnable.apply(finalCommand)) {
                            System.out.println("Command not recognized: " + finalCommand);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error during command execution: " + e.getMessage());
                } finally {
                    // 4. Reset prompt ONLY after the command and all its output are done
                    resetPrompt();
                }
            }).start();

            // **DO NOT put resetPrompt() here.** It must wait for the command thread.
            // The original method now returns immediately after starting the command thread.

        } catch (Exception ex) {
            System.out.println("Fatal error during input processing: " + ex.getMessage());
            resetPrompt();
        }
    }

    // Inside TerminalFrame.java
    /**
     * Custom print method that handles output correctly without corrupting the
     * currently active input line. Output is always followed by a newline.
     */
    public void print(String s) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(s + "\n");
            // Scroll to the bottom
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }

    private void redirectSystemStreams() {
        // Redirect System.out to the JTextArea
        PrintStream standardOut = new PrintStream(new TextAreaOutputStream(textArea,this));
        System.setOut(standardOut);

        // You can also redirect System.err if needed (e.g., using a red color for errors)
        // System.setErr(standardOut);
    }

    // Inside TerminalFrame.java (Updated resetPrompt)
    public void resetPrompt() {
        SwingUtilities.invokeLater(() -> {
            // Force a newline if the last text in the JTextArea wasn't a newline
            if (!textArea.getText().endsWith("\n")) {
                textArea.append("\n");
            }

            // Print the prompt and set input start
            textArea.append(PROMPT);

            // CRITICALLY: Update inputStart to the current end of the document
            inputStart = textArea.getDocument().getLength();

            // Scroll and focus
            textArea.setCaretPosition(inputStart);
            textArea.requestFocusInWindow();
        });
    }

    public void setCommandHandler(Function<String, Boolean> r) {
        runnable = r;
    }
}
