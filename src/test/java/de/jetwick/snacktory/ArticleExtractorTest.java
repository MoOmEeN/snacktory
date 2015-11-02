package de.jetwick.snacktory;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ArticleExtractorTest {

  ArticleTextExtractor extractor;

  @Before
  public void setup() throws Exception {
    extractor = new ArticleTextExtractor();
  }

  @Test
  public void testExtractContent_longHiddenText() throws Exception {
    String LONG_HIDDEN_TEXT = "This is the hidden text which shouldn't be shown and it is a bit longer so normally prefered";
    String SHORT_VISIBLE_TEXT = "This is the text which is shorter but visible";

    // given
    String html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
        "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" +
        "    <head>\n" +
        "        <meta content='text/html; charset=UTF-8' http-equiv='Content-Type'/>\n" +
        "    </head>\n" +
        "    <body>\n" +
        "        <div style=\"margin: 5px; display: none; padding: 5px;\">" + LONG_HIDDEN_TEXT + "</div>\n" +
        "        <div>" + SHORT_VISIBLE_TEXT + "</div>\n" +
        "    </body>\n" +
        "</html>";

    // when
    JResult res = extractor.extractContent(html);

    // then
    assertEquals(SHORT_VISIBLE_TEXT, res.getText());
  }

  @Test
  public void testExtract_longVisibleText() throws Exception {
    String LONG_HIDDEN_TEXT = "This is the hidden text which shouldn't be shown and it is a bit longer so normally prefered";
    String LONG_VISIBLE_TEXT = "This is the NOT-HIDDEN text which should be shown and it is a bit longer so normally prefered";
    String SHORT_VISIBLE_TEXT = "This is the text which is shorter but visible";

    // given
    String html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
        "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" +
        "    <head>\n" +
        "        <meta content='text/html; charset=UTF-8' http-equiv='Content-Type'/>\n" +
        "    </head>\n" +
        "    <body>\n" +
        "        <div style=\"margin: 5px; display:none; padding: 5px;\">" + LONG_HIDDEN_TEXT + "</div>\n" +
        "        <div style=\"margin: 5px; display:block; padding: 5px;\">" + LONG_VISIBLE_TEXT + "</div>\n" +
        "        <div>" + SHORT_VISIBLE_TEXT + "</div>\n" +
        "    </body>\n" +
        "</html>";

    // when
    JResult res = extractor.extractContent(html);

    // then
    assertEquals(LONG_VISIBLE_TEXT, res.getText());
  }

  @Test
  public void testExtract_skipATag() throws Exception {
    // given
    String html = "<html><body><div> aaa<a> bbb </a>ccc</div></body></html>";

    // when
    JResult res = extractor.extractContent(html);

    // then
    assertEquals("aaa bbb ccc", res.getText());
  }

  @Test
  public void testExtract_removeAdditionalSpaces() throws Exception {
    // given
    String html = "<html><body><div> aaa <strong> bbb </strong>ccc</div></body></html>";

    // when
    JResult res = extractor.extractContent(html);

    // then
    assertEquals("aaa bbb ccc", res.getText());
  }
}
