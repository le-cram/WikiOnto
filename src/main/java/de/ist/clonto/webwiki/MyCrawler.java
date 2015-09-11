/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki;

import de.ist.clonto.webwiki.model.Attribute;
import de.ist.clonto.webwiki.model.AttributeSet;
import de.ist.clonto.webwiki.model.Category;
import de.ist.clonto.webwiki.model.Element;
import de.ist.clonto.webwiki.model.Entity;
import info.bliki.api.Page;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.SAXException;

/**
 *
 * @author Marcel
 */
public class MyCrawler implements Runnable {

    private final MyCrawlerManager manager;

    private final Category category;

    public MyCrawler(MyCrawlerManager manager, Category category) {
        this.manager = manager;
        this.category = category;
    }

    @Override
    public void run() {
        processCategory();
        
        processSubCategories();

        processentities();

        manager.decthreadcounter();
    }

    /**
     * Adds Subcategories to category and offers them to the manager's category
     * queue.
     */
    private void processSubCategories() {
        List<String> subcats = null;
        try {
            subcats = WikipediaAPI.getSubCategories(category.getName());
        } catch (SAXException | IOException | InterruptedException ex) {
            Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String name : subcats) {
            if (manager.isExcludedCategoryName(name.trim())) {
                continue;
            }
            Category subcat = manager.getCategoryFromCategoryMap(name);
            if (null == subcat) {
                subcat = new Category();
                subcat.setName(name);
                manager.putInCategoryMap(name, subcat);
                manager.offerCategory(subcat);
            }
            subcat.setMinDepth(category.getMinDepth() + 1);
            category.addSubCategory(subcat);
        }
    }

    private void processentities() {
        //add entitys
        List<String> entitys = null;
        try {
            entitys = WikipediaAPI.getPages(category.getName());
        } catch (SAXException | IOException | InterruptedException ex) {
            Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (String name : entitys) {
            Entity entity = manager.getEntityFromEntityMap(name);

            if (null == entity) {
                entity = processentity(name);
                manager.putInentityMap(name, entity);
            }
            category.addEntity(entity);
        }
    }

    private Entity processentity(String name) {
        Entity entity = new Entity();
        entity.setName(name);

        Page entityPage = WikipediaAPI.getFirstPage(name);
        List<AttributeSet> infoboxSetList = retrieveAttributesFromInfobox(entityPage.toString());
        for(AttributeSet atset : infoboxSetList){
            entity.addAttributeSet(atset);
        }

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
            element.addSuperCategory(result);
        }

    }

    List<AttributeSet> retrieveAttributesFromInfobox(String entitytext) {
        List<AttributeSet> attributeSetList = new InfoboxParser().parse(entitytext);
        
        return attributeSetList;
    }

    private void processCategory() {
        Page page = WikipediaAPI.getFirstPage("Category:" + category.getName());
        retrieveMainEntity(page.toString());
        retrieveSuperCategories(page.toString(), category);
    }
    
    void retrieveMainEntity(String page){
        Pattern categoryPattern = Pattern.compile("\\n\\s*\\{\\{cat (main|more)\\|.*?\\}\\}", Pattern.CASE_INSENSITIVE);
        Matcher categoryMatcher = categoryPattern.matcher(page);
        if (categoryMatcher.find()) {
            String result = categoryMatcher.group();
            result = Pattern.compile("(cat|main|more|\\{|\\}|\\|)", Pattern.CASE_INSENSITIVE).matcher(result).replaceAll("").trim(); 
            Entity mainentity = processentity(result);
            category.setMainEntity(mainentity);
        }
        categoryPattern = Pattern.compile("\\n\\s*\\{\\{cat main\\}\\}", Pattern.CASE_INSENSITIVE);
        categoryMatcher = categoryPattern.matcher(page);
        if (categoryMatcher.find()) {
            Entity mainentity = processentity(category.getName());
            category.setMainEntity(mainentity);
        }
    }


}
