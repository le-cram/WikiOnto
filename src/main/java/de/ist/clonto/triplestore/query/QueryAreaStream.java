package de.ist.clonto.triplestore.query;

import java.io.IOException;
import java.io.OutputStream;
 
import javax.swing.JTextArea;

public class QueryAreaStream extends OutputStream {
    private JTextArea textArea;
     
    public QueryAreaStream(JTextArea textArea) {
        this.textArea = textArea;
    }
     
    @Override
    public void write(int b) throws IOException {
        textArea.append(String.valueOf((char)b));
    }
}