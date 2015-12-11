package de.ist.clonto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.ist.clonto.triplestore.transform.Prune;
import de.ist.clonto.triplestore.transform.Refactor;
import de.ist.clonto.triplestore.transform.TransformationProcessor;

/**
 * 
 * @author heinz
 *
 * Deprecated repeater of old log files
 */
public class RepeatScript {

	private final Dataset dataset;

    public RepeatScript(Dataset dataset) {
        this.dataset = dataset;
    }
    
    public static void main(String[] args) {
		try {
			repeatDoubleReachableEntity();
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
    
    public static void repeatDoubleReachableEntity() throws IOException{
    	Dataset dataset = TDBFactory.createDataset("./cleanedOntology/");
    	List<String> lines = Files.readAllLines(new File("C:/Forschung/Publikation/wikionto/ESWC/eval/old/cleaning/05.2DoubleReachableEntityLog.txt").toPath());
    	Prune p = new Prune(dataset);
    	Refactor r = new Refactor(dataset);
    	for(int j = 0;j<lines.size();j++){
    		String line = lines.get(j);
    		String iname = line.split(",")[0].trim();
    		//solange beginn mit spaces, lese nächste Zeile und führe Zeile aus.
    		while(j+1<lines.size()&&lines.get(j+1).startsWith(" ")){
    			j++;
    			String cmd = lines.get(j);
    			if(cmd.contains("6")){
    				System.out.println(iname+", Abandon Entity");
    				p.abandonEntity(iname);
    				continue;
    			}
    			String sign = cmd.split("->")[0].trim();
    			String[] tnames = cmd.split("->")[1].split(",");
    			if(sign.contains("9")){
    				for(String t : tnames){
    					System.out.println(iname+" , Remove instance from '"+t+"'");
    					p.removeInstance(iname, t.trim());
    				}
    			}
    			if(sign.contains("7")){
    				System.out.println(iname+", Remove subtype, where '"+tnames[0]+"' is subtype of '"+tnames[1]+"'");
    				p.removeSubtype(tnames[1].trim(), tnames[0].trim());
    			}
    			if(sign.contains("8")){
    				if(tnames.length==1){
    					System.out.println(iname+", Add Missing Instance to '"+tnames[0]+"'");
    					r.addMissingInstance(tnames[0].trim(), iname);
    				}else{
    					System.out.println(iname+", Add Missing Subtype '"+tnames[0]+"' to '"+tnames[1]+"'");
    					r.addMissingSubtype(tnames[1].trim(), tnames[0].trim());
    				}
    			}
    		}
    	}
    }
}
