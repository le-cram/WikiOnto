/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki;

import de.ist.clonto.webwiki.model.*;
import de.ist.clonto.webwiki.model.Type;
import info.bliki.api.Page;
import java.io.IOException;
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

    private final Type type;

    public MyCrawler(MyCrawlerManager manager, Type type) {
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
            Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String name : subcats) {
            if (manager.isExcludedCategoryName(name.trim())||name.trim().contains("Wikipedia")||name.trim().contains("Articles with")) {
                continue;
            }
            Type subtype = manager.getTypeFromTypeMap(name);
            if (null == subtype) {
                subtype = new Type();
                subtype.setName(name);
                manager.putInTypeMap(name, subtype);
                manager.offerType(subtype);
            }
            subtype.setMinDepth(type.getMinDepth() + 1);
            type.addSubtype(subtype);
        }
    }

    private void processEntities() {
        //add entitys
        List<String> entitys = null;
        try {
            entitys = WikipediaAPI.getPages(type.getName());
        } catch (SAXException | IOException | InterruptedException ex) {
            Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (String name : entitys) {
            if(name.contains("List of"))
                continue;
            Entity entity = manager.getEntityFromEntityMap(name);

            if (null == entity) {
                entity = processentity(name);
                manager.putInEntityMap(name, entity);
            }
            type.addEntity(entity);
        }
    }

    private Entity processentity(String name) {
        Entity entity = new Entity();
        entity.setName(name);

        Page entityPage = WikipediaAPI.getFirstPage(name);
        List<Information> informationList = retrieveAttributesFromInfobox(entityPage.toString());
        for(Information information : informationList){
            entity.addInformation(information);
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
            element.addType(result);
        }

    }

    List<Information> retrieveAttributesFromInfobox(String entitytext) {
        List<Information> informationList = new InfoboxParser().parse(entitytext);
        
        return informationList;
    }

    private void processCategory() {

        Page page = WikipediaAPI.getFirstPage("Category:" + type.getName());
        if(page.toString().contains("{Eponymous}"))
            System.out.println(type.getName());
        retrieveMainEntity(page.toString());
        retrieveSuperCategories(page.toString(), type);
    }
    
    void retrieveMainEntity(String page){
        Pattern categoryPattern = Pattern.compile("\\n\\s*\\{\\{cat (main|more)\\|.*?\\}\\}", Pattern.CASE_INSENSITIVE);
        Matcher categoryMatcher = categoryPattern.matcher(page);
        if (categoryMatcher.find()) {
            String result = categoryMatcher.group();
            result = Pattern.compile("(cat|main|more|\\{|\\}|\\|)", Pattern.CASE_INSENSITIVE).matcher(result).replaceAll("").trim(); 
            Entity mainentity = processentity(result);
            type.setMainEntity(mainentity);
        }
        categoryPattern = Pattern.compile("\\n\\s*\\{\\{cat main\\}\\}", Pattern.CASE_INSENSITIVE);
        categoryMatcher = categoryPattern.matcher(page);
        if (categoryMatcher.find()) {
            Entity mainentity = processentity(type.getName());
            type.setMainEntity(mainentity);
        }
    }


}
