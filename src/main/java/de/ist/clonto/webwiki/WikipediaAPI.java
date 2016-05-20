/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki;

import info.bliki.api.Connector;
import info.bliki.api.Page;
import info.bliki.api.PageInfo;
import info.bliki.api.User;
import info.bliki.api.XMLCategoryMembersParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;

/**
 * This class provides convenience functions for accessing Wikipedia content
 * through the Bliki API (https://code.google.com/p/gwtwiki/)
 *
 * @author dmosen@uni-koblenz.de
 * @author heinz@uni-koblenz.de
 *
 */
public class WikipediaAPI {

    public static final String URL_PREFIX = "http://en.wikipedia.org/";
    public static final String PAGE_URL_PREFIX = URL_PREFIX + "wiki/";
    public static final String CATEGORY_URL_PREFIX = PAGE_URL_PREFIX + "Category:";
    public static final String API_URL = URL_PREFIX + "w/api.php";

    private final static User user = new User("", "", API_URL);

    private final static Connector connector = new Connector();

    /**
     * Returns a list of strings of direct subcategories for a given category
     * string <br>
     * <br>
     * i.e. <code>"Category:Computer_languages"</code>
     *
     * @param category
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws InterruptedException
     */
    public static List<String> getSubCategories(String category)
            throws SAXException, IOException, InterruptedException {
        return getCategoryMembers(category, Cmtype.SUBCAT);
    }

    public static boolean isValidCategory(String title) {
        title = "Category:" + title;
        List<Page> pages = connector.queryInfo(user,
                Arrays.asList(new String[]{title}));
        if (!pages.isEmpty()) {
            Page page = pages.get(0);
            if (page.getPageid() != null) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getPages(String category) throws SAXException,
            IOException, InterruptedException {
        return getCategoryMembers(category, Cmtype.PAGE);
    }

    public static List<String> getCategoryMembers(String category, Cmtype cmtype)
            throws SAXException, IOException, InterruptedException {
        List<String> result = new ArrayList<String>();
        String cmcontinue = "";
        //, "clshow", "!hidden"
        do {
            String[] query = new String[]{"list", "categorymembers",
                "cmtitle", "Category:" + category, "cmtype", cmtype.id,
                "cmlimit", "max", "cmcontinue", cmcontinue};

            String rawXmlResponse = processQuery(query);

            XMLCategoryMembersParser parser = new XMLCategoryMembersParser(
                    rawXmlResponse);

            parser.parse();

            List<PageInfo> pages = parser.getPagesList();

            //BUGFIX Imagine the title ?: as in http://en.wikipedia.org/wiki/%3F:
            //This will remove the title and return an empty String
            //That's why you can't do this in a generic manner
            for (PageInfo p : pages) {
                String title = p.getTitle();
                if (title.contains("Category:")) {
                    title = title.replaceFirst(".*:", "");
                }
                result.add(title);
            }

            cmcontinue = parser.getCmContinue();
        } while (cmcontinue != "");

        return result;
    }

    public static Page getFirstPage(String title) {
        String[] listOfTitleStrings = {title};
        user.login();

        List<Page> listOfPages = user.queryContent(listOfTitleStrings);
        Page result = null;
        try {
            result = listOfPages.get(0);

        } catch (Exception e) {
            System.out.println("Error at Wikipedia access for: " + title );
            System.exit(0);
        }
        if (result != null) {
            Pattern pattern = Pattern.compile("\\{\\{non-diffusing subcategory\\}\\}",
                    Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(result.toString());
            if(matcher.find())
                System.out.println(title);
        }
        return result;
    }

    public static boolean checkExistence(String title) {
        String[] listOfTitleStrings = {title};
        user.login();

        List<Page> listOfPages = user.queryContent(listOfTitleStrings);
        return !listOfPages.isEmpty();
    }

    private static String processQuery(String[] query)
            throws InterruptedException {
        int i = 0;
        String rawXmlResponse = null;

        do {
            if (i != 0) {
                System.out.println("Connection Error, sleeping 1000 ms.");
                Thread.sleep(1000);
            }
           rawXmlResponse = connector.queryXML(user, query);
        } while (rawXmlResponse == null && i < 3);
        return rawXmlResponse;
    }
    
    public static void main(String[] args) {
    	 User user = new User("", "", "http://en.wikipedia.org/w/api.php");
         user.login();
         String[] valuePairs = { "list", "categorymembers", "cmtitle", "Category:Physics" };
         Connector connector = new Connector();
         String rawXmlResponse = connector.queryXML(user, valuePairs);
         if (rawXmlResponse == null) {
             System.out.println("Got no XML result for the query");
         }
         System.out.println(rawXmlResponse);

         // When more results are available, use "cmcontinue" from last query result
         // to continue
         String[] valuePairs2 = { "list", "categorymembers", "cmtitle", "Category:Physics", "cmcontinue", "Awards|" };
         rawXmlResponse = connector.queryXML(user, valuePairs2);
         if (rawXmlResponse == null) {
             System.out.println("Got no XML result for the query");
         }
         System.out.println(rawXmlResponse);
	}
}
