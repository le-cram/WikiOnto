/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.triplestore.query;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JOptionPane;

/**
 *
 * @author Marcel
 */
public class QueryProcessor implements Runnable {

    private final Query query;
    private final OutputStream stream;
    private final Dataset dataset;
    
    public QueryProcessor(Query query, OutputStream stream, Dataset dataset){
        this.query = query;
        this.stream = stream;
        this.dataset = dataset;
    }
    
    @Override
    public void run() {
        dataset.begin(ReadWrite.READ);
        long time = System.currentTimeMillis();
        try (QueryExecution qe = QueryExecutionFactory.create(query, dataset)) {
            ResultSet results = qe.execSelect();
            ResultSetFormatter.outputAsCSV(stream, results);
            //ResultSetFormatter.out(stream, results, query);
        }
        time = System.currentTimeMillis() - time;
        String timeString = "\n Performed query in: "+time+"ms";
        try {
            stream.write(timeString.getBytes());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Writting to textarea failed!");
        }
        System.out.println(time);
        System.out.println("Finished query");
        dataset.end();
    }
    
}
