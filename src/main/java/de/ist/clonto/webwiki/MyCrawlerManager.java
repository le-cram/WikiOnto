/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import de.ist.clonto.triplestore.WikiTaxToJenaTDB;
import de.ist.clonto.webwiki.model.Classifier;
import de.ist.clonto.webwiki.model.Instance;

/**
 * Uses Wikipedia API to access Wikipedia categories, entities and their
 * infoboxes
 *
 * @author Marcel
 */
public class MyCrawlerManager {
	private String rootname;
	private final Map<String, Classifier> classifierMap;
	private final Queue<Classifier> classifierQueue;
	private final Map<String, Instance> instanceMap;
	private Set<String> exclusionset;

	private int threadcounter;

	public MyCrawlerManager(String root, Set<String> excludedCategories, int maxDepth) {
		rootname = root;
		exclusionset = excludedCategories;
		classifierQueue = new ConcurrentLinkedQueue<>();
		classifierMap = Collections.synchronizedMap(new HashMap<String, Classifier>());
		instanceMap = Collections.synchronizedMap(new HashMap<String, Instance>());
	}

	public void start() throws SAXException, IOException, InterruptedException {
		initialize(rootname);
		threadcounter = 0;
		crawl();
		WikiTaxToJenaTDB.createTripleStore(classifierMap.get(rootname));
	}

	public void crawl() throws SAXException, IOException, InterruptedException {
		initialize(rootname);
		int corenr = Runtime.getRuntime().availableProcessors();
		System.out.println("Starting with " + corenr + " threads!");
		ExecutorService executor = Executors.newFixedThreadPool(corenr);
		while (true) {

			if (!classifierQueue.isEmpty()) {
				incthreadcounter();
				executor.execute(new CategoryCrawler(this, popClassifier()));
			} else {
				if (threadcounter == 0) {
					System.out.println("Stopping at " + classifierMap.size() + "C, " + instanceMap.size() + "E");
					break;
				}
			}
		}

		executor.shutdown();

	}

	private void initialize(String name) {
		Classifier cl = new Classifier();
		cl.setName(name);
		offerClassifier(cl);
		File dir = new File("./" + name.replaceAll(" ", ""));
		if (dir.exists()) {
			try {
				FileUtils.cleanDirectory(dir);
			} catch (IOException ex) {
				Logger.getLogger(WikiTaxToJenaTDB.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			boolean success = dir.mkdirs();
			if (!success) {
				System.err.println("Creating target directory failed");
				System.exit(0);
			}
		}

	}

	public static void main(String[] args0) throws InterruptedException, SAXException, IOException {
		Set<String> exclusionset = new HashSet<>();
		exclusionset.add("Data types");
		exclusionset.add("Programming language topics");
		exclusionset.add("Web services");
		exclusionset.add("User BASIC");
		exclusionset.add("Lists of computer languages");
		exclusionset.add("Programming languages by creation date");
		exclusionset.add("Uncategorized programming languages");
		exclusionset.add("Wikipedia");
		exclusionset.add("Articles");
		exclusionset.add("software");
		exclusionset.add("Software that");
		exclusionset.add("Software for");
		exclusionset.add("Software programmed");
		exclusionset.add("Software written");
		exclusionset.add("Software by");
		exclusionset.add("conference");
		MyCrawlerManager manager = new MyCrawlerManager("Computer_languages", exclusionset, 6);
		manager.start();
	}

	public void offerClassifier(Classifier classifier) {
		if (classifierMap.size() % 100 == 0) {
			System.out.println("#C:" + classifierMap.size() + ", #I" + instanceMap.size());
		}
		classifierQueue.offer(classifier);
	}

	public Classifier popClassifier() {
		return classifierQueue.poll();
	}

	public Classifier getClassifierFromClassifierMap(String name) {
		return classifierMap.get(name);
	}

	public void putInClassifierMap(String name, Classifier classifier) {
		classifierMap.put(name, classifier);
	}

	public Instance getInstanceFromInstanceMap(String name) {
		return instanceMap.get(name);
	}

	public void putInInstanceMap(String name, Instance instance) {
		instanceMap.put(name, instance);
	}

	public boolean isExcludedCategoryName(String name) {
		boolean result = false;
		for (String ex : exclusionset) {
			if (name.contains(ex)) {
				result = true;
				break;
			}
		}
		return result;

	}

	public synchronized void incthreadcounter() {
		threadcounter++;
	}

	public synchronized void decthreadcounter() {
		threadcounter--;
	}

}
