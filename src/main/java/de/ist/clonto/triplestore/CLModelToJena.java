/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.triplestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.ist.clonto.webwiki.model.Element;
import de.ist.clonto.webwiki.model.Entity;
import de.ist.clonto.webwiki.model.Information;
import de.ist.clonto.webwiki.model.Property;
import de.ist.clonto.webwiki.model.Type;

/**
 *
 * @author Marcel
 */
public class CLModelToJena {

    private final String URI = "http://myCLOnto.de/";
    private final String tURI = URI + "Type#";
    private final String eURI = URI + "Entity#";
    private final String pURI = URI + "Property#";
    private final String iURI = URI + "Information#";

    private Model model;

    private Map<String, Resource> typeResMap;
    private Map<String, Resource> entityResMap;
    private int informationcount;
    private int propertycount;

    public void createTripleStore(Type rootType) throws FileNotFoundException {
        String directory = "./cleanedOntology2";
        try {
            FileUtils.cleanDirectory(new File(directory));
        } catch (IOException ex) {
            Logger.getLogger(CLModelToJena.class.getName()).log(Level.SEVERE, null, ex);
        }

        Dataset dataset = TDBFactory.createDataset(directory);

        dataset.begin(ReadWrite.WRITE);
        model = dataset.getDefaultModel();
        typeResMap = new HashMap<>();
        entityResMap = new HashMap<>();

        Resource rootResource = model.getResource(tURI + typeResMap.size());
        typeResMap.put(rootType.getURIName(), rootResource);
        rootResource.addProperty(model.getProperty(URI + "name"), rootType.getName());
        rootResource.addProperty(model.getProperty(URI + "depth"),
                Integer.toString(rootType.getMinDepth()));

        transformType(rootType);

        // put outputstream instead of null
        dataset.commit();
        dataset.end();
    }

    private void transformType(Type type) {
        Resource typeResource = typeResMap.get(type.getURIName());

        if (null != type.getMainEntity()) {
            Resource mainentityResource;
            if (!entityResMap.containsKey(type.getMainEntity().getURIName())) {
                mainentityResource = model.createResource(eURI + entityResMap.size());
                mainentityResource.addProperty(model.getProperty(URI + "name"), type.getMainEntity().getName());
                entityResMap.put(type.getMainEntity().getURIName(), mainentityResource);
                typeResource.addProperty(model.getProperty(URI + "definedBy"), mainentityResource);
                transformEntity(type.getMainEntity());
            } else {
                mainentityResource = entityResMap.get(type.getMainEntity().getURIName());
                typeResource.addProperty(model.getProperty(URI + "definedBy"), mainentityResource);
            }

        }

        for (Entity entity : type.getEntities()) {
            Resource entityResource;
            if (!entityResMap.containsKey(entity.getURIName())) {
                entityResource = model.createResource(eURI + entityResMap.size());
                entityResMap.put(entity.getURIName(), entityResource);
                entityResource.addProperty(model.getProperty(URI + "name"), entity.getName());
                typeResource.addProperty(model.getProperty(URI + "hasInstance"), entityResource);
                transformEntity(entity);
            } else {
                entityResource = entityResMap.get(entity.getURIName());
                typeResource.addProperty(model.getProperty(URI + "hasInstance"), entityResource);
            }

        }

        transformSubtypes(type);
        transformSupertypes(type, false);
    }

    private void transformSubtypes(Type type) {
        for (Type subtype : type.getSubtypes()) {
            Resource subtypeResource;
            if (!typeResMap.containsKey(subtype.getURIName())) {
                subtypeResource = model.createResource(tURI + typeResMap.size());
                typeResMap.put(subtype.getURIName(), subtypeResource);
                subtypeResource.addProperty(model.getProperty(URI + "name"), subtype.getName());
                transformType(subtype);
            } else {
                subtypeResource = typeResMap.get(subtype.getURIName());
            }
            Resource typeResource = typeResMap.get(type.getURIName());
            typeResource.addProperty(model.getProperty(URI + "hasSubtype"), subtypeResource);
            if(!subtypeResource.hasProperty(model.getProperty(URI + "depth"))){
                subtypeResource.addProperty(model.getProperty(URI + "depth"),
                        Integer.toString(subtype.getMinDepth()));
                transformType(subtype);
            }
        }
    }

    private void transformSupertypes(Element element, boolean isEntity) {
        for (String supertype : element.getTypes()) {
            Resource supertypeResource;
            if (!typeResMap.containsKey(replaceWhitespaceByUnderscore(supertype))) {
                supertypeResource = model.createResource(tURI + typeResMap.size());
                typeResMap.put(replaceWhitespaceByUnderscore(supertype), supertypeResource);
                supertypeResource.addProperty(model.getProperty(URI + "name"), removeUnderscore(supertype));
            } else {
                supertypeResource = typeResMap.get(replaceWhitespaceByUnderscore(supertype));
            }
            if (isEntity) {
                Resource elementResource = entityResMap.get(element.getURIName());
                supertypeResource.addProperty(model.getProperty(URI + "hasInstance"), elementResource);
            } else {
                Resource elementResource = typeResMap.get(element.getURIName());
                supertypeResource.addProperty(model.getProperty(URI + "hasSubtype"), elementResource);
            }
        }
    }

    private void transformEntity(Entity entity) {
        transformSupertypes(entity, true);

        List<Information> informationList = entity.getInformationList();

        for (Information information : informationList) {
            Resource informationResource = model.createResource(iURI + informationcount);
            informationResource.addProperty(model.getProperty(URI + "name"), Integer.toString(informationcount));
            informationResource.addProperty(model.getProperty(URI + "topic"), information.getName());
            informationcount++;
            Resource entityResource = entityResMap.get(entity.getURIName());
            entityResource.addProperty(model.getProperty(URI + "hasInformation"), informationResource);

            transformInformation(information, informationResource);
        }
    }

    private void transformInformation(Information information, Resource informationResource) {
        List<Property> properties = information.getProperties();
        for (Property property : properties) {
            Resource propertyResource = model.createResource(pURI + propertycount);
            propertycount++;
            propertyResource.addProperty(model.getProperty(URI + "name"), filterHTML(property.getName()));
            propertyResource.addProperty(model.getProperty(URI + "value"), filterHTML(property.getValue()));
            informationResource.addProperty(model.getProperty(URI + "hasProperty"), propertyResource);
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
