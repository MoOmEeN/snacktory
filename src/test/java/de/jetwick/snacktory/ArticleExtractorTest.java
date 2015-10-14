package de.jetwick.snacktory;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ArticleExtractorTest {

  ArticleTextExtractor extractor;
  Converter c;

  @Before
  public void setup() throws Exception {
    c = new Converter();
    extractor = new ArticleTextExtractor();
  }

  @Test
  public void testExtractContent_longHiddenText() throws Exception {
    JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("no-hidden.html")));
    assertEquals("This is the text which is shorter but visible", res.getText());
  }

  @Test
  public void testExtract_longTextWithDisplayBlock() throws Exception {
    JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("no-hidden2.html")));
    assertEquals("This is the NONE-HIDDEN text which shouldn't be shown and it is a bit longer so normally prefered", res.getText());
  }

  @Test
  public void testExtract_skipATag() throws Exception {
    JResult res = extractor.extractContent("<html><body><div> aaa<a> bbb </a>ccc</div></body></html>");
    assertEquals("aaa bbb ccc", res.getText());
  }

  @Test
  public void testExtract_removeAdditionalSpaces() throws Exception {
    JResult res = extractor.extractContent("<html><body><div> aaa <strong> bbb </strong>ccc</div></body></html>");
    assertEquals("aaa bbb ccc", res.getText());
  }
}
