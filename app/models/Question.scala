package models

import org.joda.time._
import scala.util.parsing.combinator.RegexParsers

import scala.concurrent._

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._
import reactivemongo.core.commands.LastError

import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import play.api.Play.current

case class Question(
  authorID: GHID,
  title: String,
  gistID: GHGistID,
  tags: Seq[Tag] = Seq(),
  createdAt: DateTime = DateTime.now,
  updatedAt: DateTime = DateTime.now
)

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

object Question extends Function6[String, String, String, Seq[Tag], DateTime, DateTime, Question] {
  val db = ReactiveMongoPlugin.db
  lazy val coll = db("questions")

  implicit val fmt = Json.format[Question]

  val generateId = (__ \ '_id \ '$oid).json.put( JsString(BSONObjectID.generate.stringify) )
  def dateToMongo(field: String) =  (__ \ field \ '$date).json.copyFrom( (__ \ field).json.pick )

  val transformer = __.json.update( (generateId and dateToMongo("createdAt") and dateToMongo("updatedAt")).reduce )

  def insert(q: Question)(implicit ex: ExecutionContext): Future[LastError] = {
    val js = Json.toJson(q)(fmt)
    println("js: "+ js.transform(transformer))
    js.transform(transformer).map{ js =>
      coll.insert(js)
    }.recoverTotal{ e =>
      Future.failed(new RuntimeException(e.toString))
    }
  }

  def fetchTag(q: Question): Seq[Tag] = Tag.fetchTags(q.title)

}
