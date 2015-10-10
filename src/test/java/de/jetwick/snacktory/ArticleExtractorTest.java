package de.jetwick.snacktory;

import static org.junit.Assert.*;

import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ArticleExtractorTest {

  ArticleTextExtractor extractor = new ArticleTextExtractor();
  Converter c = new Converter();

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {
            "npr.org", // http://www.npr.org/blogs/money/2010/10/04/130329523/how-fake-money-saved-brazil
            expect()
              .title("How Fake Money Saved Brazil : Planet Money : NPR")
              .author("Chana Joffe-Walt")
              .imageUrl("http://media.npr.org/assets/img/2010/10/04/real_wide.jpg?t=1286218782&s=3")
        },
        {
            "benjaminste.in", // http://benjaminste.in/post/1223476561/hey-guys-whatcha-doing
            expect()
              .title("BenjaminSte.in - Hey guys, whatcha doing?")
        },
        {
            "paulgraham.com",
            expect()
              .title("Where to See Silicon Valley")
        },
        {
            "blog.traindom.com",
            expect()
              .title("36 places where you can submit your startup for some coverage")
              .imageUrl("http://blog.traindom.com/wp-content/uploads/2010/10/megaphone-225x300.jpg")
        },
        {
            "blog.airbnb.com",
            expect()
                .title("Airbnb & Y Combinator Present A Party of Epic Proportions! - The Airbnb Blog")
                .imageUrl("http://blog.traindom.com/wp-content/uploads/2010/10/megaphone-225x300.jpg")
        },
        {
            "edition.cnn.com", // http://edition.cnn.com/2011/WORLD/africa/04/06/libya.war/index.html?on.cnn=1
            expect()
                .title("Gadhafi asks Obama to end NATO bombing - CNN.com")
                .imageUrl("/2011/WORLD/africa/04/06/libya.war/t1larg.libyarebel.gi.jpg")
                .author("By the CNN Wire Staff")
        },
        {
            "bbc.co.uk", // http://www.bbc.co.uk/news/world-latin-america-21226565
            expect()
                .title("BBC News - Brazil mourns Santa Maria nightclub fire victims")
                .imageUrl("http://news.bbcimg.co.uk/media/images/65545000/gif/_65545798_brazil_santa_m_kiss_464.gif")
                .author("Caio Quero")
        },
        {
            "reuters.com", // http://www.reuters.com/article/2012/08/03/us-knightcapital-trading-technology-idUSBRE87203X20120803
            expect()
                .title("Knight trading loss shows cracks in equity markets")
                .imageUrl("http://s1.reutersmedia.net/resources/r/?m=02&d=20120803&t=2&i=637797752&w=460&fh=&fw=&ll=&pl=&r=CBRE872074Y00f")
                .author("Jed Horowitz and Joseph Menn")
        },
        {
            "daltoncaldwell.com", // http://daltoncaldwell.com/dear-mark-zuckerberg (html5)
            expect()
                .title("Dear Mark Zuckerberg by Dalton Caldwell")
        },
        {
            "karussell.wordpress.com", // http://karussell.wordpress.com/
            expect()
                .title("Twitter API and Me « Find Time for the Karussell")
        },
        {
            "golem.de", // http://www.golem.de/1104/82797.html
            expect()
                .title("Mozilla: Vorabversionen von Firefox 5 und 6 veröffentlicht - Golem.de")
                .imageUrl("http://scr3.golem.de/screenshots/1104/Firefox-Aurora/thumb480/aurora-nighly-beta-logos.png")
        },
        {
            "yomiuri.co.jp", // http://www.yomiuri.co.jp/e-japan/gifu/news/20110410-OYT8T00124.htm
            expect()
                .title("色とりどりのチューリップ : 岐阜 : 地域 : YOMIURI ONLINE（読売新聞）")
        },
        {
            "faz.net", // http://www.faz.net/s/Rub469C43057F8C437CACC2DE9ED41B7950/Doc~EBA775DE7201E46E0B0C5AD9619BD56E9~ATpl~Ecommon~Scontent.html
            expect()
                .title("Im Gespräch: Umweltaktivist Stewart Brand: Ihr Deutschen steht allein da - Atomdebatte - FAZ.NET")
                .imageUrl("/m/{5F104CCF-3B5A-4B4C-B83E-4774ECB29889}g225_4.jpg")
                .author("FAZ Electronic Media")
                .keywords(Arrays.asList("Atomkraft", "Deutschland", "Jahren", "Atommüll", "Fukushima", "Problem", "Brand", "Kohle", "2011", "11",
                    "Stewart", "Atomdebatte", "Jahre", "Boden", "Treibhausgase", "April", "Welt", "Müll", "Radioaktivität",
                    "Gesamtbild", "Klimawandel", "Reaktoren", "Verzicht", "Scheinheiligkeit", "Leute", "Risiken", "Löcher",
                    "Fusion", "Gefahren", "Land"))
        },
        {
            "en.rian.ru", // http://en.rian.ru/world/20110410/163458489.html
            expect()
                .title("Japanese rally against nuclear power industry | World")
                .imageUrl("http://en.rian.ru/images/16345/86/163458615.jpg")
        },
        {
            "jetwick.com",
            expect()
                .title("Jetwick | twitter | Twitter Search Without Noise")
                .imageUrl("img/yourkit.png")
                .keywords(Arrays.asList("news", "twitter", "search", "jetwick"))
        },
        {
            "vimeo.com", // http://vimeo.com/20910443
            expect()
                .title("finn. & Dirk von Lowtzow \"CRYING IN THE RAIN\" on Vimeo")
                .author("finn.")
                .keywords(Arrays.asList("finn", "finn.", "Dirk von Lowtzow", "crying in the rain", "I wish I was someone else", "Tocotronic",
                    "Sunday Service", "Indigo", "Patrick Zimmer", "Patrick Zimmer aka finn.", "video", "video sharing",
                    "digital cameras", "videoblog", "vidblog", "video blogging", "home video", "home movie"))
        },
        {
            "spiegel.de",
            expect()
                .title("Retro-PC: Commodore reaktiviert den C64 - SPIEGEL ONLINE - Nachrichten - Netzwelt")
                .author("SPIEGEL ONLINE, Hamburg, Germany")
        },
        {
            "github.com", // https://github.com/ifesdjeen/jReadability
            expect()
                .title("ifesdjeen/jReadability - GitHub")
        },
        {
            "itunes.apple.com", // http://itunes.apple.com/us/album/21/id420075073
            expect()
                .title("iTunes - Music - 21 by ADELE")
        },
        {
            "twitpic.com", // http://twitpic.com/4k1ku3
            expect()
                .title("It’s hard to be a dinosaur. on Twitpic")
        },
        {
            "twitpic.com_2", // http://twitpic.com/4kuem8
            expect()
                .title("*Not* what you want to see on the fetal monitor when your wif... on Twitpic")
        },
        {
            "heise.de", // http://www.heise.de/newsticker/meldung/Internet-Explorer-9-jetzt-mit-schnellster-JavaScript-Engine-1138062.html
            expect()
                .title("heise online - Internet Explorer 9 jetzt mit schnellster JavaScript-Engine")
        },
        {
            "techcrunch.com", // http://techcrunch.com/2011/04/04/twitter-advanced-search/
            expect()
                .title("Twitter Finally Brings Advanced Search Out Of Purgatory; Updates Discovery Algorithms")
                .author("MG Siegler")
        },
        {
            "engadget.com", // http://www.engadget.com/2011/04/09/editorial-androids-problem-isnt-fragmentation-its-contamina/
            expect()
                .title("Editorial: Android's problem isn't fragmentation, it's contamination -- Engadget")
                .imageUrl("http://www.blogcdn.com/www.engadget.com/media/2011/04/11x0409mnbvhg_thumbnail.jpg")
        },
        {
            "engineering.twitter.com", // http://engineering.twitter.com/2011/04/twitter-search-is-now-3x-faster_1656.html
            expect()
                .title("Twitter Engineering: Twitter Search is Now 3x Faster")
                .imageUrl("http://4.bp.blogspot.com/-CmXJmr9UAbA/TZy6AsT72fI/AAAAAAAAAAs/aaF5AEzC-e4/s72-c/Blender_Tsunami.jpg")
        },
        {
            "taz.de", // http://www.taz.de/1/politik/asien/artikel/1/anti-atomkraft-nein-danke/
            expect()
                .title("Protestkultur in Japan nach der Katastrophe: Anti-Atomkraft? Nein danke! - taz.de")
                .author("Georg Blume")
        },
        {
            "facebook.com", // http://www.facebook.com/ejdionne/posts/10150154175658687
            expect()
                .title("In my column...")
        },
        {
            "facebook.com_2", // http://www.facebook.com/permalink.php?story_fbid=214289195249322&id=101149616624415
            expect()
                .title("Sommer is the best...")
        },
        {
            "blog.talawah.net", // http://blog.talawah.net/2011/04/gavin-king-unviels-red-hats-top-secret.html
            expect()
                .title("The Brain Dump: Gavin King unveils Red Hat's Java killer successor: The Ceylon Project")
                .author("Marc Richards")
                .imageUrl("http://3.bp.blogspot.com/-cyMzveP3IvQ/TaR7f3qkYmI/AAAAAAAAAIk/mrChE-G0b5c/s200/Java.png")
        },
        {
            "nytimes.com", // http://dealbook.nytimes.com/2011/04/11/for-defense-in-galleon-trial-no-time-to-rest/
            expect()
                .title("For Defense in Galleon Trial, No Time to Rest - NYTimes.com")
                .author("Andrew Ross Sorkin")
                .imageUrl("http://3.bp.blogspot.com/-cyMzveP3IvQ/TaR7f3qkYmI/AAAAAAAAAIk/mrChE-G0b5c/s200/Java.png")
        },
        {
            "huffingtonpost.com", // http://dealbook.nytimes.com/2011/04/11/for-defense-in-galleon-trial-no-time-to-rest/
            expect()
                .title("Federal Reserve's Low Rate Policy Is A 'Dangerous Gamble,' Says Top Central Bank Official")
                .author("Shahien Nasiripour")
                .imageUrl("http://3.bp.blogspot.com/-cyMzveP3IvQ/TaR7f3qkYmI/AAAAAAAAAIk/mrChE-G0b5c/s200/Java.png")
        },
        {
            "techcrunch.com_2", // http://techcrunch.com/2010/08/13/gantto-takes-on-microsoft-project-with-web-based-project-management-application/
            expect()
                .title("Gantto Takes On Microsoft Project With Web-Based Project Management Application")
                .author("Leena Rao")
                .imageUrl("http://i0.wp.com/tctechcrunch2011.files.wordpress.com/2010/08/gantto.jpg?resize=680%2C680")
        },
        {
            "cnn.com", // http://www.cnn.com/2010/POLITICS/08/13/democrats.social.security/index.html
            expect()
                .title("Democrats to use Social Security against GOP this fall - CNN.com")
                .author("Ed Hornick")
                .imageUrl("http://i.cdn.turner.com/cnn/2010/POLITICS/08/13/democrats.social.security/story.kaine.gi.jpg")
        },
        {
            "businessweek.com", // http://www.businessweek.com/magazine/content/10_34/b4192048613870.htm
            expect()
                .author("Whitney Kisling,Caroline Dye")
                .imageUrl("http://images.businessweek.com/mz/covers/current_120x160.jpg")
        },
        {
            "foxnews.com", // http://www.foxnews.com/politics/2010/08/14/russias-nuclear-help-iran-stirs-questions-improved-relations/
            expect()
                .title("Russia's Nuclear Help to Iran Stirs Questions About Its 'Improved' Relations With U.S. - FoxNews.com")
                .imageUrl("http://a57.foxnews.com/static/managed/img/Politics/397/224/startsign.jpg")
        },
        {
            "stackoverflow.com", // http://stackoverflow.com/questions/3553693/wicket-vs-vaadin/3660938
            expect()
                .title("java - wicket vs Vaadin - Stack Overflow")
                .imageUrl("http://a57.foxnews.com/static/managed/img/Politics/397/224/startsign.jpg")
        },
        {
            "aolnews.com", // http://www.aolnews.com/nation/article/the-few-the-proud-the-marines-getting-a-makeover/19592478
            expect()
                .imageUrl("http://o.aolcdn.com/photo-hub/news_gallery/6/8/680919/1281734929876.JPEG")
                .keywords(Arrays.asList("news", "update", "breaking", "nation", "U.S.", "elections", "world", "entertainment", "sports", "business",
                    "weird news", "health", "science", "latest news articles", "breaking news", "current news", "top news"))
        },
        {
            "online.wsj.com", // http://online.wsj.com/article/SB10001424052748704532204575397061414483040.html
            expect()
                .imageUrl("http://si.wsj.net/public/resources/images/OB-JO747_stimul_D_20100814113803.jpg")
                .author("LOUISE RADNOFSKY")
        },
        {
            "usatoday.com", // http://content.usatoday.com/communities/driveon/post/2010/08/gm-finally-files-for-ipo/1
            expect()
                .imageUrl("ttp://i.usatoday.net/communitymanager/_photos/the-huddle/2010/08/18/favrespeaksx-inset-community.jpg")
                .author("Sean Leahy")
        },
        {
            "usatoday.com_2", // http://content.usatoday.com/communities/driveon/post/2010/08/gm-finally-files-for-ipo/1
            expect()
                .imageUrl("http://i.usatoday.net/communitymanager/_photos/drive-on/2010/08/18/cruzex-wide-community.jpg")
        },
        {
            "sports.espn.go.com", // http://sports.espn.go.com/espn/commentary/news/story?id=5461430
            expect()
                .imageUrl("http://a.espncdn.com/photo/2010/0813/ncf_i_mpouncey1_300.jp")
        },
        {
            "gizmodo.com", // http://sports.espn.go.com/espn/commentary/news/story?id=5461430
            expect()
                .imageUrl("http://cache.gawkerassets.com/assets/images/9/2010/08/500x_fighters_uncaged__screenshot_4b__rider.jpg")
        },
        {
            "engadget.com_2", // http://www.engadget.com/2010/08/18/verizon-fios-set-top-boxes-getting-a-new-hd-guide-external-stor/
            expect()
                .imageUrl("http://cache.gawkerassets.com/assets/images/9/2010/08/500x_fighters_uncaged__screenshot_4b__rider.jpg")
        },
    });
  }

  private String testName;
  private Expectations expectations;

  public ArticleExtractorTest(String testName, Expectations expectations){
    this.testName = testName;
    this.expectations = expectations;
  }

  @Test
  public void test(){
    String input = getResource("/input/" + testName + ".html");
    String output = getResource("/output_plain/" + testName + ".txt");
    try {
      JResult result = extractor.extractContent(input);
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
  }

  private String getResource(String name){
    InputStream is = getClass().getResourceAsStream(name);
    return new Converter().streamToString(is);
  }

  private static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  @Data
  @Accessors(fluent = true, chain = true)
  static class Expectations {
    String title;
    String author;
    String imageUrl;
    List<String> images;
    List<String> keywords;
  }

  private static Expectations expect(){
    return new Expectations();
  }

}
