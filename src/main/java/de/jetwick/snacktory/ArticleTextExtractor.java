package de.jetwick.snacktory;

import static de.jetwick.snacktory.utils.DateUtils.*;
import static de.jetwick.snacktory.utils.Utf8Utils.*;

import de.jetwick.snacktory.metadata.MetadataExtractor;
import de.jetwick.snacktory.output.OutputFormatter;
import de.jetwick.snacktory.output.PlainTextOutputFormatter;
import de.jetwick.snacktory.utils.SHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is thread safe.
 * Class for content extraction from string form of webpage
 * 'extractContent' is main call from external programs/classes
 *
 * @author Alex P (ifesdjeen from jreadability)
 * @author Peter Karich
 */
public class ArticleTextExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleTextExtractor.class);
    private static final OutputFormatter DEFAULT_FORMATTER = new PlainTextOutputFormatter();

    private static final int MAX_AUTHOR_NAME_LENGHT = 255;
    private static final int MAX_AUTHOR_DESC_LENGHT = 1000;
    private static final int MAX_IMAGE_LENGHT = 255;

    private BestNodeFinder bestNodeFinder = new BestNodeFinder();
    private MetadataExtractor metadataExtractor = new MetadataExtractor();

    public JResult extractContent(String html) throws Exception {
        return extractContent(html, -1);
    }

    public JResult extractContent(String html, OutputFormatter formatter) throws Exception {
        return extractContent(html, formatter, -1);
    }

    public JResult extractContent(String html, int maxContentSize) throws Exception {
        return extractContent(new JResult(), html, DEFAULT_FORMATTER, true, maxContentSize);
    }

    public JResult extractContent(String html, OutputFormatter formatter, int maxContentSize) throws Exception {
        return extractContent(new JResult(), html, formatter, true, maxContentSize);
    }

    public JResult extractContent(JResult res, String html, int maxContentSize) throws Exception {
        return extractContent(res, html, DEFAULT_FORMATTER, true, maxContentSize);
    }

    public JResult extractContent(JResult res, String html, OutputFormatter formatter,
                                  Boolean extractimages, int maxContentSize) throws Exception {
        if (html.isEmpty())
            throw new IllegalArgumentException("html string is empty!?");

        // http://jsoup.org/cookbook/extracting-data/selector-syntax
        return extractContent(res, Jsoup.parse(html), formatter, extractimages, maxContentSize);
    }

    public JResult extractContent(JResult res, Document doc, OutputFormatter formatter,
                                  Boolean extractimages, int maxContentSize) throws Exception {
        Document origDoc = doc.clone();
        JResult result = extractContent(res, doc, formatter, extractimages, maxContentSize, true);
        //System.out.println("result.getText().length()="+result.getText().length());
        if (result.getText().length() == 0) {
            result = extractContent(res, origDoc, formatter, extractimages, maxContentSize, false);
        }
        return result;
    }

    private static String getSnippet(String data){
        if (data.length() < 50)
            return data;
        else
            return data.substring(0, 50);
    }

    // main workhorse
    public JResult extractContent(JResult res, Document doc, OutputFormatter formatter,
                                  Boolean extractimages, int maxContentSize, boolean cleanScripts) throws Exception {
        if (doc == null)
            throw new NullPointerException("missing document");

        // get the easy stuff
        res.setTitle(metadataExtractor.extractTitle(doc));
        res.setDescription(metadataExtractor.extractDescription(doc));
        res.setCanonicalUrl(metadataExtractor.extractCanonicalUrl(doc));
        res.setType(metadataExtractor.extractType(doc));
        res.setSitename(metadataExtractor.extractSitename(doc));
        res.setLanguage(metadataExtractor.extractLanguage(doc));

        // get author information
        res.setAuthorName(metadataExtractor.extractAuthorName(doc));
        res.setAuthorDescription(metadataExtractor.extractAuthorDescription(doc, res.getAuthorName()));

        // add extra selection gravity to any element containing author name
        // wasn't useful in the case I implemented it for, but might be later
        /*
        Elements authelems = doc.select(":containsOwn(" + res.getAuthorName() + ")");
        for (Element elem : authelems) {
            elem.attr("extragravityscore", Integer.toString(100));
            System.out.println("modified element " + elem.toString());
        }
        */

        // get date from document, if not present, extract from URL if possible
        Date docdate = metadataExtractor.extractDate(doc);
        if (docdate == null) {
            String dateStr = SHelper.estimateDate(res.getUrl());
            docdate = parseDate(dateStr);
            res.setDate(docdate);
        } else {
            res.setDate(docdate);
        }

        // now remove the clutter
        if (cleanScripts) {
            removeScriptsAndStyles(doc);
        }

        Element bestMatchElement = bestNodeFinder.find(doc);

        // do extraction from the best element
        if (bestMatchElement != null) {
            if (extractimages) {
                List<ImageResult> images = new ArrayList<ImageResult>();
                Element imgEl = ImageExtractor.extractImages(bestMatchElement, images);
                if (imgEl != null) {
                    res.setImageUrl(SHelper.replaceSpaces(imgEl.attr("src")));
                    // TODO remove parent container of image if it is contained in bestMatchElement
                    // to avoid image subtitles flooding in

                    res.setImages(images);
                }
            }

            // clean before grabbing text
            String text = formatter.format(bestMatchElement);
            // this fails for short facebook post and probably tweets: text.length() > res.getDescription().length()
            if (text.length() > res.getTitle().length()) {
                if (maxContentSize > 0){
                    if (text.length() > maxContentSize){
                        text = utf8truncate(text, maxContentSize);
                    }
                }
                res.setText(text);
            }

            // extract links from the same best element
            String fullhtml = bestMatchElement.toString();
            Elements children = bestMatchElement.select("a[href]"); // a with href = link
            String linkstr = "";
            Integer linkpos = 0;
            Integer lastlinkpos = 0;
            for (Element child : children) {
                linkstr = child.toString();
                linkpos = fullhtml.indexOf(linkstr, lastlinkpos);
                res.addLink(child.attr("abs:href"), child.text(), linkpos);
                lastlinkpos = linkpos;
            }
        }

        if (extractimages) {
            if (res.getImageUrl().isEmpty()) {
                res.setImageUrl(metadataExtractor.extractImageUrl(doc));
            }
        }

        res.setRssUrl(metadataExtractor.extractRssUrl(doc));
        res.setVideoUrl(metadataExtractor.extractVideoUrl(doc));
        res.setFaviconUrl(metadataExtractor.extractFaviconUrl(doc));
        res.setKeywords(metadataExtractor.extractKeywords(doc));

        // Sanity checks in author
        if (res.getAuthorName().length() > MAX_AUTHOR_NAME_LENGHT){
            res.setAuthorName(utf8truncate(res.getAuthorName(), MAX_AUTHOR_NAME_LENGHT));
        }

        // Sanity checks in author description.
        String authorDescSnippet = getSnippet(res.getAuthorDescription());
        if (getSnippet(res.getText()).equals(authorDescSnippet) ||
                getSnippet(res.getDescription()).equals(authorDescSnippet)) {
            res.setAuthorDescription("");
        } else {
            if (res.getAuthorDescription().length() > MAX_AUTHOR_DESC_LENGHT){
                res.setAuthorDescription(utf8truncate(res.getAuthorDescription(), MAX_AUTHOR_DESC_LENGHT));
            }
        }

        // Sanity checks in image name
        if (res.getImageUrl().length() > MAX_IMAGE_LENGHT){
            // doesn't make sense to truncate a URL
            res.setImageUrl("");
        }

        return res;
    }

    private Document removeScriptsAndStyles(Document doc) {
        Elements scripts = doc.getElementsByTag("script");
        for (Element item : scripts) {
            item.remove();
        }
        Elements noscripts = doc.getElementsByTag("noscript");
        for (Element item : noscripts) {
            item.remove();
        }

        Elements styles = doc.getElementsByTag("style");
        for (Element style : styles) {
            style.remove();
        }

        return doc;
    }

}
