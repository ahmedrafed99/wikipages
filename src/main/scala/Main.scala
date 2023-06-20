import scopt.OParser
import scalaj.http.{Http, HttpResponse}
import play.api.libs.json.{Json, JsValue, JsArray}
import test.utils.HttpUtils

case class Config(limit: Int = 10, keyword: String = "")

case class WikiPage(title: String, words: Int)

object Main extends App {
  parseArguments(args) match {
    case Some(config) => run(config)
    case _            => println("Unable to parse arguments")
  }

  def parseArguments(args: Array[String]): Option[Config] = {
    val builder = OParser.builder[Config]
    val parser = {
      import builder._
      OParser.sequence(
        programName("WikiStats"),
        opt[Int]("limit")
          .action((value, config) => config.copy(limit = value))
          .text("Number of pages returned by the API (default: 10)"),
        arg[String]("keyword")
          .required()
          .action((value, config) => config.copy(keyword = value))
          .text("Keyword to search for")
      )
    }

    OParser.parse(parser, args, Config())
  }

  def run(config: Config): Unit = {
    println(config)
    val apiUrl = formatUrl(config.keyword, config.limit)
    val result = getPages(apiUrl)

    result match {
      case Right(body) =>
        println("API Result:")
        println(body)

        val wikiPages = parseJson(body)
        println("Parsed Wiki Pages:")
        wikiPages.foreach(println)

        val total = totalWords(wikiPages)
        val average = if (wikiPages.nonEmpty) total.toDouble / wikiPages.length.toDouble else 0.0
        println(s"Total number of words: $total")
        println(f"Average number of words per page: $average%.2f")
      case Left(code) =>
        println(s"Error: API response with code $code")
    }
  }

  
  def formatUrl(keyword: String, limit: Int): String = {
    s"https://en.wikipedia.org/w/api.php?action=query&format=json&prop=&sroffset=0&list=search&srsearch=$keyword&srlimit=$limit"
  }

  def getPages(url: String, httpUtils: HttpUtils): Either[Int, String] = {
    httpUtils.parse(url)
  }


  /*def getPages(url: String): Either[Int, String] = {
    val response: HttpResponse[String] = Http(url).asString

    if (response.code == 200) {
      Right(response.body)
    } else {
      Left(response.code)
    }
  } */
  
  def parseJson(rawJson: String): Seq[WikiPage] = {
    val json: JsValue = Json.parse(rawJson)
    val pages: Seq[JsValue] = (json \ "query" \ "search").as[JsArray].value

    pages.map { page =>
      val title = (page \ "title").as[String]
      val words = (page \ "wordcount").as[Int]
      WikiPage(title, words)
    }
  }

  def totalWords(pages: Seq[WikiPage]): Int = {
    pages.foldLeft(0)(_ + _.words)
  }


}


