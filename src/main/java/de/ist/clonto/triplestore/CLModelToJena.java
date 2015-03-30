/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.triplestore;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import de.ist.clonto.webwiki.model.Attribute;
import de.ist.clonto.webwiki.model.AttributeSet;
import de.ist.clonto.webwiki.model.Category;
import de.ist.clonto.webwiki.model.Element;
import de.ist.clonto.webwiki.model.Entity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;

/**
 *
 * @author Marcel
 */
public class CLModelToJena {

    private final String URI = "http://myCLOnto.de/";
    private final String cURI = URI + "Category#";
    private final String eURI = URI + "Entity#";
    private final String aURI = URI + "Attribute#";
    private final String asURI = URI + "AttributeSet#";

    private Model model;

    private Map<String, Resource> catResMap;
    private Map<String, Resource> entityResMap;
    private int attributesetcount;
    private int attributecount;

    public void createTripleStore(Category rootCategory) throws FileNotFoundException {
        String directory = "./clontologytdb";
        try {
            FileUtils.cleanDirectory(new File(directory));
        } catch (IOException ex) {
            Logger.getLogger(CLModelToJena.class.getName()).log(Level.SEVERE, null, ex);
        }

        Dataset dataset = TDBFactory.createDataset(directory);

        dataset.begin(ReadWrite.WRITE);
        model = dataset.getDefaultModel();
        catResMap = new HashMap<>();
        entityResMap = new HashMap<>();

        Resource rootResource = model.getResource(cURI + catResMap.size());
        catResMap.put(rootCategory.getURIName(), rootResource);
        rootResource.addProperty(model.getProperty(URI + "name"), rootCategory.getName());
        rootResource.addProperty(model.getProperty(URI + "depth"),
                Integer.toString(rootCategory.getMinDepth()));

        transformCategory(rootCategory);

        // put outputstream instead of null
        dataset.commit();
        dataset.end();
    }

    private void transformCategory(Category category) {
        Resource categoryResource = catResMap.get(category.getURIName());

        if (null != category.getMainEntity()) {
            Resource mainentityResource;
            if (!entityResMap.containsKey(category.getMainEntity().getURIName())) {
                mainentityResource = model.createResource(eURI + entityResMap.size());
                mainentityResource.addProperty(model.getProperty(URI + "name"), category.getMainEntity().getName());
                entityResMap.put(category.getMainEntity().getURIName(), mainentityResource);
                categoryResource.addProperty(model.getProperty(URI + "hasMainEntity"), mainentityResource);
                categoryResource.addProperty(model.getProperty(URI + "hasEntity"), mainentityResource);
                transformentity(category.getMainEntity());
            } else {
                mainentityResource = entityResMap.get(category.getMainEntity().getURIName());
                categoryResource.addProperty(model.getProperty(URI + "hasMainEntity"), mainentityResource);
                categoryResource.addProperty(model.getProperty(URI + "hasEntity"), mainentityResource);
            }

        }

        for (Entity entity : category.getEntities()) {
            Resource entityResource;
            if (!entityResMap.containsKey(entity.getURIName())) {
                entityResource = model.createResource(eURI + entityResMap.size());
                entityResMap.put(entity.getURIName(), entityResource);
                entityResource.addProperty(model.getProperty(URI + "name"), entity.getName());
                categoryResource.addProperty(model.getProperty(URI + "hasEntity"), entityResource);
                transformentity(entity);
            } else {
                entityResource = entityResMap.get(entity.getURIName());
                categoryResource.addProperty(model.getProperty(URI + "hasEntity"), entityResource);
            }

        }

        transformSubcategories(category);
        transformSupercategories(category, false);
    }

    private void transformSubcategories(Category category) {
        for (Category subcategory : category.getSubCategories()) {
            Resource subcategoryResource;
            if (!catResMap.containsKey(subcategory.getURIName())) {
                subcategoryResource = model.createResource(cURI + catResMap.size());
                catResMap.put(subcategory.getURIName(), subcategoryResource);
                subcategoryResource.addProperty(model.getProperty(URI + "name"), subcategory.getName());
                transformCategory(subcategory);
            } else {
                subcategoryResource = catResMap.get(subcategory.getURIName());
            }
            Resource categoryResource = catResMap.get(category.getURIName());
            categoryResource.addProperty(model.getProperty(URI + "hasSubCategory"), subcategoryResource);
            if(!subcategoryResource.hasProperty(model.getProperty(URI + "depth"))){
                subcategoryResource.addProperty(model.getProperty(URI + "depth"),
                        Integer.toString(subcategory.getMinDepth()));
                transformCategory(subcategory);
            }
        }
    }

    private void transformSupercategories(Element element, boolean isEntity) {
        for (String supercat : element.getSupercategories()) {
            Resource supercategoryResource;
            if (!catResMap.containsKey(replaceWhitespaceByUnderscore(supercat))) {
                supercategoryResource = model.createResource(cURI + catResMap.size());
                catResMap.put(replaceWhitespaceByUnderscore(supercat), supercategoryResource);
                supercategoryResource.addProperty(model.getProperty(URI + "name"), removeUnderscore(supercat));
            } else {
                supercategoryResource = catResMap.get(replaceWhitespaceByUnderscore(supercat));
            }
            if (isEntity) {
                Resource elementResource = entityResMap.get(element.getURIName());
                supercategoryResource.addProperty(model.getProperty(URI + "hasEntity"), elementResource);
            } else {
                Resource elementResource = catResMap.get(element.getURIName());
                supercategoryResource.addProperty(model.getProperty(URI + "hasSubCategory"), elementResource);
            }
        }
    }

    private void transformentity(Entity entity) {
        transformSupercategories(entity, true);

        List<AttributeSet> attributeSetList = entity.getAttributeSetList();

        for (AttributeSet attributeSet : attributeSetList) {
            Resource attributeSetResource = model.createResource(asURI + attributesetcount);
            attributeSetResource.addProperty(model.getProperty(URI + "name"), Integer.toString(attributesetcount));
            attributeSetResource.addProperty(model.getProperty(URI + "topic"), attributeSet.getName());
            attributesetcount++;
            Resource entityResource = entityResMap.get(entity.getURIName());
            entityResource.addProperty(model.getProperty(URI + "hasAttributeSet"), attributeSetResource);

            transformAttributeSet(attributeSet, attributeSetResource);
        }
    }

    private void transformAttributeSet(AttributeSet attributeSet, Resource attributeSetResource) {
        List<Attribute> attributes = attributeSet.getAttributes();
        for (Attribute attribute : attributes) {
            Resource attributeResource = model.createResource(aURI + attributecount);
            attributecount++;
            attributeResource.addProperty(model.getProperty(URI + "name"), filterHTML(attribute.getName()));
            attributeResource.addProperty(model.getProperty(URI + "value"), filterHTML(attribute.getValue()));
            attributeSetResource.addProperty(model.getProperty(URI + "hasAttribute"), attributeResource);
        }
    }

    private String filterHTML(String text) {
        String result = Jsoup.parse(text).text().trim();
        return removeLteGte(result);
    }

    private String removeLteGte(String text) {
        return text.replaceAll("<", "").replaceAll(">", "");
    }

    private String removeUnderscore(String supercat) {
        return supercat.replaceAll("_", " ");
    }

    private String replaceWhitespaceByUnderscore(String supercat) {
        return supercat.replaceAll(" ", "_");
    }
}
