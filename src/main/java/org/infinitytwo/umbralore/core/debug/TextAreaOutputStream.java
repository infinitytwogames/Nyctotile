package org.infinitytwo.umbralore.core.debug;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

public class TextAreaOutputStream extends OutputStream {
    private final JTextArea textArea;
    private final StringBuilder sb = new StringBuilder();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        sb.append(c);

        // Append to JTextArea when a newline is encountered or buffer is large
        if (c == '\n' || sb.length() > 500) { // Flush on newline or large buffer
            final String textToAppend = sb.toString();
            sb.setLength(0); // Clear the buffer

            SwingUtilities.invokeLater(() -> {
                textArea.append(textToAppend);
                // Auto-scroll to the bottom
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
        }
    }

    @Override
    public void flush() throws IOException {
        // Ensure any remaining buffered text is written to the JTextArea
        if (sb.length() > 0) {
            final String textToAppend = sb.toString();
            sb.setLength(0);

            SwingUtilities.invokeLater(() -> {
                textArea.append(textToAppend);
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
        }
    }
}