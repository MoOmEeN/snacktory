package de.jetwick.snacktory;

import static org.junit.Assert.*;

import de.jetwick.snacktory.output.OutputFormatter;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public abstract class AbstractArticleExtractorTest {

  private String inputDir;
  private String outputDir;
  private String inputExtension;
  private String outputExtension;
  private OutputFormatter formatter;

  private String testName;
  private Expectations expectations;

  private ArticleTextExtractor extractor = new ArticleTextExtractor();

  protected AbstractArticleExtractorTest(String testName, Expectations expectations, OutputFormatter formatter,
                                         String inputDir, String outputDir, String inputExt, String outputExt){
    this.testName = testName;
    this.expectations = expectations;
    this.formatter = formatter;
    this.inputDir = inputDir;
    this.outputDir = outputDir;
    this.inputExtension = inputExt;
    this.outputExtension = outputExt;
  }

  protected String getResource(String name){
    InputStream is = getClass().getResourceAsStream(name);
    return new Converter().streamToString(is);
  }

  protected static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  protected static String filePath(String dir, String filename, String extenstion){
    return File.separator + dir + File.separator + filename + "." + extenstion;
  }

  protected void doTest(){
    String input = getResource(filePath(inputDir, testName, inputExtension));
    String output = getResource(filePath(outputDir, testName, outputExtension));
    try {
      JResult result = extractor.extractContent(input, formatter);
      assertExpectationsMet(expectations, result);
      assertEquals(output, result.getText());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  private void assertExpectationsMet(Expectations expectations, JResult result){
    if (expectations.author != null){
      assertEquals(expectations.author, result.getAuthorName());
    }
    if (expectations.title != null){
      assertEquals(expectations.title, result.getTitle());
    }
    if (expectations.keywords != null){
      assertEquals(expectations.keywords, result.getKeywords());
    }
    if (expectations.images != null){
      assertEquals(expectations.images.size(), result.getImages().size());
      for (ImageResult res : result.getImages()){
        assertTrue(expectations.images.contains(res.src));
      }
    }
  }

  @Data
  @Accessors(fluent = true, chain = true)
  protected static class Expectations {
    String title;
    String author;
    String imageUrl;
    List<String> images;
    List<String> keywords;
  }

  protected static Expectations expectCorrectText(){
    return new Expectations();
  }
}
