package de.jetwick.snacktory;

import de.jetwick.snacktory.output.HTMLOutputFormater;
import de.jetwick.snacktory.output.OutputFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ArticleExtractorWithHTMLFormatterTest extends AbstractArticleExtractorTest{

  private static final String INPUT_DIR = "input_html";
  private static final String OUTPUT_DIR = "output_html";
  private static final String INPUT_EXT = "html";
  private static final String OUTPUT_EXT = "html";
  private static final OutputFormatter FORMATTER = new HTMLOutputFormater();

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
      {
        "olemagazyn.pl", // http://olemagazyn.pl/aritz-el-rey-leon-aduriz/
        expectCorrectText()
          .title("¡Olé! Magazyn | Felietony | Aritz \"El Rey León\" Aduriz")
          .author("Mariusz Bielski")
      },
      {
        "runtheworld.pl", // http://runtheworld.pl/jak-odpoczywac-przed-zawodami-naciaganie-zasad-pod-wlasny-organizm/
        expectCorrectText()
          .title("Jak odpoczywać przed zawodami? Naciąganie zasad pod własny organizm")
      },
    });
  }

  public ArticleExtractorWithHTMLFormatterTest(String testName, Expectations expectations){
    super(testName, expectations, FORMATTER, INPUT_DIR, OUTPUT_DIR, INPUT_EXT, OUTPUT_EXT);
  }

  @Test
  public void test(){
    doTest();
  }

}
