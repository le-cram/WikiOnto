package de.ist.clonto;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.ist.clonto.triplestore.query.QueryProcessor;
import de.ist.clonto.triplestore.transform.Prune;

public class SemDistGUI extends JFrame {

	private Dataset data;
	private JToggleButton[] ibuttons;
	private JToggleButton[] sbuttons;
	private String[] instances;
	private String[] subtypes;
	private JToggleButton[] ributtons;
	private JToggleButton[] rsbuttons;

	public SemDistGUI(Dataset data) {
		this.data = data;
		getDistantEntities();
		getDistantTypes();
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		displayMembers();
	}

	private void getDistantEntities(){
		File metricFile = new File(
				System.getProperty("user.dir") + "/sparql/smells/SemanticallyDistantEntity.sparql");

		List<String> lines = null;

		try {
			lines = Files.readAllLines(metricFile.toPath());
		} catch (IOException ex) {
			Logger.getLogger(Ontogui.class.getName()).log(Level.SEVERE, null, ex);
		}
		String queryString = "";
		for (String line : lines) {
			queryString += line + System.lineSeparator();
		}
		
		data.begin(ReadWrite.READ);
		List<QuerySolution> rlist = null;
		Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		try (QueryExecution qe = QueryExecutionFactory.create(query, data)) {
			ResultSet results = qe.execSelect();
			rlist = ResultSetFormatter.toList(results);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Writting to textarea failed!");
			e.printStackTrace();
		}
		instances = new String[rlist.size()];
		for(int j = 0; j < rlist.size(); j++){
			instances[j] = rlist.get(j).getLiteral("entityname").getString();
		}
		data.end();
	}
	
	private void getDistantTypes(){
		File metricFile = new File(
				System.getProperty("user.dir") + "/sparql/smells/SemanticallyDistantType.sparql");

		List<String> lines = null;

		try {
			lines = Files.readAllLines(metricFile.toPath());
		} catch (IOException ex) {
			Logger.getLogger(Ontogui.class.getName()).log(Level.SEVERE, null, ex);
		}
		String queryString = "";
		for (String line : lines) {
			queryString += line + System.lineSeparator();
		}
		
		data.begin(ReadWrite.READ);
		List<QuerySolution> rlist = null;
		Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		try (QueryExecution qe = QueryExecutionFactory.create(query, data)) {
			ResultSet results = qe.execSelect();
			rlist = ResultSetFormatter.toList(results);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Writting to textarea failed!");
			e.printStackTrace();
		}
		subtypes = new String[rlist.size()];
		for(int j = 0; j < rlist.size(); j++){
			subtypes[j] = rlist.get(j).getLiteral("typename").getString();
		}
		data.end();
	}

	private void displayMembers() {

		JScrollPane scrollInstances = new javax.swing.JScrollPane();
		scrollInstances.setBorder(javax.swing.BorderFactory.createTitledBorder("Instances"));
		JScrollPane scrollSubtypes = new javax.swing.JScrollPane();
		scrollSubtypes.setBorder(javax.swing.BorderFactory.createTitledBorder("Subtypes"));

		JPanel paneinstances = new javax.swing.JPanel();
		paneinstances.setLayout(new java.awt.GridBagLayout());
		JPanel panesubtypes = new javax.swing.JPanel();
		panesubtypes.setLayout(new java.awt.GridBagLayout());
		scrollInstances.setViewportView(paneinstances);
		scrollSubtypes.setViewportView(panesubtypes);
		ibuttons = new JToggleButton[instances.length];
		ributtons = new JToggleButton[instances.length];
		for (int j = 0; j < instances.length; j++) {
			GridBagConstraints ci = new GridBagConstraints();
			ci.fill = GridBagConstraints.HORIZONTAL;
			ci.gridx = 0;
			ci.gridy = j;
			JLabel label = new javax.swing.JLabel();
			label.setText(instances[j]);
			paneinstances.add(label, ci);
			JToggleButton tbutton = new javax.swing.JToggleButton("Aba");
			tbutton.setToolTipText("The member is abandoned, if this button is toggled.");
			ibuttons[j] = tbutton;
			ci.gridx = 1;
			ci.insets = new Insets(10, 10, 10, 10);
			paneinstances.add(tbutton, ci);
			JButton helpbutton = new JButton("?");
			helpbutton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						java.awt.Desktop.getDesktop()
								.browse(new URI("https://en.wikipedia.org/wiki/" + label.getText().replaceAll(" ", "_")));
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			});
			ci.gridx = 2;
			paneinstances.add(helpbutton, ci);
		}
		sbuttons = new JToggleButton[subtypes.length];
		rsbuttons = new JToggleButton[subtypes.length];
		for (int j = 0; j < subtypes.length; j++) {
			GridBagConstraints ci = new GridBagConstraints();
			ci.fill = GridBagConstraints.HORIZONTAL;
			ci.gridx = 0;
			ci.gridy = j;
			JLabel label = new javax.swing.JLabel();
			label.setText(subtypes[j]);
			panesubtypes.add(label, ci);
			JToggleButton tbutton = new javax.swing.JToggleButton("Aba");
			tbutton.setToolTipText("The member is abandoned, if this button is toggled.");
			sbuttons[j] = tbutton;
			ci.gridx = 1;
			ci.insets = new Insets(10, 10, 10, 10);
			panesubtypes.add(tbutton, ci);
			JButton helpbutton = new JButton("?");
			helpbutton.addActionListener(e -> {
				try {
					java.awt.Desktop.getDesktop()
							.browse(new URI("https://en.wikipedia.org/wiki/Category:" + label.getText().replaceAll(" ", "_")));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			});
			ci.gridx = 2;
			panesubtypes.add(helpbutton, ci);
		}
		JButton collapseButton = new JButton("Abandon Elements");
		collapseButton.addActionListener(event -> abandonElements()); //TODO: collapseType

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(scrollInstances, javax.swing.GroupLayout.PREFERRED_SIZE, 600,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(0, 0, 0).addComponent(scrollSubtypes,
												javax.swing.GroupLayout.PREFERRED_SIZE, 600,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup().addGap(308, 308, 308).addComponent(collapseButton,
								javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addGap(1, 1, 1)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(scrollInstances, javax.swing.GroupLayout.DEFAULT_SIZE, 454,
										Short.MAX_VALUE)
						.addComponent(scrollSubtypes, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(collapseButton,
						javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addContainerGap()));
		pack();
	}
	
	private void abandonElements(){
		Prune prune = new Prune(data);
		for(int j = 0; j < instances.length;j++){
			if(ibuttons[j].isSelected()){
				prune.abandonEntity(instances[j]);
				System.out.println(instances[j]+", Abandon Entity");
			}
		}
		System.out.println("--------TYPES----------");
		for(int j = 0; j < subtypes.length;j++){
			if(sbuttons[j].isSelected()){
				prune.abandonEntity(instances[j]);
				System.out.println(subtypes[j]+", Abandon Type");
			}
		}
		this.dispose();
	}

	public static void main(String[] args) {
		Dataset dataset = TDBFactory.createDataset("./cleanedOntology");
		SemDistGUI g = new SemDistGUI(dataset);
		g.setVisible(true);

	}

}
