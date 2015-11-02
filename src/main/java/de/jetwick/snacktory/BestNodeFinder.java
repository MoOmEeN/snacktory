package de.jetwick.snacktory;

import de.jetwick.snacktory.utils.SHelper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BestNodeFinder {

  // Interessting nodes
  private static final Pattern NODES = Pattern.compile("p|div|td|h1|h2|article|section");

  // Unlikely candidates
  private Pattern UNLIKELY;
  // Most likely positive candidates
  private Pattern POSITIVE;
  // Most likely negative candidates
  private Pattern NEGATIVE;

  private static final Pattern NEGATIVE_STYLE =
      Pattern.compile("hidden|display: ?none|font-size: ?small");

  public BestNodeFinder(){
    setUnlikely("com(bx|ment|munity)|dis(qus|cuss)|e(xtra|[-]?mail)|foot|"
        + "header|menu|re(mark|ply)|rss|sh(are|outbox)|sponsor"
        + "a(d|ll|gegate|rchive|ttachment)|(pag(er|ination))|popup|print|"
        + "login|si(debar|gn|ngle)");
    setPositive("(^(body|content|h?entry|main|page|post|text|blog|story|haupt))"
        + "|arti(cle|kel)|instapaper_body");
    setNegative("nav($|igation)|user|com(ment|bx)|(^com-)|contact|"
        + "foot|masthead|(me(dia|ta))|outbrain|promo|related|scroll|(sho(utbox|pping))|"
        + "sidebar|sponsor|tags|tool|widget|player|disclaimer|toc|infobox|vcard");
  }

  private void setUnlikely(String unlikelyStr) {
    UNLIKELY = Pattern.compile(unlikelyStr);
  }

  private void setPositive(String positiveStr) {
    POSITIVE = Pattern.compile(positiveStr);
  }

  private void setNegative(String negativeStr) {
    NEGATIVE = Pattern.compile(negativeStr);
  }

  public Element find(Document doc){
    Collection<Element> nodes = getNodes(doc);
    return find(nodes);
  }

  /**
   * @return a set of all important nodes
   */
  public Collection<Element> getNodes(Document doc) {
    Map<Element, Object> nodes = new LinkedHashMap<Element, Object>(64);
    int score = 100;
    for (Element el : doc.select("body").select("*")) {
      if (NODES.matcher(el.tagName()).matches()) {
        nodes.put(el, null);
        setScore(el, score);
        score = score / 2;
      }
    }
    return nodes.keySet();
  }

  public Element find(Collection<Element> nodes){
    int maxWeight = -200;        // why -200 now instead of 0?
    Element bestMatchElement = null;

    boolean ignoreMaxWeightLimit = false;
    for (Element entry : nodes) {

      int currentWeight = getWeight(entry, false);

      if (currentWeight > maxWeight) {
        maxWeight = currentWeight;
        bestMatchElement = entry;

                /*
                // NOTE: This optimization fails with large pages that
                contains chunks of text that can be mistaken by articles, since we
                want the best accuracy possible, I am disabling it for now. AP.

                // The original code had a limit of 200, the intention was that
                // if a node had a weight greater than it, then it most likely
                // it was the main content.
                // However this assumption fails when the amount of text in the
                // children (or grandchildren) is too large. If we detect this
                // case then the limit is ignored and we try all the nodes to select
                // the one with the absolute maximum weight.
                if (maxWeight > 500){
                    ignoreMaxWeightLimit = true;
                    continue;
                }

                // formerly 200, increased to 250 to account for the fact
                // we are not adding the weights of the grand children to the
                // tally.

                if (maxWeight > 250 && !ignoreMaxWeightLimit)
                    break;
                */
      }
    }

    return bestMatchElement;
  }

  /**
   * Weights current element. By matching it with positive candidates and
   * weighting child nodes. Since it's impossible to predict which exactly
   * names, ids or class names will be used in HTML, major role is played by
   * child nodes
   *
   * @param e Element to weight, along with child nodes
   */
  private int getWeight(Element e, boolean checkextra) {
    int weight = calcWeight(e);
    int ownTextWeight = (int) Math.round(e.ownText().length() / 100.0 * 10);
    weight+=ownTextWeight;
    int childrenWeight = weightChildNodes(e);
    weight+=childrenWeight;

    // add additional weight using possible 'extragravityscore' attribute
    if (checkextra) {
      Element xelem = e.select("[extragravityscore]").first();
      if (xelem != null) {
        //                System.out.println("HERE found one: " + xelem.toString());
        weight += Integer.parseInt(xelem.attr("extragravityscore"));
        //                System.out.println("WITH WEIGHT: " + xelem.attr("extragravityscore"));
      }
    }

    return weight;
  }

  private int calcWeight(Element e) {
    int weight = 0;
    if (POSITIVE.matcher(e.className()).find())
      weight += 35;

    if (POSITIVE.matcher(e.id()).find())
      weight += 45;

    if (UNLIKELY.matcher(e.className()).find())
      weight -= 20;

    if (UNLIKELY.matcher(e.id()).find())
      weight -= 20;

    if (NEGATIVE.matcher(e.className()).find())
      weight -= 50;

    if (NEGATIVE.matcher(e.id()).find())
      weight -= 50;

    String style = e.attr("style");
    if (style != null && !style.isEmpty() && NEGATIVE_STYLE.matcher(style).find())
      weight -= 50;

    String itemprop = e.attr("itemprop");
    if (itemprop != null && !itemprop.isEmpty() && POSITIVE.matcher(itemprop).find()){
      weight += 100;
    }

    return weight;
  }

  /**
   * Weights a child nodes of given Element. During tests some difficulties
   * were met. For instance, not every single document has nested paragraph
   * tags inside of the major article tag. Sometimes people are adding one
   * more nesting level. So, we're adding 4 points for every 100 symbols
   * contained in tag nested inside of the current weighted element, but only
   * 3 points for every element that's nested 2 levels deep. This way we give
   * more chances to extract the element that has less nested levels,
   * increasing probability of the correct extraction.
   *
   * @param rootEl Element, who's child nodes will be weighted
   */
  private int weightChildNodes(Element rootEl) {
    int weight = 0;
    Element caption = null;
    List<Element> pEls = new ArrayList<Element>(5);

    for (Element child : rootEl.children()) {
      String ownText = child.ownText();
      int ownTextLength = ownText.length();
      if (ownTextLength < 20)
        continue;

//      if(logEntries!=null) {
//        logEntries.add("\t      CHILD TAG: " + child.tagName());
//      }

      if (ownTextLength > 200){
        int childOwnTextWeight = Math.max(50, ownTextLength / 10);
//        if(logEntries!=null)
//          logEntries.add("      CHILD TEXT WEIGHT:"
//              + String.format("%3d", childOwnTextWeight));
        weight += childOwnTextWeight;
      }

      if (child.tagName().equals("h1") || child.tagName().equals("h2")) {
        int h2h1Weight = 30;
        weight += h2h1Weight;
//        if(logEntries!=null)
//          logEntries.add("\t   H1/H2 WEIGHT:"
//              + String.format("%3d", h2h1Weight));
      } else if (child.tagName().equals("div") || child.tagName().equals("p")) {
        int calcChildWeight = calcWeightForChild(child, ownText);
        weight+=calcChildWeight;
//        if(logEntries!=null)
//          logEntries.add("\t   CHILD WEIGHT:"
//              + String.format("%3d", calcChildWeight));
        if (child.tagName().equals("p") && ownTextLength > 50)
          pEls.add(child);

        if (child.className().toLowerCase().equals("caption"))
          caption = child;
      }
    }

    //
    // Visit grandchildren, This section visits the grandchildren
    // of the node and calculate their weights. Note that grandchildren
    // weights are only worth 1/3 of children's
    //
    int grandChildrenWeight = 0;
    int grandChildrenCount = 0;
    for (Element child2 : rootEl.children()) {

//      if(logEntries!=null) {
//        logEntries.add("\t    CHILD TAG: " + child2.tagName());
//        //logEntries.add(child2.outerHtml());
//      }

      // If the node looks negative don't include it in the weights
      // instead penalize the grandparent. This is done to try to
      // avoid giving weigths to navigation nodes, etc.
      if (NEGATIVE.matcher(child2.id()).find() ||
          NEGATIVE.matcher(child2.className()).find()){
//        if(logEntries!=null){
//          logEntries.add("\t  CHILD DISCARDED");
//        }
        grandChildrenWeight-=30;
        continue;
      }

      for (Element grandchild : child2.children()) {
        int grandchildWeight = 0;
        String ownText = grandchild.ownText();
        int ownTextLength = ownText.length();
        if (ownTextLength < 20)
          continue;

//        if(logEntries!=null) {
//          logEntries.add("\t    GRANDCHILD TAG: " + grandchild.tagName());
//          //logEntries.add(grandchild.outerHtml());
//        }
        grandChildrenCount+=1;

        if (ownTextLength > 200){
          int childOwnTextWeight = Math.max(50, ownTextLength / 10);
//          if(logEntries!=null)
//            logEntries.add("    GRANDCHILD TEXT WEIGHT:"
//                + String.format("%3d", childOwnTextWeight));
          grandchildWeight += childOwnTextWeight;
        }

        if (grandchild.tagName().equals("h1") || grandchild.tagName().equals("h2")) {
          int h2h1Weight = 30;
          grandchildWeight += h2h1Weight;
//          if(logEntries!=null)
//            logEntries.add("   GRANDCHILD H1/H2 WEIGHT:"
//                + String.format("%3d", h2h1Weight));
        } else if (grandchild.tagName().equals("div") || grandchild.tagName().equals("p")) {
          int calcChildWeight = calcWeightForChild(grandchild, ownText);
          grandchildWeight+=calcChildWeight;
//          if(logEntries!=null)
//            logEntries.add("   GRANDCHILD CHILD WEIGHT:"
//                + String.format("%3d", calcChildWeight));
        }

//        if(logEntries!=null)
//          logEntries.add("\t GRANDCHILD WEIGHT:"
//              + String.format("%3d", grandchildWeight));
        grandChildrenWeight += grandchildWeight;
      }
    }

    if (grandChildrenCount <= 0)
      grandChildrenCount = 1;
    grandChildrenWeight = grandChildrenWeight / 3;
//    if(logEntries!=null){
//      logEntries.add("\t  GRANDCHILDREN WEIGHT:"
//          + String.format("%3d", grandChildrenWeight));
//      logEntries.add("\t   GRANDCHILDREN COUNT:"
//          + String.format("%3d", grandChildrenCount));
//    }
    weight+=grandChildrenWeight;

    // use caption and image
    if (caption != null){
      int captionWeight = 30;
      weight+=captionWeight;
//      if(logEntries!=null)
//        logEntries.add("\t CAPTION WEIGHT:"
//            + String.format("%3d", captionWeight));
    }

    if (pEls.size() >= 2) {
      for (Element subEl : rootEl.children()) {
        if ("h1;h2;h3;h4;h5;h6".contains(subEl.tagName())) {
          int h1h2h3Weight = 20;
          weight += h1h2h3Weight;
//          if(logEntries!=null)
//            logEntries.add("  h1;h2;h3;h4;h5;h6 WEIGHT:"
//                + String.format("%3d", h1h2h3Weight));
          // headerEls.add(subEl);
        } else if ("table;li;td;th".contains(subEl.tagName())) {
          addScore(subEl, -30);
        }

        if ("p".contains(subEl.tagName()))
          addScore(subEl, 30);
      }
    }
    return weight;
  }

  private void addScore(Element el, int score) {
    int old = getScore(el);
    setScore(el, score + old);
  }

  private int getScore(Element el) {
    int old = 0;
    try {
      old = Integer.parseInt(el.attr("gravityScore"));
    } catch (Exception ex) {
    }
    return old;
  }

  private void setScore(Element el, int score) {
    el.attr("gravityScore", Integer.toString(score));
  }

  private int calcWeightForChild(Element child, String ownText) {
    int c = SHelper.count(ownText, "&quot;");
    c += SHelper.count(ownText, "&lt;");
    c += SHelper.count(ownText, "&gt;");
    c += SHelper.count(ownText, "px");
    int val;
    if (c > 5)
      val = -30;
    else
      val = (int) Math.round(ownText.length() / 35.0);

    addScore(child, val);
    return val;
  }
}
