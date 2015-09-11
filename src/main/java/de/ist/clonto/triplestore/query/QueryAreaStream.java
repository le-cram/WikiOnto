package de.ist.clonto.triplestore.query;

import java.io.IOException;
import java.io.OutputStream;
 
import javax.swing.JTextArea;

public class QueryAreaStream extends OutputStream {
    private final JTextArea textArea;
    private String text;
     
    public QueryAreaStream(JTextArea textArea) {
        this.textArea = textArea;
        text = "";
    }
     
    @Override
    public void write(int b) throws IOException {
        text+=String.valueOf((char)b);
    }
    
    public void showText(){
        textArea.setText(text);
    }
}