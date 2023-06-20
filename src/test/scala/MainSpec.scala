import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers



class MainSpec extends AnyFlatSpec with Matchers {

  "formatUrl" should "return the formatted URL" in {
    val keyword = "test"
    val limit = 5
    val expectedUrl = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=&sroffset=0&list=search&srsearch=test&srlimit=5"

    val formattedUrl = Main.formatUrl(keyword, limit)

    formattedUrl should be(expectedUrl)
  }

  "parseJson" should "return a list of WikiPages" in {
    val rawJson =
      """
        |{
        |  "query": {
        |    "search": [
        |      {
        |        "title": "Page 1",
        |        "wordcount": 500
        |      },
        |      {
        |        "title": "Page 2",
        |        "wordcount": 800
        |      }
        |    ]
        |  }
        |}
        |""".stripMargin

    val expectedPages = Seq(
      WikiPage("Page 1", 500),
      WikiPage("Page 2", 800)
    )

    val parsedPages = Main.parseJson(rawJson)

    parsedPages should be(expectedPages)
  }

  "totalWords" should "return 0 for an empty list" in {
    val pages = Seq.empty[WikiPage]

    val result = Main.totalWords(pages)

    result should be(0)
  }

  it should "return the sum of words for a non-empty list" in {
    val pages = Seq(
      WikiPage("Page 1", 500),
      WikiPage("Page 2", 800),
      WikiPage("Page 3", 300)
    )

    val result = Main.totalWords(pages)

    result should be(1600)
  }

  "parseArguments" should "return None for non-parseable arguments" in {
    val args = Array("--limit", "abc", "keyword")

    val result = Main.parseArguments(args)

    result should be(None)
  }

  it should "return Some(Config) for a keyword argument" in {
    val args = Array("keyword")

    val result = Main.parseArguments(args)

    result should be(Some(Config(keyword = "keyword")))
  }

  it should "return Some(Config) for keyword and limit arguments" in {
    val args = Array("--limit", "5", "keyword")

    val result = Main.parseArguments(args)

    result should be(Some(Config(limit = 5, keyword = "keyword")))
  }

}

