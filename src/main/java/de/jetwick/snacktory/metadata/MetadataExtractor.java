package de.jetwick.snacktory.metadata;

import static de.jetwick.snacktory.utils.DateUtils.parseDate;

import de.jetwick.snacktory.BestNodeFinder;
import de.jetwick.snacktory.utils.SHelper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetadataExtractor {

  private static final Pattern IGNORE_AUTHOR_PARTS =
      Pattern.compile("by|name|author|posted|twitter|handle|news", Pattern.CASE_INSENSITIVE);

  private static final Set<String> IGNORED_TITLE_PARTS = new LinkedHashSet<String>() {
    {
      add("hacker news");
      add("facebook");
      add("home");
      add("articles");
    }
  };

  private static final List<Pattern> CLEAN_AUTHOR_PATTERNS = Arrays.asList(
      Pattern.compile("By\\S*(.*)[\\.,].*")
  );

  private static final int MIN_AUTHOR_NAME_LENGTH = 4;

  private BestNodeFinder bestNodeFinder = new BestNodeFinder();

  public String extractTitle(Document doc) {
    String title = cleanTitle(doc.title());
    if (title.isEmpty()) {
      title = SHelper.innerTrim(doc.select("head title").text());
      if (title.isEmpty()) {
        title = SHelper.innerTrim(doc.select("head meta[name=title]").attr("content"));
        if (title.isEmpty()) {
          title = SHelper.innerTrim(doc.select("head meta[property=og:title]").attr("content"));
          if (title.isEmpty()) {
            title = SHelper.innerTrim(doc.select("head meta[name=twitter:title]").attr("content"));
            if (title.isEmpty()) {
              title = SHelper.innerTrim(doc.select("h1:first-of-type").text());
            }
          }
        }
      }
    }
    return title;
  }

  private String cleanTitle(String title) {
    StringBuilder res = new StringBuilder();
//        int index = title.lastIndexOf("|");
//        if (index > 0 && title.length() / 2 < index)
//            title = title.substring(0, index + 1);

    int counter = 0;
    String[] strs = title.split("\\|");
    for (String part : strs) {
      if (IGNORED_TITLE_PARTS.contains(part.toLowerCase().trim()))
        continue;

      if (counter == strs.length - 1 && res.length() > part.length())
        continue;

      if (counter > 0)
        res.append("|");

      res.append(part);
      counter++;
    }

    return SHelper.innerTrim(res.toString());
  }

  public String extractCanonicalUrl(Document doc) {
    String url = SHelper.replaceSpaces(doc.select("head link[rel=canonical]").attr("href"));
    if (url.isEmpty()) {
      url = SHelper.replaceSpaces(doc.select("head meta[property=og:url]").attr("content"));
      if (url.isEmpty()) {
        url = SHelper.replaceSpaces(doc.select("head meta[name=twitter:url]").attr("content"));
      }
    }
    return url;
  }

  public String extractDescription(Document doc) {
    String description = SHelper.innerTrim(doc.select("head meta[name=description]").attr("content"));
    if (description.isEmpty()) {
      description = SHelper.innerTrim(doc.select("head meta[property=og:description]").attr("content"));
      if (description.isEmpty()) {
        description = SHelper.innerTrim(doc.select("head meta[name=twitter:description]").attr("content"));
      }
    }
    return description;
  }

  // Returns the publication Date or null
  public Date extractDate(Document doc) {
    String dateStr = "";

    // try some locations that nytimes uses
    Element elem = doc.select("meta[name=ptime]").first();
    if (elem != null) {
      dateStr = SHelper.innerTrim(elem.attr("content"));
      //            elem.attr("extragravityscore", Integer.toString(100));
      //            System.out.println("date modified element " + elem.toString());
    }

    if (dateStr == "") {
      dateStr = SHelper.innerTrim(doc.select("meta[name=utime]").attr("content"));
    }
    if (dateStr == "") {
      dateStr = SHelper.innerTrim(doc.select("meta[name=pdate]").attr("content"));
    }
    if (dateStr == "") {
      dateStr = SHelper.innerTrim(doc.select("meta[property=article:published]").attr("content"));
    }
    if (dateStr != "") {
      return parseDate(dateStr);
    }

    // taking this stuff directly from Juicer (and converted to Java)
    // opengraph (?)
    Elements elems = doc.select("meta[property=article:published_time]");
    if (elems.size() > 0) {
      Element el = elems.get(0);
      if (el.hasAttr("content")) {
        dateStr = el.attr("content");
        try {
          if (dateStr.endsWith("Z")) {
            dateStr = dateStr.substring(0, dateStr.length() - 1) + "GMT-00:00";
          } else {
            dateStr = "%sGMT%s".format(dateStr.substring(0, dateStr.length() - 6),
                dateStr.substring(dateStr.length() - 6,
                    dateStr.length()));
          }
        } catch(StringIndexOutOfBoundsException ex) {
          // do nothing
        }
        return parseDate(dateStr);
      }
    }

    // rnews
    elems = doc.select("meta[property=dateCreated], span[property=dateCreated]");
    if (elems.size() > 0) {
      Element el = elems.get(0);
      if (el.hasAttr("content")) {
        dateStr = el.attr("content");

        return parseDate(dateStr);
      } else {
        return parseDate(el.text());
      }
    }

    // schema.org creativework
    elems = doc.select("meta[itemprop=datePublished], span[itemprop=datePublished]");
    if (elems.size() > 0) {
      Element el = elems.get(0);
      if (el.hasAttr("content")) {
        dateStr = el.attr("content");

        return parseDate(dateStr);
      } else if (el.hasAttr("value")) {
        dateStr = el.attr("value");

        return parseDate(dateStr);
      } else {
        return parseDate(el.text());
      }
    }

    // parsely page (?)
        /*  skip conversion for now, seems highly specific and uses new lib
        elems = doc.select("meta[name=parsely-page]");
        if (elems.size() > 0) {
            implicit val formats = net.liftweb.json.DefaultFormats

                Element el = elems.get(0);
                if(el.hasAttr("content")) {
                    val json = parse(el.attr("content"))

                        return DateUtils.parseDateStrictly((json \ "pub_date").extract[String], Array("yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssZZ", "yyyy-MM-dd'T'HH:mm:ssz"))
                        }
            }
        */

    // BBC
    elems = doc.select("meta[name=OriginalPublicationDate]");
    if (elems.size() > 0) {
      Element el = elems.get(0);
      if (el.hasAttr("content")) {
        dateStr = el.attr("content");
        return parseDate(dateStr);
      }
    }

    // wired
    elems = doc.select("meta[name=DisplayDate]");
    if (elems.size() > 0) {
      Element el = elems.get(0);
      if (el.hasAttr("content")) {
        dateStr = el.attr("content");
        return parseDate(dateStr);
      }
    }

    // wildcard
    elems = doc.select("meta[name*=date]");
    if (elems.size() > 0) {
      Element el = elems.get(0);
      if (el.hasAttr("content")) {
        dateStr = el.attr("content");
        Date parsedDate = parseDate(dateStr);
        if (parsedDate != null){
          return parsedDate;
        }
      }
    }

    // blogger
    elems = doc.select(".date-header");
    if (elems.size() > 0) {
      Element el = elems.get(0);
      dateStr = el.text();
      return parseDate(dateStr);
    }

    return null;
  }


  // Returns the author name or null
  public String extractAuthorName(Document doc) {
    String authorName = "";

    // first try the Google Author tag
    Element result = doc.select("body [rel*=author]").first();
    if (result != null)
      authorName = SHelper.innerTrim(result.ownText());

    // if that doesn't work, try some other methods
    if (authorName.isEmpty()) {

      // meta tag approaches, get content
      result = doc.select("head meta[name=author]").first();
      if (result != null) {
        authorName = SHelper.innerTrim(result.attr("content"));
      }

      if (authorName.isEmpty()) {  // for "opengraph"
        authorName = SHelper.innerTrim(doc.select("head meta[property=article:author]").attr("content"));
      }
      if (authorName.isEmpty()) { // OpenGraph twitter:creator tag
        authorName = SHelper.innerTrim(doc.select("head meta[property=twitter:creator]").attr("content"));
      }
      if (authorName.isEmpty()) {  // for "schema.org creativework"
        authorName = SHelper.innerTrim(doc.select("meta[itemprop=author], span[itemprop=author]").attr("content"));
      }

      // other hacks
      if (authorName.isEmpty()) {
        try{
          // build up a set of elements which have likely author-related terms
          // .X searches for class X
          Elements matches = doc.select("a[rel=author],.byline-name,.byLineTag,.byline,.author,.by,.writer,.address");

          if(matches == null || matches.size() == 0){
            matches = doc.select("body [class*=author]");
          }

          if(matches == null || matches.size() == 0){
            matches = doc.select("body [title*=author]");
          }

          // a hack for huffington post
          if(matches == null || matches.size() == 0){
            matches = doc.select(".staff_info dl a[href]");
          }

          // a hack for http://sports.espn.go.com/
          if(matches == null || matches.size() == 0){
            matches = doc.select("cite[class*=source]");
          }

          // select the best element from them
          if(matches != null){
            Element bestMatch = bestNodeFinder.find(matches);

            if(!(bestMatch == null))
            {
              authorName = bestMatch.text();

              if(authorName.length() < MIN_AUTHOR_NAME_LENGTH){
                authorName = bestMatch.text();
              }

              authorName = SHelper.innerTrim(IGNORE_AUTHOR_PARTS.matcher(authorName).replaceAll(""));

              if(authorName.indexOf(",") != -1){
                authorName = authorName.split(",")[0];
              }
            }
          }
        }
        catch(Exception e){
          System.out.println(e.toString());
        }
      }
    }

    for (Pattern pattern : CLEAN_AUTHOR_PATTERNS) {
      Matcher matcher = pattern.matcher(authorName);
      if(matcher.matches()){
        authorName = SHelper.innerTrim(matcher.group(1));
        break;
      }
    }

    return authorName;
  }

  // Returns the author description or null
  public String extractAuthorDescription(Document doc, String authorName){

    String authorDesc = "";

    if(authorName.equals(""))
      return "";

    // Special case for entrepreneur.com
    Elements matches = doc.select(".byline > .bio");
    if (matches!= null && matches.size() > 0){
      Element bestMatch = matches.first(); // assume it is the first.
      authorDesc = bestMatch.text();
      return authorDesc;
    }

    // Special case for huffingtonpost.com
    matches = doc.select(".byline span[class*=teaser]");
    if (matches!= null && matches.size() > 0){
      Element bestMatch = matches.first(); // assume it is the first.
      authorDesc = bestMatch.text();
      return authorDesc;
    }

    try {
      Elements nodes = doc.select(":containsOwn(" + authorName + ")");
      Element bestMatch = bestNodeFinder.find(nodes);
      if (bestMatch != null)
        authorDesc = bestMatch.text();
    } catch(Selector.SelectorParseException se){
      // Avoid error when selector is invalid
    }

    return authorDesc;
  }

  public Collection<String> extractKeywords(Document doc) {
    String content = SHelper.innerTrim(doc.select("head meta[name=keywords]").attr("content"));

    if (content != null) {
      if (content.startsWith("[") && content.endsWith("]"))
        content = content.substring(1, content.length() - 1);

      String[] split = content.split("\\s*,\\s*");
      if (split.length > 1 || (split.length > 0 && !"".equals(split[0])))
        return Arrays.asList(split);
    }
    return Collections.emptyList();
  }

  /**
   * Tries to extract an image url from metadata if determineImageSource
   * failed
   *
   * @return image url or empty str
   */
  public String extractImageUrl(Document doc) {
    // use open graph tag to get image
    String imageUrl = SHelper.replaceSpaces(doc.select("head meta[property=og:image]").attr("content"));
    if (imageUrl.isEmpty()) {
      imageUrl = SHelper.replaceSpaces(doc.select("head meta[name=twitter:image]").attr("content"));
      if (imageUrl.isEmpty()) {
        // prefer link over thumbnail-meta if empty
        imageUrl = SHelper.replaceSpaces(doc.select("link[rel=image_src]").attr("href"));
        if (imageUrl.isEmpty()) {
          imageUrl = SHelper.replaceSpaces(doc.select("head meta[name=thumbnail]").attr("content"));
        }
      }
    }
    return imageUrl;
  }

  public String extractRssUrl(Document doc) {
    return SHelper.replaceSpaces(doc.select("link[rel=alternate]").select("link[type=application/rss+xml]").attr("href"));
  }

  public String extractVideoUrl(Document doc) {
    return SHelper.replaceSpaces(doc.select("head meta[property=og:video]").attr("content"));
  }

  public String extractFaviconUrl(Document doc) {
    String faviconUrl = SHelper.replaceSpaces(doc.select("head link[rel=icon]").attr("href"));
    if (faviconUrl.isEmpty()) {
      faviconUrl = SHelper.replaceSpaces(doc.select("head link[rel^=shortcut],link[rel$=icon]").attr("href"));
    }
    return faviconUrl;
  }

  public String extractType(Document doc) {
    String type = cleanTitle(doc.title());
    type = SHelper.innerTrim(doc.select("head meta[property=og:type]").attr("content"));
    return type;
  }

  public String extractSitename(Document doc) {
    String sitename = SHelper.innerTrim(doc.select("head meta[property=og:site_name]").attr("content"));
    if (sitename.isEmpty()) {
      sitename = SHelper.innerTrim(doc.select("head meta[name=twitter:site]").attr("content"));
    }
    if (sitename.isEmpty()) {
      sitename = SHelper.innerTrim(doc.select("head meta[property=og:site_name]").attr("content"));
    }
    return sitename;
  }

  public String extractLanguage(Document doc) {
    String language = SHelper.innerTrim(doc.select("head meta[property=language]").attr("content"));
    if (language.isEmpty()) {
      language = SHelper.innerTrim(doc.select("html").attr("lang"));
      if (language.isEmpty()) {
        language = SHelper.innerTrim(doc.select("head meta[property=og:locale]").attr("content"));
      }
    }
    if (!language.isEmpty()) {
      if (language.length()>2) {
        language = language.substring(0, 2);
      }
    }
    return language;
  }


}
