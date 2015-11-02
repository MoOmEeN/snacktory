package de.jetwick.snacktory;

import de.jetwick.snacktory.output.OutputFormatter;
import de.jetwick.snacktory.output.PlainTextOutputFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ArticleExtractorWithPlainTextFormatterTest extends AbstractArticleExtractorTest {

  private static final String INPUT_DIR = "input_plain";
  private static final String OUTPUT_DIR = "output_plain";
  private static final String INPUT_EXT = "html";
  private static final String OUTPUT_EXT = "txt";
  private static final OutputFormatter FORMATTER = new PlainTextOutputFormatter();

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
      {
        "npr.org", // http://www.npr.org/blogs/money/2010/10/04/130329523/how-fake-money-saved-brazil
        expectCorrectText()
          .title("How Fake Money Saved Brazil : Planet Money : NPR")
          .author("Chana Joffe-Walt")
          .imageUrl("http://media.npr.org/assets/img/2010/10/04/real_wide.jpg?t=1286218782&s=3")
      },
      {
        "benjaminste.in", // http://benjaminste.in/post/1223476561/hey-guys-whatcha-doing
        expectCorrectText()
          .title("BenjaminSte.in - Hey guys, whatcha doing?")
      },
      {
        "paulgraham.com",
        expectCorrectText()
          .title("Where to See Silicon Valley")
      },
      {
        "blog.traindom.com",
        expectCorrectText()
          .title("36 places where you can submit your startup for some coverage")
          .imageUrl("http://blog.traindom.com/wp-content/uploads/2010/10/megaphone-225x300.jpg")
      },
      {
        "blog.airbnb.com",
        expectCorrectText()
          .title("Airbnb & Y Combinator Present A Party of Epic Proportions! - The Airbnb Blog")
          .imageUrl("http://blog.traindom.com/wp-content/uploads/2010/10/megaphone-225x300.jpg")
      },
      {
        "edition.cnn.com", // http://edition.cnn.com/2011/WORLD/africa/04/06/libya.war/index.html?on.cnn=1
        expectCorrectText()
          .title("Gadhafi asks Obama to end NATO bombing - CNN.com")
          .imageUrl("/2011/WORLD/africa/04/06/libya.war/t1larg.libyarebel.gi.jpg")
          .author("By the CNN Wire Staff")
      },
      {
        "bbc.co.uk", // http://www.bbc.co.uk/news/world-latin-america-21226565
        expectCorrectText()
          .title("BBC News - Brazil mourns Santa Maria nightclub fire victims")
          .imageUrl("http://news.bbcimg.co.uk/media/images/65545000/gif/_65545798_brazil_santa_m_kiss_464.gif")
          .author("Caio Quero")
      },
      {
        "reuters.com", // http://www.reuters.com/article/2012/08/03/us-knightcapital-trading-technology-idUSBRE87203X20120803
        expectCorrectText()
          .title("Knight trading loss shows cracks in equity markets")
          .imageUrl(
            "http://s1.reutersmedia.net/resources/r/?m=02&d=20120803&t=2&i=637797752&w=460&fh=&fw=&ll=&pl=&r=CBRE872074Y00f")
          .author("Jed Horowitz and Joseph Menn")
          .images(Arrays.asList(
            "http://s1.reutersmedia.net/resources/r/?m=02&d=20120803&t=2&i=637797752&w=460&fh=&fw=&ll=&pl=&r=CBRE872074Y00"))
      },
      {
        "daltoncaldwell.com", // http://daltoncaldwell.com/dear-mark-zuckerberg (html5)
        expectCorrectText()
          .title("Dear Mark Zuckerberg by Dalton Caldwell")
      },
      {
        "karussell.wordpress.com", // http://karussell.wordpress.com/
        expectCorrectText()
          .title("Twitter API and Me « Find Time for the Karussell")
      },
      {
        "golem.de", // http://www.golem.de/1104/82797.html
        expectCorrectText()
          .title("Mozilla: Vorabversionen von Firefox 5 und 6 veröffentlicht - Golem.de")
          .imageUrl("http://scr3.golem.de/screenshots/1104/Firefox-Aurora/thumb480/aurora-nighly-beta-logos.png")
      },
      {
        "yomiuri.co.jp", // http://www.yomiuri.co.jp/e-japan/gifu/news/20110410-OYT8T00124.htm
        expectCorrectText()
          .title("色とりどりのチューリップ : 岐阜 : 地域 : YOMIURI ONLINE（読売新聞）")
      },
      {
        "faz.net", // http://www.faz.net/s/Rub469C43057F8C437CACC2DE9ED41B7950/Doc~EBA775DE7201E46E0B0C5AD9619BD56E9~ATpl~Ecommon~Scontent.html
        expectCorrectText()
          .title("Im Gespräch: Umweltaktivist Stewart Brand: Ihr Deutschen steht allein da - Atomdebatte - FAZ.NET")
          .imageUrl("/m/{5F104CCF-3B5A-4B4C-B83E-4774ECB29889}g225_4.jpg")
          .author("FAZ Electronic Media")
          .keywords(Arrays
            .asList("Atomkraft", "Deutschland", "Jahren", "Atommüll", "Fukushima", "Problem", "Brand", "Kohle", "2011",
              "11", "Stewart", "Atomdebatte", "Jahre", "Boden", "Treibhausgase", "April", "Welt", "Müll",
              "Radioaktivität", "Gesamtbild", "Klimawandel", "Reaktoren", "Verzicht", "Scheinheiligkeit", "Leute",
              "Risiken", "Löcher", "Fusion", "Gefahren", "Land"))
      },
      {
        "en.rian.ru", // http://en.rian.ru/world/20110410/163458489.html
        expectCorrectText()
          .title("Japanese rally against nuclear power industry | World")
          .imageUrl("http://en.rian.ru/images/16345/86/163458615.jpg")
      },
      {
        "jetwick.com",
        expectCorrectText()
          .title("Jetwick | twitter | Twitter Search Without Noise")
          .imageUrl("img/yourkit.png")
          .keywords(Arrays.asList("news", "twitter", "search", "jetwick"))
      },
      {
        "vimeo.com", // http://vimeo.com/20910443
        expectCorrectText()
          .title("finn. & Dirk von Lowtzow \"CRYING IN THE RAIN\" on Vimeo")
          .author("finn.")
          .keywords(Arrays
            .asList("finn", "finn.", "Dirk von Lowtzow", "crying in the rain", "I wish I was someone else",
              "Tocotronic", "Sunday Service", "Indigo", "Patrick Zimmer", "Patrick Zimmer aka finn.", "video",
              "video sharing", "digital cameras", "videoblog", "vidblog", "video blogging", "home video", "home movie"))
      },
      {
        "spiegel.de",
        expectCorrectText()
          .title("Retro-PC: Commodore reaktiviert den C64 - SPIEGEL ONLINE - Nachrichten - Netzwelt")
          .author("SPIEGEL ONLINE, Hamburg, Germany")
      },
      {
        "github.com", // https://github.com/ifesdjeen/jReadability
        expectCorrectText()
          .title("ifesdjeen/jReadability - GitHub")
      },
      {
        "itunes.apple.com", // http://itunes.apple.com/us/album/21/id420075073
        expectCorrectText()
          .title("iTunes - Music - 21 by ADELE")
      },
      {
        "twitpic.com", // http://twitpic.com/4k1ku3
        expectCorrectText()
          .title("It’s hard to be a dinosaur. on Twitpic")
      },
      {
        "twitpic.com_2", // http://twitpic.com/4kuem8
        expectCorrectText()
          .title("*Not* what you want to see on the fetal monitor when your wif... on Twitpic")
      },
      {
        "heise.de", // http://www.heise.de/newsticker/meldung/Internet-Explorer-9-jetzt-mit-schnellster-JavaScript-Engine-1138062.html
        expectCorrectText()
          .title("heise online - Internet Explorer 9 jetzt mit schnellster JavaScript-Engine")
      },
      {
        "techcrunch.com", // http://techcrunch.com/2011/04/04/twitter-advanced-search/
        expectCorrectText()
          .title("Twitter Finally Brings Advanced Search Out Of Purgatory; Updates Discovery Algorithms")
          .author("MG Siegler")
      },
      {
        "engadget.com", // http://www.engadget.com/2011/04/09/editorial-androids-problem-isnt-fragmentation-its-contamina/
        expectCorrectText()
          .title("Editorial: Android's problem isn't fragmentation, it's contamination -- Engadget")
          .imageUrl("http://www.blogcdn.com/www.engadget.com/media/2011/04/11x0409mnbvhg_thumbnail.jpg")
      },
      {
        "engineering.twitter.com", // http://engineering.twitter.com/2011/04/twitter-search-is-now-3x-faster_1656.html
        expectCorrectText()
          .title("Twitter Engineering: Twitter Search is Now 3x Faster")
          .imageUrl(
            "http://4.bp.blogspot.com/-CmXJmr9UAbA/TZy6AsT72fI/AAAAAAAAAAs/aaF5AEzC-e4/s72-c/Blender_Tsunami.jpg")
      },
      {
        "taz.de", // http://www.taz.de/1/politik/asien/artikel/1/anti-atomkraft-nein-danke/
        expectCorrectText()
          .title("Protestkultur in Japan nach der Katastrophe: Anti-Atomkraft? Nein danke! - taz.de")
          .author("Georg Blume")
      },
      {
        "facebook.com", // http://www.facebook.com/ejdionne/posts/10150154175658687
        expectCorrectText()
          .title("In my column...")
      },
      {
        "facebook.com_2", // http://www.facebook.com/permalink.php?story_fbid=214289195249322&id=101149616624415
        expectCorrectText()
          .title("Sommer is the best...")
      },
      {
        "blog.talawah.net", // http://blog.talawah.net/2011/04/gavin-king-unviels-red-hats-top-secret.html
        expectCorrectText()
          .title("The Brain Dump: Gavin King unveils Red Hat's Java killer successor: The Ceylon Project")
          .author("Marc Richards")
          .imageUrl("http://3.bp.blogspot.com/-cyMzveP3IvQ/TaR7f3qkYmI/AAAAAAAAAIk/mrChE-G0b5c/s200/Java.png")
      },
      {
        "nytimes.com", // http://dealbook.nytimes.com/2011/04/11/for-defense-in-galleon-trial-no-time-to-rest/
        expectCorrectText()
          .title("For Defense in Galleon Trial, No Time to Rest - NYTimes.com")
          .author("Andrew Ross Sorkin")
          .imageUrl("http://3.bp.blogspot.com/-cyMzveP3IvQ/TaR7f3qkYmI/AAAAAAAAAIk/mrChE-G0b5c/s200/Java.png")
      },
      {
        "huffingtonpost.com", // http://dealbook.nytimes.com/2011/04/11/for-defense-in-galleon-trial-no-time-to-rest/
        expectCorrectText()
          .title("Federal Reserve's Low Rate Policy Is A 'Dangerous Gamble,' Says Top Central Bank Official")
          .author("Shahien Nasiripour")
          .imageUrl("http://3.bp.blogspot.com/-cyMzveP3IvQ/TaR7f3qkYmI/AAAAAAAAAIk/mrChE-G0b5c/s200/Java.png")
      },
      {
        "techcrunch.com_2", // http://techcrunch.com/2010/08/13/gantto-takes-on-microsoft-project-with-web-based-project-management-application/
        expectCorrectText()
          .title("Gantto Takes On Microsoft Project With Web-Based Project Management Application")
          .author("Leena Rao")
          .imageUrl("http://i0.wp.com/tctechcrunch2011.files.wordpress.com/2010/08/gantto.jpg?resize=680%2C680")
      },
      {
        "cnn.com", // http://www.cnn.com/2010/POLITICS/08/13/democrats.social.security/index.html
        expectCorrectText()
          .title("Democrats to use Social Security against GOP this fall - CNN.com")
          .author("Ed Hornick")
          .imageUrl("http://i.cdn.turner.com/cnn/2010/POLITICS/08/13/democrats.social.security/story.kaine.gi.jpg")
      },
      {
        "businessweek.com", // http://www.businessweek.com/magazine/content/10_34/b4192048613870.htm
        expectCorrectText()
          .author("Whitney Kisling,Caroline Dye")
          .imageUrl("http://images.businessweek.com/mz/covers/current_120x160.jpg")
      },
      {
        "foxnews.com", // http://www.foxnews.com/politics/2010/08/14/russias-nuclear-help-iran-stirs-questions-improved-relations/
        expectCorrectText()
          .title("Russia's Nuclear Help to Iran Stirs Questions About Its 'Improved' Relations With U.S. - FoxNews.com")
          .imageUrl("http://a57.foxnews.com/static/managed/img/Politics/397/224/startsign.jpg")
      },
      {
        "stackoverflow.com", // http://stackoverflow.com/questions/3553693/wicket-vs-vaadin/3660938
        expectCorrectText()
          .title("java - wicket vs Vaadin - Stack Overflow")
          .imageUrl("http://a57.foxnews.com/static/managed/img/Politics/397/224/startsign.jpg")
      },
      {
        "aolnews.com", // http://www.aolnews.com/nation/article/the-few-the-proud-the-marines-getting-a-makeover/19592478
        expectCorrectText()
          .imageUrl("http://o.aolcdn.com/photo-hub/news_gallery/6/8/680919/1281734929876.JPEG")
          .keywords(Arrays
            .asList("news", "update", "breaking", "nation", "U.S.", "elections", "world", "entertainment", "sports",
              "business", "weird news", "health", "science", "latest news articles", "breaking news", "current news",
              "top news"))
      },
      {
        "online.wsj.com", // http://online.wsj.com/article/SB10001424052748704532204575397061414483040.html
        expectCorrectText()
          .imageUrl("http://si.wsj.net/public/resources/images/OB-JO747_stimul_D_20100814113803.jpg")
          .author("LOUISE RADNOFSKY")
      },
      {
        "usatoday.com", // http://content.usatoday.com/communities/driveon/post/2010/08/gm-finally-files-for-ipo/1
        expectCorrectText()
          .imageUrl(
            "ttp://i.usatoday.net/communitymanager/_photos/the-huddle/2010/08/18/favrespeaksx-inset-community.jpg")
          .author("Sean Leahy")
      },
      {
        "usatoday.com_2", // http://content.usatoday.com/communities/driveon/post/2010/08/gm-finally-files-for-ipo/1
        expectCorrectText()
          .imageUrl("http://i.usatoday.net/communitymanager/_photos/drive-on/2010/08/18/cruzex-wide-community.jpg")
      },
      {
        "sports.espn.go.com", // http://sports.espn.go.com/espn/commentary/news/story?id=5461430
        expectCorrectText()
          .imageUrl("http://a.espncdn.com/photo/2010/0813/ncf_i_mpouncey1_300.jp")
      },
      {
        "gizmodo.com", // http://sports.espn.go.com/espn/commentary/news/story?id=5461430
        expectCorrectText()
          .imageUrl(
            "http://cache.gawkerassets.com/assets/images/9/2010/08/500x_fighters_uncaged__screenshot_4b__rider.jpg")
      },
      {
        "engadget.com_2", // http://www.engadget.com/2010/08/18/verizon-fios-set-top-boxes-getting-a-new-hd-guide-external-stor/
        expectCorrectText()
          .imageUrl(
            "http://cache.gawkerassets.com/assets/images/9/2010/08/500x_fighters_uncaged__screenshot_4b__rider.jpg")
      },
      {
        "wired.com", // http://www.wired.com/playbook/2010/08/stress-hormones-boxing/
        expectCorrectText()
          .title("Stress Hormones Could Predict Boxing Dominance")
          .author("Brian Mossop")
          .imageUrl("http://www.wired.com/playbook/wp-content/uploads/2010/08/fight_f-660x441.jpg")
      },
      {
        "gigaom.com", // http://gigaom.com/apple/apples-next-macbook-an-800-mac-for-the-masses/
        expectCorrectText()
          .title("Apple’s Next MacBook: An $800 Mac for the Masses: Apple News, Tips and Reviews «")
          .imageUrl("http://gigapple.files.wordpress.com/2010/10/macbook-feature.png?w=300&h=200")
      },
      {
        "mashable.com", // http://mashable.com/2010/08/18/how-tonot-to-ask-someone-out-online/
        expectCorrectText()
          .title("HOW TO/NOT TO: Ask Someone Out Online")
          .imageUrl("http://9.mshcdn.com/wp-content/uploads/2010/07/love.jpg")
      },
      {
        "politico.com", // http://www.politico.com/news/stories/1010/43352.html
        expectCorrectText()
          .title("2012 map takes unfamiliar shape - Maggie Haberman and Shira Toeplitz - POLITICO.com")
          .imageUrl("http://images.politico.com/global/news/100927_obama22_ap_328.jpg")
      },
      {
        "ninjatraderblog.com", // http://www.ninjatraderblog.com/im/2010/10/seo-marketing-facts-about-google-instant-and-ranking-your-website/
        expectCorrectText()
          .title("SEO Marketing- Facts About Google Instant And Ranking Your Website")
      },
      {
        "sportsillustrated.cnn.com", // http://sportsillustrated.cnn.com/2010/football/ncaa/10/15/ohio-state-holmes.ap/index.html?xid=si_ncaaf
        expectCorrectText()
          .imageUrl("http://i.cdn.turner.com/si/.e1d/img/4.0/global/logos/si_100x100.jpg")
      },
      {
        "thedailybeast.com", // http://www.thedailybeast.com/blogs-and-stories/2010-11-01/ted-sorensen-speechwriter-behind-jfks-best-jokes/?cid=topic:featured1
        expectCorrectText()
          .title("Ted Sorensen: Speechwriter Behind JFK’s Best Jokes - The Daily Beast")
          .imageUrl("http://www.tdbimg.com/files/2010/11/01/img-article---katz-ted-sorensen_163531624950.jpg")
      },
      {
        "sciencemag.org", // http://news.sciencemag.org/sciencenow/2011/04/early-birds-smelled-good.html
        expectCorrectText()
          .title("Early Birds Smelled Good - ScienceNOW")
      },
      {
        "slamonline.com", // http://www.slamonline.com/online/nba/2010/10/nba-schoolyard-rankings/
        expectCorrectText()
          .title("SLAM ONLINE | » NBA Schoolyard Rankings")
          .imageUrl("http://www.slamonline.com/online/wp-content/uploads/2010/10/celtics.jpg")
      },
      {
        "sports.espn.go.com_2", // http://sports.espn.go.com/nfl/news/story?id=5971053
        expectCorrectText()
          .title("Michael Vick of Philadelphia Eagles misses practice, unlikely to play vs. Dallas Cowboys - ESPN")
          .imageUrl("http://a.espncdn.com/i/espn/espn_logos/espn_red.png")
      },
      {
        "sportingnews.com", // http://www.sportingnews.com/nfl/feed/2011-01/nfl-coaches/story/raiders-cut-ties-with-cable
        expectCorrectText()
          .title("Raiders cut ties with Cable - NFL - Sporting News")
          .imageUrl("http://dy.snimg.com/story-image/0/69/174475/14072-650-366.jpg")
      },
      {
        "foxsports.com", // http://msn.foxsports.com/nfl/story/Tom-Cable-fired-contract-option-Oakland-Raiders-coach-010411
        expectCorrectText()
          .title("Oakland Raiders won't bring Tom Cable back as coach - NFL News")
      },
      {
        "economist.com", // http://www.economist.com/node/17956885
        expectCorrectText()
          .imageUrl("http://www.economist.com/sites/default/files/images/articles/migrated/20110122_stp004.jpg")
      },
      {
        "thevacationgals.com", // http://thevacationgals.com/vacation-rental-homes-are-a-family-reunion-necessity/
        expectCorrectText()
          .imageUrl(
            "http://thevacationgals.com/wp-content/uploads/2010/11/Gemmel-Family-Reunion-at-a-Vacation-Rental-Home1-300x225.jpg")
          .images(Arrays.asList(
            "http://thevacationgals.com/wp-content/uploads/2010/11/Gemmel-Family-Reunion-at-a-Vacation-Rental-Home1-300x225.jpg",
            "../wp-content/uploads/2010/11/The-Gemmel-Family-Does-a-Gilligans-Island-Theme-Family-Reunion-Vacation-Sarah-Gemmel-300x225.jpg",
            "http://www.linkwithin.com/pixel.png"))
      },
      {
        "shockya.com", // http://www.shockya.com/news/2011/01/30/daily-shock-jonathan-knight-of-new-kids-on-the-block-publicly-reveals-hes-gay/
        expectCorrectText()
          .imageUrl("http://www.shockya.com/news/wp-content/uploads/jonathan_knight_new_kids_gay.jpg")
      },
      {
        "wikipedia.com", // http://en.wikipedia.org/wiki/Therapsids
        expectCorrectText()
          .imageUrl(
            "//upload.wikimedia.org/wikipedia/commons/thumb/4/42/Pristeroognathus_DB.jpg/240px-Pristeroognathus_DB.jpg")
      },
      {
        "wikipedia.com_2", // http://en.wikipedia.org/wiki/President_of_the_United_States
        expectCorrectText()
          .title("President of the United States - Wikipedia, the free encyclopedia")
      },
      {
        "wikipedia.com_3", // http://en.wikipedia.org/wiki/Muhammad
        expectCorrectText()
          .title("Muhammad - Wikipedia, the free encyclopedia")
      },
      {
        "wikipedia.com_4", // http://de.wikipedia.org/wiki/Henne_Strand
        expectCorrectText()
          .title("Henne Strand – Wikipedia")
      },
      {
        "wikipedia.com_5", // http://de.wikipedia.org/wiki/Java
        expectCorrectText()
          .title("Java - Wikipedia, the free encyclopedia")
      },
      {
        "wikipedia.com_6", // http://de.wikipedia.org/wiki/Knight_Rider
        expectCorrectText()
          .title("Knight Rider – Wikipedia")
      },
      {
        "time.com", // http://www.time.com/time/health/article/0,8599,2011497,00.html
        expectCorrectText()
          .imageUrl("http://img.timeinc.net/time/daily/2010/1008/bp_oil_spill_0817.jpg")
      },
      {
        "cnet.com", // hhttp://news.cnet.com/8301-30686_3-20014053-266.html?tag=topStories1
        expectCorrectText()
          .imageUrl("http://i.i.com.com/cnwk.1d/i/tim//2010/08/18/Verizon_iPad_and_live_TV_610x458.JPG")
      },
      {
        "bloomberg.com", // http://www.bloomberg.com/news/2010-11-01/china-becomes-boss-in-peru-on-50-billion-mountain-bought-for-810-million.html
        expectCorrectText()
          .imageUrl("http://www.bloomberg.com/apps/data?pid=avimage&iid=iimODmqjtcQU")
      },
      {
        "thefrisky.com", // http://www.bloomberg.com/news/2010-11-01/china-becomes-boss-in-peru-on-50-billion-mountain-bought-for-810-million.html
        expectCorrectText()
          .title("Rachel Dratch Met Her Baby Daddy At A Bar")
          .imageUrl("http://cdn.thefrisky.com/images/uploads/rachel_dratch_102810_m.jpg")
      },
      {
        "br-online.de", // http://www.br-online.de/br-klassik/programmtipps/highlight-bayreuth-tannhaeuser-festspielzeit-2011-ID1309895438808.xml
        expectCorrectText()
          .title("Eröffnung der 100. Bayreuther Festspiele: Alles neu beim \"Tannhäuser\" | Programmtipps | BR-KLASSIK")
          .imageUrl("http://cdn.thefrisky.com/images/uploads/rachel_dratch_102810_m.jpg")
      },
      {
        "galtime.com", // http://galtime.com/article/entertainment/37/22938/kris-humphries-avoids-kim-talk-gma
        expectCorrectText()
          .title("Kris Humphries Avoids Kim Talk On GMA | Entertainment")
          .imageUrl("http://cdn.thefrisky.com/images/uploads/rachel_dratch_102810_m.jpg")
      },
      {
        "galtime.com", // http://galtime.com/article/entertainment/37/22938/kris-humphries-avoids-kim-talk-gma
        expectCorrectText()
          .title("Kris Humphries Avoids Kim Talk On GMA | Entertainment")
          .imageUrl("http://cdn.thefrisky.com/images/uploads/rachel_dratch_102810_m.jpg")
      },
      {
        "i4online.com", // https://i4online.com
        expectCorrectText()
      },
      {
        "searchenginejournal.com", // http://www.searchenginejournal.com/planning-progress-18-tips-successful-social-media-strategy/112567/
        expectCorrectText()
          .title("18 Tips for a Successful Social Media Strategy")
      },
      {
        "adweek.com", // http://www.adweek.com/prnewser/5-digital-data-metricstools-that-pr-pros-need-to-know/97735?red=pr
        expectCorrectText()
          .title("5 Digital Metrics/Tools That PR Pros Need to Know")
      },
      {
        "spinsucks.com", // http://spinsucks.com/communication/2015-communications-trends/
        expectCorrectText()
          .title("The Five Communications Trends for 2015 Spin Sucks")
      },
      {
        "prdaily.com", // http://www.prdaily.com/Main/Articles/7_PR_blogs_worth_reading_17870.aspx
        expectCorrectText()
          .title("7 PR blogs worth reading")
      },
      {
        "marthastewartweddings.com", // http://www.marthastewartweddings.com/363473/bridal-beauty-diaries-lauren-%25E2%2580%2593-toning-and-cutting-down
        expectCorrectText()
          .title("Bridal Beauty Diaries: Lauren – Toning Up and Cutting Down")
      },
      {
        "notebookcheck.com", // http://www.notebookcheck.com/UEbernahme-Microsoft-schluckt-Devices-und-Services-Sparte-von-Nokia.115522.0.html
        expectCorrectText()
          .title("Übernahme: Microsoft schluckt Devices und Services Sparte von Nokia - Notebookcheck.com News")
      },
      {
        "people.com", // http://www.people.com/article/ryan-seacrest-marriage-turning-40
        expectCorrectText()
          .title("Ryan Seacrest on Marriage: 'I Want What My Mom and Dad Have' - American Idol, Ryan Seacrest : People.com")
      },
      {
        "people.com_2", // http://www.people.com/article/truck-driver-rescues-family-burning-car-video
        expectCorrectText()
          .title("Truck Driver Rescues Family in Burning Wreck : People.com")
      },
      {
        "people.com_3", // http://www.people.com/article/pierce-brosnan-jimmy-fallon-goldeneye-007-n64
        expectCorrectText()
          .title("Pierce Brosnan Plays Jimmy Fallon in 'GoldenEye 007' on 'Tonight Show' : People.com")
          .author("Alex Heigl")
      },
      {
        "entrepreneur.com", // http://www.entrepreneur.com/article/237402
        expectCorrectText()
          .title("7 Big Changes in the PR Landscape Every Business Should Know About")
          .author("Rebekah Iliff")
      },
      {
        "huffingtonpost.com_2", // http://www.huffingtonpost.com/rebekah-iliff/millions-of-consumers-aba_b_5269051.html
        expectCorrectText()
          .title("Millions of Consumers Abandon Hashtag for Backslash ")
          .author("Rebekah Iliff")
      },
      {
        "allvoices.com", // http://www.allvoices.com/article/17660716
        expectCorrectText()
          .title("Marchex exec: Lead generation moving away from 'faceless transactions'")
      },
      {
        "rocketfuel.com", // http://rocketfuel.com/blog/you-wont-be-seeing-coca-cola-ads-for-awhile-the-reason-why-is-amazing
        expectCorrectText()
          .title("You Won't be Seeing Coca Cola Ads for Awhile. The Reason why Is Amazing.")
      },
      {
        "huffingtonpost.com_3", // http://www.huffingtonpost.com/2015/03/10/bruce-miller-san-francisco-49ers-domestic-violence_n_6836416.html
        expectCorrectText()
          .title("San Francisco 49ers Fullback Bruce Miller Arrested On Domestic Violence Charges")
      },
      {
        "prnewswire.com", // www.prnewswire.com/news-releases/tableau-to-present-at-upcoming-investor-conferences-300039248.html
        expectCorrectText()
          .title("Tableau to Present at Upcoming Investor Conferences -- SEATTLE, Feb. 24, 2015 /PRNewswire/ --")
      },
      {
        "trendkraft.de", // http://www.trendkraft.de/it-software/freigegeben-und-ab-sofort-verfuegbar-die-sechste-generation-des-ecm-systems-windream/
        expectCorrectText()
          .title("Freigegeben und ab sofort verfügbar: die sechste Generation des ECM-Systems windream")
      },
      {
        "medium.com", // https://medium.com/@nathanbruinooge/a-travelogue-of-india-7b1f3aa62a19
        expectCorrectText()
          .title("A Travelogue of India — Medium")
      },
      {
        "qualcomm.com", // https://www.qualcomm.com/news/releases/2014/10/16/qualcomm-declares-quarterly-cash-dividend
        expectCorrectText()
          .title("Qualcomm Declares Quarterly Cash Dividend")
      },
      {
        "apple.com", // http://www.apple.com/pr/library/2015/04/27Apple-Expands-Capital-Return-Program-to-200-Billion.html
        expectCorrectText()
          .title("Apple - Press Info - Apple Expands Capital Return Program to $200 Billion")
      },
      {
        "apple.com_2", // www.apple.com/pr/library/2015/03/09Apple-Watch-Available-in-Nine-Countries-on-April-24.html
        expectCorrectText()
          .title("Apple - Press Info - Apple Watch Available in Nine Countries on April 24")
      },
      {
        "fortune.com", // http://fortune.com/2015/05/11/rackspaces-support-other-cloud/
        expectCorrectText()
          .title("Will Rackspace support Google's or Amazon's clouds? - Fortune")
      },
      {
        "adverts.ie", // http://www.adverts.ie/lego-building-toys/lego-general-zod-minifigure-brand-new/5980084
        expectCorrectText()
          .title("Lego General Zod Minifigure Brand New For Sale in Tralee, Kerry from dlaw1")
      },
      {
        "cloudcomputingexpo.com", // www.cloudcomputingexpo.com/node/3342675
        expectCorrectText()
      },
      {
        "cloudcomputingexpo.com_2",
        expectCorrectText()
      },
    });
  }

  public ArticleExtractorWithPlainTextFormatterTest(String testName, Expectations expectations){
    super(testName, expectations, FORMATTER, INPUT_DIR, OUTPUT_DIR, INPUT_EXT, OUTPUT_EXT);
  }

  @Test
  public void test(){
    doTest();
  }
}
