package models

import scala.util.parsing.combinator.RegexParsers

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import play.api.Play.current

case class Tag(name: String) extends AnyVal

object Tag extends Function1[String, Tag]{
  implicit val tagReads = (__ \ "name").read[String].map( n => Tag(n) )
  implicit val tagWrites = Writes{ t: Tag => JsString(t.name) }

  val hash = "#"

  object TagParser extends RegexParsers {

    def hash: Parser[String] = Tag.hash
    def hashname: Parser[String] = """[a-zA-Z0-9._]+""".r
    def hashtag: Parser[Tag] = hash ~> hashname ^^ { t => Tag(t) }
    def excapedHash: Parser[String] = hash ~ hash ^^ { _ => Tag.hash }
    def content: Parser[String] = ("""[^"""+Tag.hash+"""]+""").r
    def string: Parser[Seq[Either[String, Tag]]] = (excapedHash ^^ { Left(_) } | hashtag ^^ { Right(_) } | content ^^ { Left(_) } )*

    def apply(input: String): Seq[Either[String, Tag]] = parseAll(string, input) match {
      case Success(result, _) => result
      case failure : NoSuccess => scala.sys.error(failure.msg)
    }

  }

  def fetchTags(str: String): Seq[Tag] = TagParser(str).collect{ case Right(t) => t }
}
