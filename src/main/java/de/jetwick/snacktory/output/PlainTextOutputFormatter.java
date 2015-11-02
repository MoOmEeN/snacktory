package de.jetwick.snacktory.output;

import de.jetwick.snacktory.utils.SHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * @author goose | jim
 * @author karussell
 *
 * this class will be responsible for taking our top node and stripping out junk
 * we don't want and getting it ready for how we want it presented to the user
 */
public class PlainTextOutputFormatter extends OutputFormatter{

    private final static String NODES_TO_KEEP = "p, ol";
    private final static int MIN_TEXT_LENGTH = 100;

    @Override
    protected String getNodesToKeep() {
        return NODES_TO_KEEP;
    }

    /**
     * takes an element and turns the P tags into \n\n
     */
    @Override
    public String doFormat(Element node) {
        ExtractResult extractResult = extractText(node);
        String text = SHelper.innerTrim(extractResult.text);

        int topNodeLength = node.text().length();
        if (topNodeLength == 0) {
            topNodeLength = 1;
        }

        boolean lowTextRatio = ((text.length() / (topNodeLength * 1.0)) < 0.25);
        if (text.length() > MIN_TEXT_LENGTH && extractResult.paragraphCount > 0 && !lowTextRatio){
            return text;
        }

        // no subelements
        boolean noSubelements = text.isEmpty() || (!node.text().isEmpty()
            && text.length() <= node.ownText().length())
            || extractResult.paragraphCount == 0 || lowTextRatio;
        if (noSubelements){
            text = node.text();
        }

        // if jsoup failed to parse the whole html now parse this smaller 
        // snippet again to avoid html tags disturbing our text:
        return Jsoup.parse(text).text();
    }

    protected ExtractResult extractText(Element node) {
        int paragraphCounter = 0; // Number of P elements in the article
        int paragraphWithTextIndex = 0;

        StringBuilder sb = new StringBuilder();
        // is select more costly then getElementsByTag?
        for (Element element : node.select(getNodesToKeep())) {
            boolean isUnlikely = isUnlikely(element, node);
            if (isUnlikely){
                continue;
            }
//            if (inlineImages){
//                boolean isImage = element.tagName().equals("img");
//                if (isImage){
//                    Element img = new Element(Tag.valueOf("img"), "");
//                    img.attr("src", element.attr("src"));
//                    sb.append(img.toString());
//                }
//            }
            String text = node2Text(element);
            if (text.isEmpty() || text.length() < getMinParagraph(paragraphWithTextIndex) 
                || text.length() > SHelper.countLetters(text) * 2){
                continue;
            }

            if (element.tagName().equals("p")){
                paragraphCounter++;
            }

            sb.append(text);
            sb.append("\n\n");
            paragraphWithTextIndex+=1;
        }

        return new ExtractResult(sb.toString(), paragraphCounter);
    }

    private String notHiddenText(Element node) {
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

    boolean lastCharIsWhitespace(StringBuilder accum) {
        if (accum.length() == 0)
            return false;
        return Character.isWhitespace(accum.charAt(accum.length() - 1));
    }

    protected String node2Text(Element el) {
        return notHiddenText(el);
    }

    private class ExtractResult {
        String text;
        int paragraphCount;
        ExtractResult(String text, int paragraphCount){
            this.text = text;
            this.paragraphCount = paragraphCount;
        }
    }
}
