import scopt.OParser
import scalaj.http.{Http, HttpResponse}

case class Config(limit: Int = 10, keyword: String = "")

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
      case Left(code) =>
        println(s"Error: API response with code $code")
    }
  }

  
  def formatUrl(keyword: String, limit: Int): String = {
    s"https://en.wikipedia.org/w/api.php?action=query&format=json&prop=&sroffset=0&list=search&srsearch=$keyword&srlimit=$limit"
  }

  

  def getPages(url: String): Either[Int, String] = {
    val response: HttpResponse[String] = Http(url).asString

    if (response.code == 200) {
      Right(response.body)
    } else {
      Left(response.code)
    }
  }


}
