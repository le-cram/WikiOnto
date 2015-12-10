package de.ist.clonto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.ist.clonto.triplestore.transform.Prune;
import de.ist.clonto.triplestore.transform.TransformationProcessor;

public class RepeatScript {

	private final Dataset dataset;

    public RepeatScript(Dataset dataset) {
        this.dataset = dataset;
    }
    
    public static void main(String[] args) {
		try {
			repeatSemDistEntity();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    /*
     * This executes redundancy removal
     */
    public static void repeat(String logfilename){
        //load dataset
        Dataset dataset = TDBFactory.createDataset("./cleanedOntology/");
        // initialise transformation processor
        TransformationProcessor tp = new TransformationProcessor(dataset);
        Map<String,String> pmap = new HashMap<>();
        
        //load log file
        
        tp.transform("deletex.sparql",pmap);
        
    }
    
    public static void repeatSemDistType() throws IOException{
    	Dataset dataset = TDBFactory.createDataset("./cleanedOntology/");
    	List<String> lines = Files.readAllLines(new File("C:/Forschung/Publikation/wikionto/ESWC/eval/old/cleaning/02SemDistType.txt").toPath());
    	for(String line : lines){
    		String sign = line.split(",")[5];
    		String name = line.split(",")[0];
    		if(sign.equals("2")){
    			System.out.println(line.replace(",,2", ", Abandon Type"));
    			new Prune(dataset).abandonType(name);
    		}
    	}
    }
    
    public static void repeatSemDistEntity() throws IOException {
    	Dataset dataset = TDBFactory.createDataset("./cleanedOntology/");
    	List<String> lines = Files.readAllLines(new File("C:/Forschung/Publikation/wikionto/ESWC/eval/old/cleaning/03SemDistEntityLog.txt").toPath());
    	Prune p = new Prune(dataset);
    	for(String line : lines){
    		String sign = line.split(",")[2].trim();
    		String name = line.split(",")[0].trim();
    		if(sign.equals("6")){
    			System.out.println(line.replace(",, 6", ", Abandon Entity"));
    			p.abandonEntity(name);
    		}if(sign.contains("Abandon Type")){
    			System.out.println(line);
    			String typename = sign.replace("Abandon Type","").replaceAll("\"", "").trim();
    			//System.out.println(typename);
    			p.abandonType(typename);
    		}if(sign.contains("Eponymous Type")){
    			
    			String typename = sign.replace("Eponymous Type","").replaceAll("\"", "").trim();
    			System.out.println(line.replace(sign, "").trim()+"Dissolve Type "+typename);
    			p.dissolveType(typename);
    		}
    	}
    }
}
