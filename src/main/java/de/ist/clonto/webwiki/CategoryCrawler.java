/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;

import de.ist.clonto.webwiki.model.Classifier;
import de.ist.clonto.webwiki.model.Element;
import de.ist.clonto.webwiki.model.Instance;
import info.bliki.api.Page;

/**
 *
 * @author Marcel
 */
public class CategoryCrawler implements Runnable {

    private final MyCrawlerManager manager;

    private final Classifier type;

    public CategoryCrawler(MyCrawlerManager manager, Classifier type) {
        this.manager = manager;
        this.type = type;
    }

    @Override
    public void run() {
        processCategory();
        
        processSubCategories();

        processEntities();

        manager.decthreadcounter();
    }

    /**
     * Adds Subcategories to type and offers them to the manager's type
     * queue.
     */
    private void processSubCategories() {
        List<String> subcats = null;
        try {
            subcats = WikipediaAPI.getSubCategories(type.getName());
        } catch (SAXException | IOException | InterruptedException ex) {
            Logger.getLogger(CategoryCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String name : subcats) {
            if (manager.isExcludedCategoryName(name.trim())) {
                continue;
            }
            Classifier subtype = manager.getClassifierFromClassifierMap(name);
            if (null == subtype) {
                subtype = new Classifier();
                subtype.setName(name);
                manager.putInClassifierMap(name, subtype);
                manager.offerClassifier(subtype);
            }
            subtype.setMinDepth(type.getMinDepth() + 1);
            type.addSubclassifier(subtype);
        }
    }

    private void processEntities() {
        //add entitys
        List<String> entitys = null;
        try {
            entitys = WikipediaAPI.getPages(type.getName());
        } catch (SAXException | IOException | InterruptedException ex) {
            Logger.getLogger(CategoryCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (String name : entitys) {
            if(name.contains("List of"))
                continue;
            Instance entity = manager.getInstanceFromInstanceMap(name);

            if (null == entity) {
                entity = processentity(name);
                manager.putInInstanceMap(name, entity);
            }
            type.addInstance(entity);
        }
    }

    private Instance processentity(String name) {
        Instance entity = new Instance();
        entity.setName(name);

        Page entityPage = WikipediaAPI.getFirstPage(name);

        retrieveSuperCategories(entityPage.toString(), entity);
        
        return entity;
    }

    void retrieveSuperCategories(String entitytext, Element element) {
        //get supercategories
        Pattern categoryPattern = Pattern.compile("\\s*\\[\\[\\s*category\\s*:.*?\\]\\]", Pattern.CASE_INSENSITIVE);
        Matcher categoryMatcher = categoryPattern.matcher(entitytext);
        while (categoryMatcher.find()) {
            String result = categoryMatcher.group();
            result = result.replaceAll("[\\[\\]\\n]", "");
            result = result.split("\\|")[0];
            result = result.split(":")[1];
            element.addClassifier(result);
        }

    }

    /**
    List<Information> retrieveAttributesFromInfobox(String entitytext) {
        List<Information> informationList = new InfoboxParser().parse(entitytext);
        
        return informationList;
    }
    **/

    private void processCategory() {

        Page page = WikipediaAPI.getFirstPage("Category:" + type.getName());
        /**This kind of marker is not used
        if(page.toString().contains("{Eponymous}"))
            System.out.println(type.getName());
        retrieveMainEntity(page.toString());
        **/
        retrieveSuperCategories(page.toString(), type);
    }
    
    void retrieveMainEntity(String page){
        Pattern categoryPattern = Pattern.compile("\\n\\s*\\{\\{cat (main|more)\\|.*?\\}\\}", Pattern.CASE_INSENSITIVE);
        Matcher categoryMatcher = categoryPattern.matcher(page);
        if (categoryMatcher.find()) {
            String result = categoryMatcher.group();
            result = Pattern.compile("(cat|main|more|\\{|\\}|\\|)", Pattern.CASE_INSENSITIVE).matcher(result).replaceAll("").trim(); 
            Instance mainentity = processentity(result);
            type.setDescription(mainentity);
        }
        categoryPattern = Pattern.compile("\\n\\s*\\{\\{cat main\\}\\}", Pattern.CASE_INSENSITIVE);
        categoryMatcher = categoryPattern.matcher(page);
        if (categoryMatcher.find()) {
            Instance mainentity = processentity(type.getName());
            type.setDescription(mainentity);
        }
    }


}
