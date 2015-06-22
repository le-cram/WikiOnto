/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ist.clonto.webwiki;

import de.ist.clonto.webwiki.model.Attribute;
import de.ist.clonto.webwiki.model.AttributeSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.UrlValidator;

/**
 *
 * @author Marcel
 *
 * Referencing to
 * https://code.google.com/p/gwtwiki/source/browse/trunk/info.bliki.wiki/bliki-core/src/main/java/info/bliki/wiki/dump/WikiPatternMatcher.java?r=1349
 * modified the idea here since multiple infoboxes can exist and they are only
 * able to retrieve one infobox per article sample: 'XML'. Provided a test
 * method in MyCrawlerTest.
 */
public class InfoboxParser {

    public List<AttributeSet> parse(String text) {
        String pagetext = replaceHTMLComments(text);
        List<AttributeSet> setlist = new ArrayList<AttributeSet>();

        Pattern pattern = Pattern.compile("\\{\\{\\s*infobox",
                Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(pagetext);
        while (matcher.find()) {
            int begin = matcher.start();
            int bracketnr = 2;
            int end = begin + matcher.group().length();
            while (end < pagetext.length()) {
                switch (pagetext.charAt(end)) {
                    case '}':
                        bracketnr--;
                        break;
                    case '{':
                        bracketnr++;
                        break;
                }
                if (bracketnr == 0) {
                    break;
                }
                end++;
            }
            String infobox = pagetext.substring(begin, end);
            AttributeSet atset = parseSet(infobox);
            setlist.add(atset);
        }

        return setlist;
    }

    public AttributeSet parseSet(String infoboxtext) {

        AttributeSet attributeSet = new AttributeSet();

        String text = filterInfoboxMarkup(infoboxtext);

        String ibname = retrieveInfoboxName(text);
        attributeSet.setName(ibname);

        //Match attributes
        Pattern pattern = Pattern.compile("[^|]*=.*");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String match = matcher.group();
            match = match.trim();
            String[] parts = match.split("=");

            if (parts.length == 1) {
                continue;
            }

            //parts[0] is the name, parts[1] the value
            String name = parts[0].trim();

            String value = parts[1].trim();

            for (String val : value.split(",")) {
                Attribute at = new Attribute();
                at.setName(name);
                at.setValue(val.trim());
                attributeSet.addAttribute(at);
            }
        }

        return attributeSet;
    }

    private String filterInfoboxMarkup(String text) {
        text = replaceWikiAnchorsWithNames(text);
        text = removeExternalAnchors(text);
        text = removeReferences(text);
        return text;
    }

    private String replaceWikiAnchorsWithNames(String text) {
        //remove wikianchors and replace with their first value
        Pattern anchorPattern = Pattern.compile("\\[\\[.*?\\]\\]");
        Matcher anchorMatcher = anchorPattern.matcher(text);

        while (anchorMatcher.find()) {
            String anchor = anchorMatcher.group();
            String manchor = anchor.replaceAll("\\[", "");
            manchor = manchor.replaceAll("\\]", "");
            String[] manchorParts = manchor.split("\\|");
            text = text.replace(anchor, manchorParts[0]);
        }

        return text;
    }

    private String removeExternalAnchors(String text) {
        Pattern eanchorPattern = Pattern.compile("\\[.*?\\]");
        Matcher eanchorMatcher = eanchorPattern.matcher(text);

        while (eanchorMatcher.find()) {
            String anchor = eanchorMatcher.group();
            String manchor = anchor.replaceAll("\\[", "");
            manchor = manchor.replaceAll("\\]", "");
            String[] manchorParts = manchor.split("\\s");
            UrlValidator validator = new UrlValidator();
            if (validator.isValid(manchorParts[0])) {
                text = text.replace(anchor, manchor);
            }
        }

        return text;
    }

    private String removeReferences(String text) {
        text = Pattern.compile("<ref name=\".*?\">.*?</ref>", Pattern.MULTILINE | Pattern.DOTALL).matcher(text).replaceAll("");
        text = Pattern.compile("<ref name=\".*?\"\\s/>", Pattern.MULTILINE | Pattern.DOTALL).matcher(text).replaceAll("");

        return text;
    }

    private String replaceHTMLComments(String text) {
        text = Pattern.compile("<!--.*?-->", Pattern.MULTILINE | Pattern.DOTALL).
                matcher(text).
                replaceAll("");

        return text;
    }

    private String retrieveInfoboxName(String text) {
        String headline = text.split("\n")[0];
        headline = text.split("\\|")[0]; // demo case: infobox vg|title="something"
        headline = headline.replaceAll("\\{", "");
        headline = headline.toLowerCase();
        headline = headline.replace("infobox", "");
        return headline.trim();
    }

}
