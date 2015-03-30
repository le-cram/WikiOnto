/*
 * To change null license header, choose License Headers in Project Properties.
 * To change null template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.triplestore.transform;

import com.hp.hpl.jena.query.Dataset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.swing.JOptionPane;

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
                + "(Category,Entity,AttributeSet,Attribute):");
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
        String atsetname = JOptionPane.showInputDialog(null, "Insert the attributeset's name\n (index behind the # in the URI):");
        String newtopic = JOptionPane.showInputDialog(null, "Insert the new topic:");
        Map<String, String> pmap = new HashMap<>();
        pmap.put("atsetname", atsetname);
        pmap.put("newtopic", newtopic);
        long size = proc.transform("changeTopic.sparql", pmap);
        JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
    }

    void moveEntity() {
        String entityname = JOptionPane.showInputDialog(null, "Insert the entity's name:");
        String oldname = JOptionPane.showInputDialog(null, "Insert the category's name, where \n"
                + "the entity should be removed:");
        String newname = JOptionPane.showInputDialog(null, "Insert the category's name, where \n"
                + "the entity should be added:");
        if (null != entityname & null != oldname & null != newname) {
            Map<String, String> pmap = new HashMap<>();
            pmap.put("entityname", entityname);
            pmap.put("catname", oldname);
            pmap.put("newcatname", newname);
            long size = proc.transform("insertHasEntity.sparql", pmap);
            size += proc.transform("deleteHasEntity.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void moveCategory() {
        String categoryname = JOptionPane.showInputDialog(null, "Insert the category's name:");
        String oldname = JOptionPane.showInputDialog(null, "Insert the category's name, where \n"
                + "the category should be removed:");
        String newname = JOptionPane.showInputDialog(null, "Insert the category's name, where \n"
                + "the category should be added:");
        if (null != categoryname & null != oldname & null != newname) {
            Map<String, String> pmap = new HashMap<>();
            pmap.put("subcatname", categoryname);
            pmap.put("oldsupercatname", oldname);
            pmap.put("newsupercatname", newname);
            long size = proc.transform("insertHasSubCategory.sparql", pmap);
            size += proc.transform("deleteHasSubCategory.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void addMissingCategory() {
        String categoryname = JOptionPane.showInputDialog(null, "Insert the category's name:");
        String entityname = JOptionPane.showInputDialog(null, "Insert the entity's name:");
        Map<String, String> pmap = new HashMap<>();
        pmap.put("newcatname", categoryname);
        pmap.put("entityname", entityname);
        if (null != categoryname && null != entityname) {
            long size = proc.transform("insertHasEntity.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void uniteAttributesets() {
        String index = JOptionPane.showInputDialog(null, "Specify the main attributeset's index:");
        String index2 = JOptionPane.showInputDialog(null, "Specify the secondary attributeset's index:");
        Map<String, String> pmap = new HashMap<>();
        pmap.put("atset1name", index);
        pmap.put("atset2name", index2);
        pmap.put("attributeSetname", index2);
        String newname = JOptionPane.showInputDialog(null, "Specify the main attributeset's new topic:");
        pmap.put("newtopic", newname);
        pmap.put("atsetname", index);
        if (index != null && index2 != null && newname != null) {
            long size = proc.transform("moveallattributes.sparql", pmap);
            size += proc.transform("deleteHasAttributeSet.sparql", pmap);
            size += proc.transform("changeTopic.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void extractEntity() {
        String sourceentityname = JOptionPane.showInputDialog("Specify the source entity!");
        String index = JOptionPane.showInputDialog(null, "How many entities exist right now?:");
        String entityname = JOptionPane.showInputDialog(null, "Specify entity's name:");
        Map<String, String> pmap = new HashMap<>();
        pmap.put("newname", entityname);
        pmap.put("element", "clonto:Entity#" + index);
        long size = proc.transform("introduceElement.sparql", pmap);
        if (size == 0) {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
            return;
        } else {
            JOptionPane.showMessageDialog(null, "Entity created! \n Model size difference: " + size);
        }

        List<String> categories = new ArrayList<>();
        while (true) {
            String category = JOptionPane.showInputDialog(null, "Specify a category that should be added to the entity\n"
                    + "or press cancel after at least one category has been added!");
            if (category == null) {
                break;
            }
            categories.add(category);
        }
        List<String> rcategories = new ArrayList<>();
        while (true) {
            String category = JOptionPane.showInputDialog(null, "Specify a category that should be removed from the source entity"
                    + "or press cancel");
            if (category == null) {
                break;
            }
            rcategories.add(category);
        }
        String atsetname = JOptionPane.showInputDialog(null, "How many attributesets exist now?");
        String atsettopic = JOptionPane.showInputDialog(null, "Specify a topic for an attributeset\n"
                + "you want to introduce or press cancel:");
        List<String> atsetindices = new ArrayList<>();
        while (true) {
            String atsetindex = JOptionPane.showInputDialog(null, "Specify an attributeset's index that you\n "
                    + "want to move to the new entity:");
            if (atsetindex == null) {
                break;
            }
            atsetindices.add(atsetindex);
        }
        Queue<String> atmovequeue = new LinkedList();
        while (true) {
            String satset = JOptionPane.showInputDialog(null, "Specify the name of an attributeset\n"
                    + "where you want to remove an attribute:");
            String tatset = JOptionPane.showInputDialog(null, "Specify the name of an attributeset\n"
                    + "where you want to add an attribute:");
            String at = JOptionPane.showInputDialog(null, "Specify the name of an attribute that\n"
                    + "you want to move:");
            if (satset != null && tatset != null && at != null) {
                atmovequeue.add(satset);
                atmovequeue.add(tatset);
                atmovequeue.add(at);
            } else {
                break;
            }
        }

        pmap = new HashMap<>();
        for (String category : categories) {
            pmap.put("newcatname", category);
            pmap.put("entityname", entityname);
            proc.transform("insertHasEntity.sparql", pmap);
        }
        pmap = new HashMap<>();
        for (String category : rcategories) {
            pmap.put("catname", category);
            pmap.put("entityname", sourceentityname);
            proc.transform("deleteHasEntity.sparql", pmap);
        }
        pmap = new HashMap<>();
        if (atsetname != null && atsettopic != null) {
            pmap.put("element", "clonto:AttributeSet#" + atsetname);
            pmap.put("newname", atsetname);
            proc.transform("introduceElement.sparql", pmap);
            pmap = new HashMap<>();
            pmap.put("atsetname", atsetname);
            pmap.put("newtopic", atsettopic);
            pmap = new HashMap<>();
        }
        for (String matset : atsetindices) {
            pmap.put("entityname", sourceentityname);
            pmap.put("attributesetname", matset);
            proc.transform("deleteHasAttributeSet.sparql", pmap);
            pmap = new HashMap<>();
            pmap.put("entityname", entityname);
            pmap.put("attributesetname", matset);
            proc.transform("insertHasAttributeSet.sparql", pmap);
            pmap = new HashMap<>();
        }
        while (!atmovequeue.isEmpty()) {
            pmap.put("atset2name", atmovequeue.poll());
            pmap.put("atset1name", atmovequeue.poll());
            pmap.put("atname", atmovequeue.poll());
            proc.transform("moveallattributes.sparql", pmap);
            pmap = new HashMap<>();
        }
        JOptionPane.showMessageDialog(null, "Extracting Entity finished!");
    }

    void extractSubcategory() {
        Map<String, String> pmap = new HashMap<>();
        String categoryname = JOptionPane.showInputDialog(null, "Specify category's name:");
        pmap.put("newname", categoryname);
        String index = JOptionPane.showInputDialog(null, "How many categories exist right now?:");
        pmap.put("element", "clonto:Category#" + index);
        proc.transform("introduceElement.sparql", pmap);
        pmap = new HashMap<>();
        String newname = "";
        while (true) {
            newname = JOptionPane.showInputDialog(null, "Insert the category's name, where \n"
                    + "the category should be added or cancel:");
            if (newname == null) {
                break;
            }
            pmap.put("newsupercatname", newname);
            pmap.put("subcatname", categoryname);
            proc.transform("insertHasSubCategory.sparql", pmap);
        }
        pmap = new HashMap<>();
        newname = "";
        while (true) {
            newname = JOptionPane.showInputDialog(null, "Insert a subcategory's name that should be added or cancel:");
            if (newname == null) {
                break;
            }
            pmap.put("subcatname", newname);
            pmap.put("newsupercatname", categoryname);
            proc.transform("insertHasSubCategory.sparql", pmap);
        }
        pmap = new HashMap<>();
        newname = "";
        while (true) {
            newname = JOptionPane.showInputDialog(null, "Insert an entity's name that should be added or cancel:");
            if (newname == null) {
                break;
            }
            pmap.put("newcatname", categoryname);
            pmap.put("entityname", newname);
            proc.transform("insertHasEntity.sparql", pmap);
        }
    }

    void removeRedundantHasEntity() {
        String entityname = JOptionPane.showInputDialog(null, "Name the entity that corresponds to the target of the relation:");
        if (null != entityname) {
            String name2 = JOptionPane.showInputDialog("Name the category that corresponds to the source of the relation:");
            if (null != name2) {
                Map<String, String> pmap = new HashMap<>();
                pmap.put("catname", name2);
                pmap.put("entityname", entityname);
                long size = proc.transform("deleteHasEntity.sparql", pmap);
                JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
            } else {
                JOptionPane.showMessageDialog(null, "Transformation failed!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void removeRedundantHasSucategory() {
        String entityname = JOptionPane.showInputDialog(null, "Name the containing category:");
        if (null != entityname) {
            String name2 = JOptionPane.showInputDialog("Name the subcategory that should be removed from the category:");
            if (null != name2) {
                Map<String, String> pmap = new HashMap<>();
                pmap.put("subcatname", name2);
                pmap.put("oldsupercatname", entityname);
                long size = proc.transform("deleteHasSubCategory.sparql", pmap);
                JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
            } else {
                JOptionPane.showMessageDialog(null, "Transformation failed!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
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
            case "Move Category":
                moveCategory();
                break;
            case "Add Missing Category":
                addMissingCategory();
                break;
            case "Unite Attributesets":
                uniteAttributesets();
                break;
            case "Extract Entity":
                extractEntity();
                break;
            case "Extract Subcategory":
                extractSubcategory();
                break;
            case "Remove Redundant HasEntity":
                removeRedundantHasEntity();
                break;
            case "Remove Redundant SubCategory":
                removeRedundantHasSucategory();
                break;
        }
    }
}
