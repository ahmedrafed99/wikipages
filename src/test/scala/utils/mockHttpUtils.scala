package utils

object MockHttpUtils extends HttpUtils {
  def parse(url: String): Either[Int, String] = {
    // Mock implementation that does not make a network call
    Right("Mocked response")
  }
}

