package org.infinitytwo.umbralore.core.debug;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {

    private final JTextArea textArea;
    private final TerminalFrame frame;
    private final StringBuilder buffer = new StringBuilder(128); // Buffer for line output

    public TextAreaOutputStream(JTextArea textArea, TerminalFrame frame) {
        this.textArea = textArea;
        this.frame = frame;
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        buffer.append(c);

        if (c == '\n') {
            // Newline received: flush the buffer as a complete line
            final String line = buffer.toString();
            buffer.setLength(0); // Clear buffer

            // Dispatch to EDT for safe GUI update
            SwingUtilities.invokeLater(() -> {
                // Append the line to the text area
                textArea.append(line);
                // Scroll to the bottom
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
        }
    }

    // NOTE: You should also override write(byte[] b, int off, int len) for efficiency
}