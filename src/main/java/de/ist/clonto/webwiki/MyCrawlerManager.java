/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki;

import de.ist.clonto.triplestore.CLModelToJena;
import de.ist.clonto.webwiki.model.Category;
import de.ist.clonto.webwiki.model.Entity;
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
import org.xml.sax.SAXException;

/**
 * Uses Wikipedia API to access Wikipedia categories, entities and their infoboxes
 *
 * @author Marcel
 */
public class MyCrawlerManager {

    private final Map<String, Category> categoryMap;
    private final Queue<Category> categoryQueue;
    private final Map<String, Entity> entityMap;
    private Set<String> exclusionset;

    private int threadcounter;

    public MyCrawlerManager() {
        categoryQueue = new ConcurrentLinkedQueue<>();
        categoryMap = Collections.synchronizedMap(new HashMap<String, Category>());
        entityMap = Collections.synchronizedMap(new HashMap<String, Entity>());
        initExclusionSet();

        threadcounter = 0;

    }

    public void crawl() throws SAXException, IOException, InterruptedException {
        int corenr = Runtime.getRuntime().availableProcessors();
        System.out.println("Starting with "+corenr+" threads!");
        ExecutorService executor = Executors.newFixedThreadPool(corenr);
        while (true) {
            
            if (!categoryQueue.isEmpty()) {
                executor.execute(new MyCrawler(this, popCategory()));
                incthreadcounter();
            }else{
                if(threadcounter == 0){
                    System.out.println("Stopping at "+categoryMap.size()+"C, "+entityMap.size()+"E");
                    break;
                }
            }
        }

        executor.shutdown();
    }

    public static void main(String[] args0) throws InterruptedException, SAXException, IOException {
        Category cl = new Category();
        cl.setName("Computer languages");
        MyCrawlerManager manager = new MyCrawlerManager();
        manager.offerCategory(cl);
        manager.crawl();
        new CLModelToJena().createTripleStore(cl);
    }

    private void initExclusionSet() {
        exclusionset = new HashSet<>();
        exclusionset.add("Data types");
        exclusionset.add("Programming language topics");
        exclusionset.add("Articles with example code");
        exclusionset.add("Internet search engines");
        exclusionset.add("Instant messaging");
        exclusionset.add("Internet search");
        //from Programming language topics -> Programming Paradigms
        exclusionset.add("Google services");
        exclusionset.add("Net-centric");
        exclusionset.add("Service-oriented architecture-related products");
        exclusionset.add("Programming language topic stubs");
        exclusionset.add("User BASIC");
        exclusionset.add("Lists of computer languages");
    }

    public void offerCategory(Category category) {
        if(categoryMap.size() % 100 ==0)
            System.out.println("#C:"+categoryMap.size()+", #E"+entityMap.size());
        categoryQueue.offer(category);
    }

    public Category popCategory() {
        return categoryQueue.poll();
    }

    public Category getCategoryFromCategoryMap(String title) {
        return categoryMap.get(title);
    }

    public void putInCategoryMap(String name, Category category) {
        categoryMap.put(name, category);
    }

    public Entity getEntityFromEntityMap(String title) {
        return entityMap.get(title);
    }

    public void putInentityMap(String name, Entity entity) {
        entityMap.put(name, entity);
    }

    public boolean isExcludedCategoryName(String name) {
        return exclusionset.contains(name);
    }

    public synchronized void incthreadcounter() {
        threadcounter++;
    }

    public synchronized void decthreadcounter() {
        threadcounter--;
    }
   
}
