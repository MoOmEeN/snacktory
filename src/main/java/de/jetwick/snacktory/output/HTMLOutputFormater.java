package de.jetwick.snacktory.output;

import de.jetwick.snacktory.utils.SHelper;
import org.jsoup.nodes.Element;

public class HTMLOutputFormater extends OutputFormatter {

  private final static String NODES_TO_KEEP = "p, ol, img";

  @Override
  public String doFormat(Element node) {
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
      String text = element.toString();
      if (text.isEmpty() || text.length() < getMinParagraph(paragraphWithTextIndex)
          || text.length() > SHelper.countLetters(text) * 2){
        continue;
      }

//      if (element.tagName().equals("p")){
//        paragraphCounter++;
//      }

      sb.append(text);
      sb.append("\n\n");
      paragraphWithTextIndex+=1;
    }

    return sb.toString();
  }

  @Override
  protected String getNodesToKeep() {
    return NODES_TO_KEEP;
  }
}
