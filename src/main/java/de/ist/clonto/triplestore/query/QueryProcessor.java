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
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
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
    private final boolean pretty;
    
    public QueryProcessor(Query query, OutputStream stream, Dataset dataset, boolean pretty){
        this.query = query;
        this.stream = stream;
        this.dataset = dataset;
        this.pretty = pretty;
    }
    
    
    @Override
    public void run() {
        dataset.begin(ReadWrite.READ);
        System.out.println("------------------");
        System.out.println(query);
        Op op = Algebra.compile(query);
        op = Algebra.optimize(op);
        System.out.println(op);
        System.out.println("------------------");
        System.out.println(query);
        long time = System.currentTimeMillis();
        try (QueryExecution qe = QueryExecutionFactory.create(query, dataset)) {
            ResultSet results = qe.execSelect();
            if(pretty)
                ResultSetFormatter.out(stream, results, query);
            else{
                System.out.println("Output as CSV");
                ResultSetFormatter.outputAsCSV(stream, results);
            }
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
