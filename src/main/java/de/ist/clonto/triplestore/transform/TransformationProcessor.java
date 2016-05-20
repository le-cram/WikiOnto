/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.triplestore.transform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.UpdateAction;

/**
 *
 * @author Marcel
 */
public class TransformationProcessor {

    private final Dataset dataset;

    public TransformationProcessor(Dataset dataset) {
        this.dataset = dataset;
    }
    
    public long transform(String tfilename, Map<String,String> parameter){
        File tfile = new File(System.getProperty("user.dir")+"/sparql/transformations/"+tfilename);
        String transformation = "";
        try {
            List<String> lines = Files.readAllLines(tfile.toPath());
            for(String line : lines){
                transformation+=line+"\n";
            }
        } catch (IOException ex) {
            System.err.println("Exception transforming:"+tfilename);;
        }
        dataset.begin(ReadWrite.WRITE);
        Graph graph = dataset.asDatasetGraph().getDefaultGraph();
        long size = graph.size();
        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(transformation);
        for(String key: parameter.keySet()){
            String query = pss.asUpdate().toString();
            if(!parameter.get(key).contains("http://")){
                pss.setLiteral(key, parameter.get(key).trim());
            }else{
                pss.setIri(key, parameter.get(key).trim());
            }
            if(query.equals(pss.asUpdate().toString())) {
                JOptionPane.showMessageDialog(null,"Querynames are flawed. This should not happen.");
                System.err.println(pss.toString());
                return 0;
            }
        }
        UpdateAction.execute(pss.asUpdate(), graph);
        size = graph.size() - size;
        dataset.commit();
        return size;
    }
    
	public Dataset getDataset() {
		return dataset;
	}

    /*
     * This executes redundancy removal
     */
    public static void main(String[] args0){
        //load dataset
        Dataset dataset;
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            dataset = TDBFactory.createDataset(fc.getSelectedFile().toString());
            TransformationProcessor tp = new TransformationProcessor(dataset);
            Map<String,String> pmap = new HashMap<>();
            tp.transform("deletex.sparql",pmap);
        }
    }



}
