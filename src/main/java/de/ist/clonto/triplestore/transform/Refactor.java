/*
 * To change null license header, choose License Headers in Project Properties.
 * To change null template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.triplestore.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.swing.JOptionPane;

import com.hp.hpl.jena.query.Dataset;

/**
 *
 * @author Marcel
 */
public class Refactor {

    private final TransformationProcessor proc;

    public Refactor(Dataset dataset) {
        this.proc = new TransformationProcessor(dataset);
    }

    void renameElement() {
        String type = JOptionPane.showInputDialog(null, "Specifiy the element's type \n"
                + "(Type,Entity,Information,Property):");
        String oldname = JOptionPane.showInputDialog(null, "Insert the current element's name:");
        String newname = JOptionPane.showInputDialog(null, "Insert the new name:");
        if (null != type & null != oldname & null != newname) {
            Map<String, String> pmap = new HashMap<>();
            pmap.put("type", type);
            pmap.put("oldname", oldname);
            pmap.put("newname", newname);
            long size = proc.transform("renameElement.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void changeTopic() {
        String atsetname = JOptionPane.showInputDialog(null, "Insert the information's name:");
        String newtopic = JOptionPane.showInputDialog(null, "Insert the new topic:");
        if(atsetname!=null && newtopic!=null) {

            Map<String, String> pmap = new HashMap<>();
            pmap.put("infoname", atsetname);
            pmap.put("newtopic", newtopic);
            long size = proc.transform("changeTopic.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        }else
            JOptionPane.showMessageDialog(null, "Transformation failed!");
    }

    void moveEntity() {
        String entityname = JOptionPane.showInputDialog(null, "Insert the entity's name:");
        String oldname = JOptionPane.showInputDialog(null, "Insert the type's name, where \n"
                + "the entity should be removed:");
        String newname = JOptionPane.showInputDialog(null, "Insert the type's name, where \n"
                + "the entity should be added:");
        if (null != entityname & null != oldname & null != newname) {
            Map<String, String> pmap = new HashMap<>();
            pmap.put("entityname", entityname);
            pmap.put("typename", oldname);
            pmap.put("newtypename", newname);
            long size = proc.transform("insertHasInstance.sparql", pmap);
            size += proc.transform("deleteHasInstance.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void moveType() {
        String subtypename = JOptionPane.showInputDialog(null, "Insert the type's name:");
        String oldname = JOptionPane.showInputDialog(null, "Insert the type's name, where \n"
                + "the subtype should be removed:");
        String newname = JOptionPane.showInputDialog(null, "Insert the type's name, where \n"
                + "the subtype should be added:");
        if (null != subtypename & null != oldname & null != newname) {
            Map<String, String> pmap = new HashMap<>();
            pmap.put("subtypename", subtypename);
            pmap.put("oldsupertypename", oldname);
            pmap.put("newsupertypename", newname);
            long size = proc.transform("insertHasSubtype.sparql", pmap);
            size += proc.transform("deleteHasSubtype.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    public void addMissingInstance(String tname, String ename) {
    	String typename = "";
    	String entityname = "";
    	if(tname==null&&ename==null){
    		typename = JOptionPane.showInputDialog(null, "Insert the type's name:");
            entityname = JOptionPane.showInputDialog(null, "Insert the entity's name:");
    	}else{
    		typename = tname;
    		entityname = ename;
    	}
        
        Map<String, String> pmap = new HashMap<>();
        pmap.put("newtypename", typename);
        pmap.put("entityname", entityname);
        if (null != typename && null != entityname) {
            long size = proc.transform("insertHasInstance.sparql", pmap);
            if(tname==null&&ename==null)
            	JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    public void addMissingSubtype(String supname, String subname){
    	String typename = "";
    	String subtypename = "";
    	if(supname==null&&subname == null){
    		typename = JOptionPane.showInputDialog(null, "Insert the supertype's name:");
            subtypename = JOptionPane.showInputDialog(null, "Insert the subtype's name:");
    	}else{
    		typename = supname;
    		subtypename = subname;
    	}
        Map<String, String> pmap = new HashMap<>();
        pmap.put("newsupertypename", typename);
        pmap.put("subtypename", subtypename);
        if (null != typename && null != subtypename) {
            long size = proc.transform("insertHasSubtype.sparql", pmap);
            if(supname==null&&subname == null)
            	JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void uniteInformation() {
        JOptionPane.showMessageDialog(null,"Please look up all information ids!");
        String index = JOptionPane.showInputDialog(null, "Specify the main information's id:");
        String index2 = JOptionPane.showInputDialog(null, "Specify the secondary information's id:");
        String newname = JOptionPane.showInputDialog(null, "Specify the main information's new topic:");
        Map<String, String> pmap = new HashMap<>();

        if (index != null && index2 != null && newname != null) {
            pmap.put("sinfoname", index2);
            pmap.put("tinfoname", index);
            long size = proc.transform("moveProperties.sparql", pmap);
            pmap.clear();
            pmap.put("informationname", index);
            size += proc.transform("deleteHasInformation.sparql", pmap);
            pmap.clear();
            pmap.put("newtopic",newname);
            size += proc.transform("changeTopic.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void extractEntity() {
        String sourceentityname = JOptionPane.showInputDialog(null,"Specify the source entity!");
        long index = System.nanoTime();
        String entityname = JOptionPane.showInputDialog(null, "Specify entity's name:");
        Map<String, String> pmap = new HashMap<>();
        pmap.put("newname", entityname);
        pmap.put("element", "http://myCLOnto.de/Entity#" + index);
        long size = proc.transform("introduceElement.sparql", pmap);
        if (size == 0) {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
            return;
        } else {
            JOptionPane.showMessageDialog(null, "Entity created! \n Model size difference: " + size);
        }

        List<String> types = new ArrayList<>();
        while (true) {
            String type = JOptionPane.showInputDialog(null, "Specify a type that should be added to the entity\n"
                    + "or press cancel after at least one type has been added!");
            if (type == null) {
                break;
            }
            types.add(type);
        }
        List<String> rtypes = new ArrayList<>();
        while (true) {
            String type = JOptionPane.showInputDialog(null, "Specify a type that should be removed from the source entity"
                    + "or press cancel");
            if (type == null) {
                break;
            }
            rtypes.add(type);
        }

        long infoname = System.nanoTime();
        String infotopic = JOptionPane.showInputDialog(null, "Specify a topic for an information collection that\n"
                + "you want to introduce or press cancel:");
        List<String> infoindices = new ArrayList<>();
        while (true) {
            String infoindex = JOptionPane.showInputDialog(null, "Specify an information's id that you\n "
                    + "want to move to the new entity:");
            if (infoindex == null) {
                break;
            }
            infoindices.add(infoindex);
        }
        Queue<String> propmovequeue = new LinkedList<>();
        while (true) {
            String sinfo = JOptionPane.showInputDialog(null, "Specify the information id\n"
                    + "where you want to remove a property:");
            String tinfo = JOptionPane.showInputDialog(null, "Specify the information id\n"
                    + "where you want to add a property:");
            String at = JOptionPane.showInputDialog(null, "Specify the name of a property that\n"
                    + "you want to move:");
            if (sinfo != null && tinfo != null && at != null) {
                propmovequeue.add(sinfo);
                propmovequeue.add(tinfo);
                propmovequeue.add(at);
            } else {
                break;
            }
        }

        pmap = new HashMap<>();
        for (String type : types) {
            pmap.put("newtypename", type);
            pmap.put("entityname", entityname);
            proc.transform("insertHasInstance.sparql", pmap);
        }
        pmap = new HashMap<>();
        for (String category : rtypes) {
            pmap.put("typename", category);
            pmap.put("entityname", sourceentityname);
            proc.transform("deleteHasInstance.sparql", pmap);
        }
        pmap = new HashMap<>();
        if (infotopic != null) {
            pmap.put("element", ":Information#" + infoname);
            pmap.put("newname", String.valueOf(infoname));
            proc.transform("introduceElement.sparql", pmap);
            pmap = new HashMap<>();
            pmap.put("infoname", String.valueOf(infoname));
            pmap.put("newtopic", infotopic);
            pmap = new HashMap<>();
        }
        for (String matset : infoindices) {
            pmap.put("entityname", sourceentityname);
            pmap.put("informationname", matset);
            proc.transform("deleteHasInformation.sparql", pmap);
            pmap = new HashMap<>();
            pmap.put("entityname", entityname);
            pmap.put("informationname", matset);
            proc.transform("insertHasInformation.sparql", pmap);
            pmap = new HashMap<>();
        }
        while (!propmovequeue.isEmpty()) {
            String info2name = propmovequeue.poll();
            String info1name = propmovequeue.poll();
            String propname = propmovequeue.poll();


            pmap.put("infoname", info2name);
            pmap.put("propname", propname);
            proc.transform("deleteHasProperty.sparql", pmap);
            pmap.put("infoname", info1name);
            proc.transform("insertHasProperty.sparql", pmap);
            pmap = new HashMap<>();
        }
        JOptionPane.showMessageDialog(null, "Extracting Entity finished!");
    }

    void removeRedundantInstances() {
    	HashMap<String, String> pmap = new HashMap<>();
        long size = proc.transform("removeRedundantInstances.sparql", pmap);
        JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
    }

    void removeRedundantSubtypes() {
    	HashMap<String, String> pmap = new HashMap<>();
        long size = proc.transform("removeRedundantSubtypes.sparql", pmap);
        JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
    }

    public void execute(String refactorName) {
        switch (refactorName) {
            case "Rename Element":
                renameElement();
                break;
            case "Change Topic":
                changeTopic();
                break;
            case "Move Entity":
                moveEntity();
                break;
            case "Move Type":
                moveType();
                break;
            case "Add Missing Instance":
                addMissingInstance(null,null);
                break;
            case "Add Missing Subtype":
                addMissingSubtype(null,null);
                break;
            case "Unite Information":
                uniteInformation();
                break;
            case "Extract Entity":
                extractEntity();
                break;
            case "Remove Redundant Instances":
                removeRedundantInstances();
                break;
            case "Remove Redundant Subtypes":
                removeRedundantSubtypes();
                break;
        }
    }
}
