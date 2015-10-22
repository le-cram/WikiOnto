/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki;

import de.ist.clonto.triplestore.CLModelToJena;
import de.ist.clonto.webwiki.model.Type;
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

    private final Map<String, Type> typeMap;
    private final Queue<Type> typeQueue;
    private final Map<String, Entity> entityMap;
    private Set<String> exclusionset;

    private int threadcounter;

    public MyCrawlerManager() {
        typeQueue = new ConcurrentLinkedQueue<>();
        typeMap = Collections.synchronizedMap(new HashMap<String, Type>());
        entityMap = Collections.synchronizedMap(new HashMap<String, Entity>());
        initExclusionSet();

        threadcounter = 0;

    }

    public void crawl() throws SAXException, IOException, InterruptedException {
        int corenr = Runtime.getRuntime().availableProcessors();
        System.out.println("Starting with "+corenr+" threads!");
        ExecutorService executor = Executors.newFixedThreadPool(corenr);
        while (true) {
            
            if (!typeQueue.isEmpty()) {
                incthreadcounter();
                executor.execute(new MyCrawler(this, popType()));
            }else{
                if(threadcounter == 0){
                    System.out.println("Stopping at "+ typeMap.size()+"C, "+entityMap.size()+"E");
                    break;
                }
            }
        }

        executor.shutdown();
    }

    public static void main(String[] args0) throws InterruptedException, SAXException, IOException {
        Type cl = new Type();
        cl.setName("Computer languages");
        MyCrawlerManager manager = new MyCrawlerManager();
        manager.offerType(cl);
        manager.crawl();
        new CLModelToJena().createTripleStore(cl);
    }

    private void initExclusionSet() {
        exclusionset = new HashSet<>();
        exclusionset.add("Data types");
        exclusionset.add("Programming language topics");
        exclusionset.add("Web services");
        //Google services, Net-centric, Service-oriented architecture-related products, Programming language topic stubs
        exclusionset.add("User BASIC");
        exclusionset.add("Lists of computer languages");
    }

    public void offerType(Type type) {
        if(typeMap.size() % 100 ==0)
            System.out.println("#C:"+ typeMap.size()+", #E"+entityMap.size());
        typeQueue.offer(type);
    }

    public Type popType() {
        return typeQueue.poll();
    }

    public Type getTypeFromTypeMap(String title) {
        return typeMap.get(title);
    }

    public void putInTypeMap(String name, Type type) {
        typeMap.put(name, type);
    }

    public Entity getEntityFromEntityMap(String title) {
        return entityMap.get(title);
    }

    public void putInEntityMap(String name, Entity entity) {
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
