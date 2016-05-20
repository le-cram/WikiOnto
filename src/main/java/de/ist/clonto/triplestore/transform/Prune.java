/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.triplestore.transform;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import com.hp.hpl.jena.query.Dataset;

import de.ist.clonto.DissolveGUI;

/**
 *
 * @author Marcel
 */
public class Prune {

	private final TransformationProcessor proc;

	public Prune(Dataset dataset) {
		this.proc = new TransformationProcessor(dataset);
	}

	public void abandonEntity(String n) {
		String name = "";
		if (n != null)
			name = n;
		else
			name = JOptionPane.showInputDialog(null,
					"Name the entity that should be deleted:");
		if (null != name) {
			Map<String, String> pmap = new HashMap<>();
			pmap.put("name", name);
			long size = proc.transform("abandonEntity.sparql", pmap);
			if (n == null)
				JOptionPane.showMessageDialog(null,
						"Transformation successful! \n Model size difference: "
								+ size);
		} else {
			JOptionPane.showMessageDialog(null, "Transformation failed! "
					+ name);
		}
	}

	public void abandonType(String n) {
		String name = "";
		if (n != null)
			name = n;
		else
			name = JOptionPane.showInputDialog(null,
					"Name the type that should be deleted:");
		if (null != name) {
			Map<String, String> pmap = new HashMap<>();
			pmap.put("name", name);
			long size = proc.transform("abandonType.sparql", pmap);
			if (n == null)
				JOptionPane.showMessageDialog(null,
						"Transformation successful! \n Model size difference: "
								+ size);
		} else {
			JOptionPane.showMessageDialog(null, "Transformation failed! "
					+ name);
		}
	}

	void cleanUpUnreachableAll() {
		Map<String, String> pmap = new HashMap<>();
		long tsize = 0;
		long size = 1;
		while (size != 0) {
			size = proc.transform("cleanUpTypes.sparql", pmap);
			System.out.println("cleaned up types: " + size);
			size += proc.transform("cleanUpEntities.sparql", pmap);
			System.out.println("cleaned up entities: " + size);
			size += proc.transform("cleanUpInformation.sparql", pmap);
			System.out.println("cleaned up information: " + size);
			size += proc.transform("cleanUpProperties.sparql", pmap);
			System.out.println("cleaned up properties: " + size);
			tsize += size;
		}

		JOptionPane
				.showMessageDialog(null,
						"Transformation successful! \n Model size difference: "
								+ tsize);
	}

	void cleanUpUnreachableType() {
		Map<String, String> pmap = new HashMap<>();
		long tsize = 0;
		long size = 1;
		while (size != 0) {
			size = proc.transform("cleanUpTypes.sparql", pmap);
			System.out.println("cleaned up types: " + size);
			tsize += size;
		}

		JOptionPane
				.showMessageDialog(null,
						"Transformation successful! \n Model size difference: "
								+ tsize);
	}

	void cleanUpUnreachableEnt() {
		Map<String, String> pmap = new HashMap<>();
		long tsize = 0;
		long size = 1;
		while (size != 0) {
			size += proc.transform("cleanUpEntities.sparql", pmap);
			System.out.println("cleaned up entities: " + size);
			tsize += size;
		}

		JOptionPane
				.showMessageDialog(null,
						"Transformation successful! \n Model size difference: "
								+ tsize);
	}

	public void removeInstance(String ename, String tname) {
		String namee = "";
		String namet = "";
		if (ename == null || tname == null) {
			namee = JOptionPane.showInputDialog("Name the entity:");
			namet = JOptionPane.showInputDialog("Name the type:");
		} else {
			namee = ename;
			namet = tname;
		}
		if (null != namee && null != namet) {
			Map<String, String> pmap = new HashMap<>();
			pmap.put("typename", namet);
			pmap.put("entityname", namee);
			long size = proc.transform("deleteHasInstance.sparql", pmap);
			if (ename == null && tname == null)
				JOptionPane.showMessageDialog(null,
						"Transformation successful! \n Model size difference: "
								+ size);

		} else {
			JOptionPane.showMessageDialog(null, "Transformation failed!");
		}
	}

	public void removeSubtype(String sup, String sub) {
		String supname = "";
		String subname = "";
		if (sup == null || sub == null) {
			supname = JOptionPane.showInputDialog(null, "Name the supertype:");
			subname = JOptionPane.showInputDialog(null,
					"Name the subtype that should be removed from the type:");
		}
		if (supname != null && subname != null) {
			Map<String, String> pmap = new HashMap<>();
			pmap.put("subtypename", subname);
			pmap.put("oldsupertypename", supname);
			long size = proc.transform("deleteHasSubtype.sparql", pmap);
			if (sup == null && sub == null)
				JOptionPane.showMessageDialog(null,
						"Transformation successful! \n Model size difference: "
								+ size);
		} else {
			JOptionPane.showMessageDialog(null, "Transformation failed!");
		}
	}

	public void abandonTypeRescueAll(String n) {
		String name = "";
		if (n != null)
			name = n;
		else
			name = JOptionPane.showInputDialog(null,
					"Name the dissolvable type:");
		if (null != name) {
			Map<String, String> pmap = new HashMap<>();
			pmap.put("oldtypename", name);
			long size = proc.transform("collapseType.sparql", pmap);
			pmap.clear();
			pmap.put("name", name);
			size += proc.transform("abandonType.sparql", pmap);
			if (n == null)
				JOptionPane.showMessageDialog(null,
						"Transformation successful! \n Model size difference: "
								+ size);
		} else {
			JOptionPane
					.showMessageDialog(null, "Transformation failed!" + name);
		}
	}

	void liftCycle() {
		String name = JOptionPane.showInputDialog(null, "Name the supertype:");
		if (null != name) {
			String name2 = JOptionPane.showInputDialog(null,
					"Name the subtype that should be removed:");
			if (null != name2) {
				Map<String, String> pmap = new HashMap<>();
				pmap.put("subtypename", name2);
				pmap.put("oldsupertypename", name);
				long size = proc.transform("deleteHasSubtype.sparql", pmap);
				JOptionPane.showMessageDialog(null,
						"Transformation successful! \n Model size difference: "
								+ size);
			} else {
				JOptionPane.showMessageDialog(null, "Transformation failed!");
			}
		} else {
			JOptionPane.showMessageDialog(null, "Transformation failed!");
		}
	}

	private void abandonInformation() {
		String name = JOptionPane.showInputDialog(null,
				"Name the information id that should be deleted:");
		if (null != name) {
			Map<String, String> pmap = new HashMap<>();
			pmap.put("informationname", name);
			long size = proc.transform("deleteHasInformation.sparql", pmap);
			JOptionPane.showMessageDialog(null,
					"Transformation successful! \n Model size difference: "
							+ size);
		} else {
			JOptionPane.showMessageDialog(null, "Transformation failed!");
		}
	}

	private void custom() {
		Map<String, String> pmap = new HashMap<>();
		long size = proc.transform("custom.sparql", pmap);
		JOptionPane.showMessageDialog(null,
				"Transformation successful! \n Model size difference: " + size);
	}

	public void abandonTypeSelectiveRescue(String tname) {
		String name = "";
		if(tname == null)
			 name = JOptionPane.showInputDialog(null,
				"Name the type you want to dissolve:");
		else
			name = tname;
		new DissolveGUI(proc.getDataset(), name).setVisible(true);
		;
	}

	public void execute(String pruneName) {
		switch (pruneName) {
		case "Abandon Type (selective)":
			abandonTypeSelectiveRescue(null);
			break;
		case "Abandon Entity":
			abandonEntity(null);
			break;
		case "Abandon Type (no rescue)":
			abandonType(null);
			break;
		case "Abandon Information":
			abandonInformation();
			break;
		case "Cleanup Unreachable All":
			cleanUpUnreachableAll();
			break;
		case "Cleanup Unreachable Type":
			cleanUpUnreachableType();
			break;
		case "Cleanup Unreachable Ent":
			cleanUpUnreachableEnt();
			break;
		case "Remove Instance":
			removeInstance(null, null);
			break;
		case "Remove Subtype":
			removeSubtype(null, null);
			break;
		case "Abandon Type (rescue all)":
			abandonTypeRescueAll(null);
			break;
		case "Lift Cycle":
			liftCycle();
			break;
		case "Custom":
			custom();
			break;
		}
	}
}
