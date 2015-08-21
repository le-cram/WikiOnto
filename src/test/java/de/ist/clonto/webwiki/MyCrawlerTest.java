/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki;

import de.ist.clonto.webwiki.model.Attribute;
import de.ist.clonto.webwiki.model.AttributeSet;
import de.ist.clonto.webwiki.model.Category;
import de.ist.clonto.webwiki.model.Entity;
import info.bliki.api.Page;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Marcel
 */
public class MyCrawlerTest {

    public MyCrawlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of retrieveSuperCategories() of class MyCrawler.
     */
    @Test
    public void testSuperCategories() {
        System.out.println("supercategories of Category:SQL");
        MyCrawler instance = new MyCrawler(null, null);
        Page page = WikipediaAPI.getFirstPage("Category:SQL");
        Entity e = new Entity();
        instance.retrieveSuperCategories(page.toString(), e);
        assertTrue(e.getSupercategories().size() == 2);
        assertTrue(e.getSupercategories().contains("Database management systems"));
        assertTrue(e.getSupercategories().contains("Query languages"));
    }

    @Test
    public void testSuperCategories1() {
        System.out.println("supercategories of Entity SQL");
        MyCrawler instance = new MyCrawler(null, null);
        Page page = WikipediaAPI.getFirstPage("SQL");
        Entity e = new Entity();
        instance.retrieveSuperCategories(page.toString(), e);
        System.out.println(e.getSupercategories().size());
        assertTrue(e.getSupercategories().size() == 8);
        assertTrue(e.getSupercategories().contains("Articles with example SQL code"));
        assertTrue(e.getSupercategories().contains("Computer languages"));
        assertTrue(e.getSupercategories().contains("Data modeling languages"));
        assertTrue(e.getSupercategories().contains("Declarative programming languages"));
        assertTrue(e.getSupercategories().contains("Relational database management systems"));
        assertTrue(e.getSupercategories().contains("Query languages"));
        assertTrue(e.getSupercategories().contains("SQL"));
        assertTrue(e.getSupercategories().contains("Requests for audio pronunciation (English)"));
    }

    @Test
    public void testSuperCategories2() {
        System.out.println("supercategories of Category:Free compilers and interpreters");
        MyCrawler instance = new MyCrawler(null, null);
        Page page = WikipediaAPI.getFirstPage("Category:Free_compilers_and_interpreters");
        Entity e = new Entity();
        instance.retrieveSuperCategories(page.toString(), e);
        assertTrue(e.getSupercategories().size() == 3);
        assertTrue(e.getSupercategories().contains("Free computer programming tools"));
        assertTrue(e.getSupercategories().contains("Compilers"));
        assertTrue(e.getSupercategories().contains("Interpreters (computing)"));
    }

    @Test
    public void testSuperCategories3() {
        System.out.println("supercategories of APL (programming language)");
        MyCrawler instance = new MyCrawler(null, null);
        Page page = WikipediaAPI.getFirstPage("APL (programming language)");
        Entity e = new Entity();
        instance.retrieveSuperCategories(page.toString(), e);
        assertTrue(e.getSupercategories().size() == 8);
        assertTrue(e.getSupercategories().contains("Array programming languages"));
        assertTrue(e.getSupercategories().contains("Functional languages"));
        assertTrue(e.getSupercategories().contains("Dynamic programming languages"));
        assertTrue(e.getSupercategories().contains("APL programming language family"));
        assertTrue(e.getSupercategories().contains(".NET programming languages"));
        assertTrue(e.getSupercategories().contains("IBM software"));
        assertTrue(e.getSupercategories().contains("Command shells"));
        assertTrue(e.getSupercategories().contains("Programming languages created in 1964"));
    }

    @Test
    public void testSuperCategories4() {
        System.out.println("supercategories of C++");
        MyCrawler instance = new MyCrawler(null, null);
        Page page = WikipediaAPI.getFirstPage("C++");
        Entity e = new Entity();
        instance.retrieveSuperCategories(page.toString(), e);
        assertEquals(9, e.getSupercategories().size());
        assertTrue(e.getSupercategories().contains("C++"));
        assertTrue(e.getSupercategories().contains("Algol programming language family"));
        assertTrue(e.getSupercategories().contains("C++ programming language family"));
        assertTrue(e.getSupercategories().contains("Class-based programming languages"));
        assertTrue(e.getSupercategories().contains("Cross-platform software"));
        assertTrue(e.getSupercategories().contains("Object-oriented programming languages"));
        assertTrue(e.getSupercategories().contains("Programming languages created in 1983"));
        assertTrue(e.getSupercategories().contains("Statically typed programming languages"));
        assertTrue(e.getSupercategories().contains("Programming languages with an ISO standard"));
    }

    @Test
    public void testSuperCategories5() {
        System.out.println("supercategories of JSLint");
        MyCrawler instance = new MyCrawler(null, null);
        Page page = WikipediaAPI.getFirstPage("JSLint");
        Entity e = new Entity();
        instance.retrieveSuperCategories(page.toString(), e);
        assertEquals(2, e.getSupercategories().size());
        assertTrue(e.getSupercategories().contains("JavaScript programming tools"));
        assertTrue(e.getSupercategories().contains("Static program analysis tools"));
    }

    @Test
    public void testSuperCategories6() {
        System.out.println("supercategories of Category:LibreOffice");
        MyCrawler instance = new MyCrawler(null, null);
        Page page = WikipediaAPI.getFirstPage("Category:LibreOffice");
        Entity e = new Entity();
        instance.retrieveSuperCategories(page.toString(), e);
        assertEquals(6, e.getSupercategories().size());
        assertTrue(e.getSupercategories().contains("Cross-platform free software"));
        assertTrue(e.getSupercategories().contains("Free software programmed in C++"));
        assertTrue(e.getSupercategories().contains("Office suites for Linux"));
        assertTrue(e.getSupercategories().contains("OpenDocument"));
        assertTrue(e.getSupercategories().contains("Open-source office suites"));
        assertTrue(e.getSupercategories().contains("Free software"));
    }

    /**
     * Test of retrieveAttributesFromInfobox of class MyCrawler. Specific
     * aspect: Filtering references and their contained cites.
     */
    @Test
    public void testInfoboxRetrievalNoRef() {
        System.out.println("infobox of article SPARQL");
        Page page = WikipediaAPI.getFirstPage("SPARQL");
        List<AttributeSet> atsetlist = new InfoboxParser().parse(page.toString());
        assertEquals(1,atsetlist.size());
        AttributeSet set = atsetlist.get(0);
        assertEquals("programming language", set.getName());
        assertEquals(9, set.getAttributes().size());

        for (Attribute clat : set.getAttributes()) {
            switch (clat.getName()) {
                case "name":
                    assertEquals("SPARQL", clat.getValue());
                    break;
                case "paradigm":
                    assertEquals("Query language", clat.getValue());
                    break;
                case "year":
                    assertEquals("{{Start date and age|2008}}", clat.getValue());
                    break;
                case "developer":
                    assertEquals("W3C", clat.getValue());
                    break;
                case "latest_release_version":
                    assertEquals("1.1", clat.getValue());
                    break;
                case "latest_release_date":
                    assertEquals("{{Start date and age|2013|03|21}}", clat.getValue());
                    break;
                case "implementations":
                    if (!clat.getValue().equals("Jena (framework)") && !clat.getValue().equals("Virtuoso Universal Server")) {
                        System.out.println(clat.getValue());
                        fail();
                    }
                    break;
                case "website":
                    assertEquals("{{url|http://www.w3.org/TR/sparql11-query/}}", clat.getValue());
                    break;
                default:
                    fail();
            }
        }
    }

    /**
     * Test of retrieveSuperCategories() of class MyCrawler.
     *
     */
    @Test
    public void testInfoboxRetrieval() {
        System.out.println("infobox of article BALL");
        Page page = WikipediaAPI.getFirstPage("BALL");
        List<AttributeSet> atsetlist = new InfoboxParser().parse(page.toString());
        assertEquals(1,atsetlist.size());
        AttributeSet set = atsetlist.get(0);
        assertEquals("software", set.getName());
        assertEquals(17, set.getAttributes().size());
    }
    
    @Test
    public void testInfoboxRetrieval2GnuWin32(){
        System.out.println("infobox of article GnuWin32");
        Page page = WikipediaAPI.getFirstPage("GnuWin32");
        List<AttributeSet> atsetlist = new InfoboxParser().parse(page.toString());
        assertEquals(1,atsetlist.size());
        AttributeSet atset = atsetlist.get(0);
        assertEquals("",atset.getName());
    }
    
    @Test
    public void testInfoboxRetrievalNull() {
        System.out.println("infobox of article AMBER");
        Page page = WikipediaAPI.getFirstPage("AMBER");
        List<AttributeSet> atsetlist = new InfoboxParser().parse(page.toString());
        assertTrue(atsetlist.isEmpty());
    }
    
    @Test
    public void testInfoboxRetrievalCommentedInfoboxName() {
        System.out.println("infobox of article ARM_architecture");
        Page page = WikipediaAPI.getFirstPage("ARM_architecture");
        List<AttributeSet> atsetlist = new InfoboxParser().parse(page.toString());
        assertEquals(4,atsetlist.size());
        AttributeSet set = atsetlist.get(0);
        assertEquals("cpu architecture", atsetlist.get(0).getName());
        assertEquals(12, set.getAttributes().size());
        assertEquals("cpu architecture", atsetlist.get(1).getName());
        assertEquals(14, atsetlist.get(1).getAttributes().size());
        assertEquals("cpu architecture", atsetlist.get(2).getName());
        assertEquals(17, atsetlist.get(2).getAttributes().size());
        assertEquals("cpu architecture", atsetlist.get(3).getName());
        assertEquals(12, atsetlist.get(3).getAttributes().size());
        
    }
    
    @Test
    public void testInfoboxRetrievalInfoboxCombiningNameFirstitem() {
        System.out.println("infobox of article Jumpjet");
        Page page = WikipediaAPI.getFirstPage("Jumpjet");
        List<AttributeSet> atsetlist = new InfoboxParser().parse(page.toString());
        assertEquals(1,atsetlist.size());
        AttributeSet set = atsetlist.get(0);
        assertEquals("video game", set.getName());
        assertEquals(set.getAttributes().size(), 8);
    }
    
    @Test
    public void testInfoboxRetrievalTwoSets(){
        System.out.println("infoboxes of article XML");
        Page page = WikipediaAPI.getFirstPage("XML");
        List<AttributeSet> atsetlist = new InfoboxParser().parse(page.toString());
        assertEquals(2,atsetlist.size());
        assertEquals("file format", atsetlist.get(0).getName());
        assertEquals("technology standard", atsetlist.get(1).getName());
    }
    
    @Test
    public void testInfoboxRetrievalTwoSets2(){
        System.out.println("infoboxes of article MATLAB");
        Page page = WikipediaAPI.getFirstPage("MATLAB");
        List<AttributeSet> atsetlist = new InfoboxParser().parse(page.toString());
        assertEquals(2,atsetlist.size());
        assertEquals("software", atsetlist.get(0).getName());
        assertEquals("software", atsetlist.get(0).getURIName());
        assertEquals("programming language", atsetlist.get(1).getName());
        assertEquals("programming_language", atsetlist.get(1).getURIName());
    }

    /**
     * example for annotation with {{Cat more|(name)}}
     */
    @Test
    public void testMainEntityRetrieval1() {
        System.out.println("Main entity computer languages");
        Page page = WikipediaAPI.getFirstPage("Category:Computer languages");
        Category testcl = new Category();
        testcl.setName("Computer languages");
        MyCrawler instance = new MyCrawler(null, testcl);
        instance.retrieveMainEntity(page.toString());
        Entity entity = testcl.getMainEntity();
        String ename = entity.getName();
        assertEquals("Computer language", ename);
    }

    /**
     * example for annotation with {{Cat main|(name)}}
     */
    @Test
    public void testMainEntityRetrieval2() {
        System.out.println("Main entity XML");
        Page page = WikipediaAPI.getFirstPage("Category:XML");
        Category testcl = new Category();
        testcl.setName("XML");
        MyCrawler instance = new MyCrawler(null, testcl);
        instance.retrieveMainEntity(page.toString());
        Entity entity = testcl.getMainEntity();
        String ename = entity.getName();
        assertEquals("XML", ename);
    }

    /**
     * example for annotation with {{Cat main}}
     */
    @Test
    public void testMainEntityRetrieval3() {
        System.out.println("Main entity PostgreSQL");
        Page page = WikipediaAPI.getFirstPage("Category:PostgreSQL");
        Category testcl = new Category();
        testcl.setName("PostgreSQL");
        MyCrawler instance = new MyCrawler(null, testcl);
        instance.retrieveMainEntity(page.toString());
        Entity entity = testcl.getMainEntity();
        String ename = entity.getName();
        assertEquals("PostgreSQL", ename);
    }
}
