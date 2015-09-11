package de.ist.clonto;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        int n=50 + linenumber/100;
        int y=linenumber;
        Set<Integer> numbers = new HashSet<>();
        while(numbers.size()<n) {
            int x = (int) (1 + Math.floor(y * Math.random()));
            assert(x<y);
            if(!numbers.contains(x))
                numbers.add(x);
        }
        return numbers;
    }

    public static void eval() throws IOException {
        //File Chooser öffnen
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(null);
        File csv = null;
        File newcsv=null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            csv = fc.getSelectedFile();
            newcsv = new File(csv.getPath().replace(csv.getName(), csv.getName() + "new"));
        }
        List<String> lines = FileUtils.readLines(csv);
        Set<Integer> numbers = genRandom(lines.size());
        int i=1;
        for(int n: numbers){
            String line = lines.get(n);
            String text="";
            for(String part : line.split(","))
                text+=part+"\n";
            String eval = JOptionPane.showInputDialog(null, text+"\n"+i+"/"+numbers.size());
            String newline= line+",,"+eval;
            lines.set(n,newline);
            i++;
        }
        Writer fw = new FileWriter(newcsv);
        for(String line : lines){
            fw.append(line+"\n");
        }
        //Zufallszahlen generieren
        //für jede Zufallszahl
        //   Zeile anzeigen
        //   Wert angeben
        //   speichern in kopierter csv via replace
    }

    public static void repair() throws IOException {
        File newf = new File("C:\\Programmierung\\Repos\\WikiOnto\\eval\\new\\DoubleReachableEntitynew.txt");
        String fixtext = "";

        for(String line : FileUtils.readLines(newf)){
            fixtext+=line+"\n";
        }
        Pattern p = Pattern.compile("\n,,\\d+");
        Matcher m = p.matcher(fixtext);
        while (m.find()){
            String t = m.group();
            String r = t.trim()+"\n";
            fixtext.replaceAll(t,r);
        }

        Writer fw = new FileWriter(newf);
        fw.write(fixtext);
        fw.close();
    }
}
