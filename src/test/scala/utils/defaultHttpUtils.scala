package utils

object DefaultHttpUtils extends HttpUtils {
  def parse(url: String): HttpRequest = {
    // Implement the logic to parse the URL and return an HttpRequest object
    // using the actual HTTP functionality
    Http(url).asString
  }
}