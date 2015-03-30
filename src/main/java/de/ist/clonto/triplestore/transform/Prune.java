/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.triplestore.transform;

import com.hp.hpl.jena.query.Dataset;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author Marcel
 */
public class Prune {

    private final TransformationProcessor proc;

    public Prune(Dataset dataset) {
        this.proc = new TransformationProcessor(dataset);
    }

    void abandonEntity() {
        String name = JOptionPane.showInputDialog("Name the entity that should be deleted:");
        if (null != name) {
            Map<String, String> pmap = new HashMap<>();
            pmap.put("name", name);
            long size = proc.transform("abandonEntity.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void abandonCategory() {
        String name = JOptionPane.showInputDialog("Name the category that should be deleted:");
        if (null != name) {
            Map<String, String> pmap = new HashMap<>();
            pmap.put("name", name);
            long size = proc.transform("abandonCategory.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void cleanUpUnreachable() {
        Map<String, String> pmap = new HashMap<>();
        long tsize = 0;
        long size = 1;
        while (size != 0) {
            size = proc.transform("cleanUpCategories.sparql", pmap);
            System.out.println("cleaned up categories: " + size);
            size += proc.transform("cleanUpEntities.sparql", pmap);
            System.out.println("cleaned up entities: " + size);
            size += proc.transform("cleanUpAttributeSets.sparql", pmap);
            System.out.println("cleaned up attributesets: " + size);
            size += proc.transform("cleanUpAttributes.sparql", pmap);
            System.out.println("cleaned up attributes: " + size);
            tsize += size;
        }

        JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + tsize);
    }

    void removeHasEntity() {
        String name = JOptionPane.showInputDialog("Name the entity that corresponds to the target of the relation:");
        if (null != name) {
            String name2 = JOptionPane.showInputDialog("Name the category that corresponds to the source of the relation:");
            if (null != name2) {
                Map<String, String> pmap = new HashMap<>();
                pmap.put("catname", name2);
                pmap.put("entityname", name);
                long size = proc.transform("deleteHasEntity.sparql", pmap);
                JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
            } else {
                JOptionPane.showMessageDialog(null, "Transformation failed!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void removeHasSubcategory() {
        String name = JOptionPane.showInputDialog("Name the containing category:");
        if (null != name) {
            String name2 = JOptionPane.showInputDialog("Name the subcategory that should be removed from the category:");
            if (null != name2) {
                Map<String, String> pmap = new HashMap<>();
                pmap.put("subcatname", name2);
                pmap.put("oldsupercatname", name);
                long size = proc.transform("deleteHasSubCategory.sparql", pmap);
                JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
            } else {
                JOptionPane.showMessageDialog(null, "Transformation failed!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void collapseHierarchy() {
        String name = JOptionPane.showInputDialog("Name the dissolvable category:");
        if (null != name) {
            Map<String, String> pmap = new HashMap<>();
            pmap.put("oldcatname", name);
            long size = proc.transform("dissolveCategory.sparql", pmap);
            pmap.put("name", name);
            size += proc.transform("abandonCategory.sparql", pmap);
            JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    void liftCycle() {
        String name = JOptionPane.showInputDialog("Name the containing category:");
        if (null != name) {
            String name2 = JOptionPane.showInputDialog("Name the subcategory that should be removed from the category:");
            if (null != name2) {
                Map<String, String> pmap = new HashMap<>();
                pmap.put("subcatname", name2);
                pmap.put("oldsupercatname", name);
                long size = proc.transform("deleteHasSubCategory.sparql", pmap);
                JOptionPane.showMessageDialog(null, "Transformation successful! \n Model size difference: " + size);
            } else {
                JOptionPane.showMessageDialog(null, "Transformation failed!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Transformation failed!");
        }
    }

    public void execute(String pruneName) {
        switch (pruneName) {
            case "Abandon Entity":
                abandonEntity();
                break;
            case "Abandon Category":
                abandonCategory();
                break;
            case "Cleanup Unreachable":
                cleanUpUnreachable();
                break;
            case "Remove HasEntity":
                removeHasEntity();
                break;
            case "Remove Subcategory":
                removeHasSubcategory();
                break;
            case "Collapse Hierarchy":
                collapseHierarchy();
                break;
            case "Lift Cycle":
                liftCycle();
                break;
        }
    }
}
