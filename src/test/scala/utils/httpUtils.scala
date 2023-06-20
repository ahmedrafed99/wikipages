package utils
trait HttpUtils {
  def parse(url: String): Either[Int, String]
}
