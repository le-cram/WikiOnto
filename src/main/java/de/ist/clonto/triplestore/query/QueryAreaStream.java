package de.ist.clonto.triplestore.query;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JEditorPane;

public class QueryAreaStream extends OutputStream {
    private final JEditorPane textArea;
    private String text;
     
    public QueryAreaStream(JEditorPane textArea) {
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