package de.jetwick.snacktory.output;

import de.jetwick.snacktory.ImageExtractor;
import de.jetwick.snacktory.ImageResult;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public abstract class OutputFormatter {

  private static final int DEFAULT_MIN_FIRST_PARAGRAPH_TEXT = 50; // Min size of first paragraph
  private static final int DEFAULT_MIN_PARAGRAPH_TEXT = 30;       // Min size of any other paragraphs
  private static final List<String> DEFAULT_NODES_TO_REPLACE = Arrays.asList("strong", "b", "i");
  private static final Pattern HIDDEN_PATTERN = Pattern.compile("display\\:none|visibility\\:hidden");

  protected final int minFirstParagraphText;
  protected final int minParagraphText;
  protected final List<String> nodesToReplace;

  protected OutputFormatter(){
    this(DEFAULT_MIN_FIRST_PARAGRAPH_TEXT, DEFAULT_MIN_PARAGRAPH_TEXT, DEFAULT_NODES_TO_REPLACE);
  }

  protected OutputFormatter(int minFirstParagraphText, int minParagraphText,
                         List<String> nodesToReplace){
    this.minFirstParagraphText = minFirstParagraphText;
    this.minParagraphText = minParagraphText;
    this.nodesToReplace = nodesToReplace;
  }

  public String format(Element node){
    setParagraphIndex(node, getNodesToKeep());
    removeNodesWithNegativeScores(node);

    return doFormat(node);
  }

  protected abstract String doFormat(Element node);
  protected abstract String getNodesToKeep();

  protected void setParagraphIndex(Element node, String tagName) {
    int paragraphIndex = 0;
    for (Element e : node.select(tagName)) {
      e.attr("paragraphIndex", Integer.toString(paragraphIndex++));
    }
  }

  /**
   * If there are elements inside our top node that have a negative gravity
   * score remove them
   */
  protected void removeNodesWithNegativeScores(Element topNode) {
    Elements gravityItems = topNode.select("*[gravityScore]");

    for (Element item : gravityItems) {
      int score = getScore(item);
      int paragraphIndex = getParagraphIndex(item);
      if (score < 0 || item.text().length() < getMinParagraph(paragraphIndex)){
//                Elements imgs = ImageExtractor.getImages(item);
        Elements imgs = item.select("img");
        for (Element img : imgs){
          ImageResult image = ImageExtractor.analyzeImage(img);
          if (image == null){
            continue;
          }
          boolean tooSmall = (image.weight != 0 && image.width < 100) ||
              (image.height != 0 && image.height < 50);
          if (!tooSmall) {
            Element imgToAdd = new Element(Tag.valueOf("img"), "");
            imgToAdd.attr("src", image.src);
            item.after(imgToAdd);
          }
        }
        item.remove();
      }
    }
  }

  protected int getParagraphIndex(Element el){
    try {
      return Integer.parseInt(el.attr("paragraphIndex"));
    } catch(NumberFormatException ex) {
      return -1;
    }
  }

  protected int getScore(Element el) {
    try {
      return Integer.parseInt(el.attr("gravityScore"));
    } catch (Exception ex) {
      return 0;
    }
  }

  protected int getMinParagraph(int paragraphIndex){
    if(paragraphIndex < 1){
      return minFirstParagraphText;
    } else {
      return minParagraphText;
    }
  }

  protected boolean isUnlikely(Element node, Element root){
    // check all elements until 'root'
    while (node != null && !node.equals(root)) {
      if (unlikely(node))
        return true;
      node = node.parent();
    }
    return false;
  }

  protected boolean unlikely(Node node) {
    if (isCaption(node) || isHidden(node)){
      return true;
    }
    return false;
  }

  protected boolean isCaption(Node node){
    if (node.attr("class") != null && node.attr("class").toLowerCase().contains("caption"))
      return true;
    return false;
  }

  protected boolean isHidden(Node node){
    String style = node.attr("style");
    String clazz = node.attr("class");
    if (HIDDEN_PATTERN.matcher(style).find() || HIDDEN_PATTERN.matcher(clazz).find())
      return true;
    return false;
  }
}
