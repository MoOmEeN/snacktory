package de.jetwick.snacktory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author goose | jim
 * @author karussell
 *
 * this class will be responsible for taking our top node and stripping out junk
 * we don't want and getting it ready for how we want it presented to the user
 */
public class OutputFormatter {

    public static final int MIN_FIRST_PARAGRAPH_TEXT = 50; // Min size of first paragraph
    public static final int MIN_PARAGRAPH_TEXT = 30;       // Min size of any other paragraphs
    private static final List<String> NODES_TO_REPLACE = Arrays.asList("strong", "b", "i");
    private Pattern hiddenPattern = Pattern.compile("display\\:none|visibility\\:hidden");
    protected final int minFirstParagraphText;
    protected final int minParagraphText;
    protected final List<String> nodesToReplace;
    protected String nodesToKeepCssSelector = "p, ol, img";

    public OutputFormatter() {
        this(MIN_FIRST_PARAGRAPH_TEXT, MIN_PARAGRAPH_TEXT, NODES_TO_REPLACE);
    }

    public OutputFormatter(int minParagraphText) {
        this(minParagraphText, minParagraphText, NODES_TO_REPLACE);
    }

    public OutputFormatter(int minFirstParagraphText, int minParagraphText) {
        this(minFirstParagraphText, minParagraphText, NODES_TO_REPLACE);
    }

    public OutputFormatter(int minFirstParagraphText, int minParagraphText, 
                           List<String> nodesToReplace) {
        this.minFirstParagraphText = minFirstParagraphText;
        this.minParagraphText = minParagraphText;
        this.nodesToReplace = nodesToReplace;
    }

    /**
     * set elements to keep in output text
     */
    public void setNodesToKeepCssSelector(String nodesToKeepCssSelector) {
        this.nodesToKeepCssSelector = nodesToKeepCssSelector;
    }

    /**
     * takes an element and turns the P tags into \n\n
     */
    public String getFormattedText(Element topNode, boolean inlineImages) {
        setParagraphIndex(topNode, nodesToKeepCssSelector);
        removeNodesWithNegativeScores(topNode);
        StringBuilder sb = new StringBuilder();
        int countOfP = append(topNode, sb, nodesToKeepCssSelector, inlineImages);
        String str = SHelper.innerTrim(sb.toString());

        int topNodeLength = topNode.text().length();
        if (topNodeLength == 0) {
            topNodeLength = 1;
        }


        boolean lowTextRatio = ((str.length() / (topNodeLength * 1.0)) < 0.25);
        if (str.length() > 100 && countOfP > 0 && !lowTextRatio)
            return str;

        // no subelements
        boolean noSubelements = str.isEmpty() || (!topNode.text().isEmpty()
            && str.length() <= topNode.ownText().length())
            || countOfP == 0 || lowTextRatio;
        if (noSubelements){
            str = topNode.text();
        }

        // if jsoup failed to parse the whole html now parse this smaller 
        // snippet again to avoid html tags disturbing our text:
        return Jsoup.parse(str).text();
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

    protected int append(Element node, StringBuilder sb, String tagName, boolean inlineImages) {
        int countOfP = 0; // Number of P elements in the article
        int paragraphWithTextIndex = 0;
        // is select more costly then getElementsByTag?
        for (Element element : node.select(tagName)) {
            boolean isUnlikely = isUnlikely(element, node);
            if (isUnlikely){
                continue;
            }
            if (inlineImages){
                boolean isImage = element.tagName().equals("img");
                if (isImage){
                    Element img = new Element(Tag.valueOf("img"), "");
                    img.attr("src", element.attr("src"));
                    sb.append(img.toString());
                }
            }
            String text = node2Text(element);
            if (text.isEmpty() || text.length() < getMinParagraph(paragraphWithTextIndex) 
                || text.length() > SHelper.countLetters(text) * 2){
                continue;
            }

            if (element.tagName().equals("p")){
                countOfP++;
            }

            sb.append(text);
            sb.append("\n\n");
            paragraphWithTextIndex+=1;
        }

        return countOfP;
    }

    private boolean isUnlikely(Element node, Element root){
        // check all elements until 'root'
        while (node != null && !node.equals(root)) {
            if (unlikely(node))
                return true;
            node = node.parent();
        }
        return false;
    }

    protected void setParagraphIndex(Element node, String tagName) {
        int paragraphIndex = 0;
        for (Element e : node.select(tagName)) {
            e.attr("paragraphIndex", Integer.toString(paragraphIndex++));
        }
    }

    protected int getMinParagraph(int paragraphIndex){
        if(paragraphIndex < 1){
            return minFirstParagraphText;
        } else {
            return minParagraphText;
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

    private boolean unlikely(Node node) {
        if (isCaption(node) || isHidden(node)){
            return true;
        }
        return false;
    }

    private boolean isCaption(Node node){
        if (node.attr("class") != null && node.attr("class").toLowerCase().contains("caption"))
            return true;
        return false;
    }

    private boolean isHidden(Node node){
        String style = node.attr("style");
        String clazz = node.attr("class");
        if (hiddenPattern.matcher(style).find() || hiddenPattern.matcher(clazz).find())
            return true;
        return false;
    }


    String notHiddenText(Element node) {
        StringBuilder sb = new StringBuilder();
        for (Node child : node.childNodes()) {
            if (unlikely(child)){
                continue;
            }
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode) child;
                String txt = textNode.text();
                sb.append(txt);
            } else if (child instanceof Element) {
                Element childElement = (Element) child;
                boolean isBr = childElement.tagName().equals("br");
                if (sb.length() > 0 && childElement.isBlock()
                    && !lastCharIsWhitespace(sb))
                    sb.append(" ");
                else if (isBr)
//                if (isBr)
                    sb.append(" ");
                String childText = notHiddenText(childElement);
                sb.append(childText);
            }
        }
        return sb.toString();
    }

    void printWithIndent(String text, int indent){
        if (indent <= 0) {
            System.out.println(text);
        } else {
            System.out.println(String.format("%1$" + (indent*2) +"s%2$s", " ", text));
        }
    }

    boolean lastCharIsWhitespace(StringBuilder accum) {
        if (accum.length() == 0)
            return false;
        return Character.isWhitespace(accum.charAt(accum.length() - 1));
    }

    protected String node2TextOld(Element el) {
        return el.text();
    }

    protected String node2Text(Element el) {
        return notHiddenText(el);
    }

    public OutputFormatter setHiddenPattern(String hiddenPattern) {
        this.hiddenPattern = Pattern.compile(hiddenPattern);
        return this;
    }

    public OutputFormatter appendUnlikelyPattern(String str) {
        return setHiddenPattern(hiddenPattern.toString() + "|" + str);
    }
}
