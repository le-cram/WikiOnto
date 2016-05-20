package de.ist.clonto.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

/**
 * Created by heinz on 11.9.2015.
 * This is a helper class including functionality to speed up the evaluation process.
 * The main method opens a file chooser for a csv file and helps with the annotation of random
 * rows in it.
 */
public class RandomEval {

    public static void main(String[] args0) throws IOException {
        eval();
    }

    public static Set<Integer> genRandom(int linenumber){
        int n;
        if(linenumber>50)
            n=50;
        else
            n = linenumber-1;
        int y=linenumber-1;
        Set<Integer> numbers = new HashSet<>();
        while(numbers.size()<n) {
            int x = (int) (1+Math.floor(y * Math.random()));
            assert(x<y);
            if(!numbers.contains(x))
                numbers.add(x);
        }
        return numbers;
    }

    public static void eval() throws IOException {
        //open File Chooser
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(null);
        File csv = null;
        File newcsv=null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            csv = fc.getSelectedFile();
            newcsv = new File(csv.getPath().replace(csv.getName().split("\\.")[0], "Eval"+csv.getName().split("\\.")[0] ));
        }
        List<String> lines = FileUtils.readLines(csv);
        String[] headlines = lines.get(0).split(",");

        Set<Integer> numbers = genRandom(lines.size());
        int i=1;
        //for each random number
        //   show dialog
        //   request value
        for(int n: numbers){
            String line = lines.get(n);
            String text="";
            String[] parts = line.split(",");
            for(int j = 0;j<parts.length;j++)
                text += headlines[j] + " : " + parts[j] + "\n";
            String eval = JOptionPane.showInputDialog(null, text+"\n"+i+"/"+numbers.size());
            String newline= line+",,"+eval;
            lines.set(n,newline);
            i++;
        }
        //save in copied csv via replace
        Writer fw = new FileWriter(newcsv);
        for(String line : lines){
            fw.append(line + "\n");
        }
        fw.close();
    }

}
