package models

import org.joda.time._

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
  authorID: GHAuthorID,
  title: String,
  gistID: GHGistID,
  tags: Seq[Tag] = Seq(),
  createdAt: DateTime = DateTime.now,
  updatedAt: DateTime = DateTime.now
)

object Question extends Function6[GHAuthorID, String, GHGistID, Seq[Tag], DateTime, DateTime, Question] {
  val db = ReactiveMongoPlugin.db
  lazy val coll = db("questions")

  implicit val fmt = Json.format[Question]

  val generateId = (__ \ '_id \ '$oid).json.put( JsString(BSONObjectID.generate.stringify) )
  def dateToMongo(field: String) =  (__ \ field \ '$date).json.copyFrom( (__ \ field).json.pick )

  val transformer = __.json.update( (generateId and dateToMongo("createdAt") and dateToMongo("updatedAt")).reduce )

  def insert(q: Question)(implicit ex: ExecutionContext): Future[LastError] = {
    val js = Json.toJson(q)(fmt)
    js.transform(transformer).map{ js =>
      coll.insert(js)
    }.recoverTotal{ e =>
      Future.failed(new RuntimeException(e.toString))
    }
  }

}
