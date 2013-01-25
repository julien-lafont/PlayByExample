package controllers

import play.api._
import play.api.mvc._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext


object Questions extends Controller {

  val createReads = (
    (__ \ 'author).read[String] and
    (__ \ 'title).read[String] and
    (__ \ 'play).read[String] and
    (__ \ 'lang).read[String]
  ).tupled

  def list = Action {
    Ok(views.html.questions.list())
  }

  def add = Action {
    Ok(views.html.questions.create())
  }

  def create = Action(parse.json){ request =>
    
    request.body.validate(createReads).map{ case (author, title, play, lang) =>
      // TODO create gist

      val gistId = ""
      val tags = Seq(Tag(play), Tag(lang))
      val q = Question(author, title, gistId, tags)

      Async{
        Question.insert(q).map { lasterror =>
          Ok(Json.obj("status" -> "OK"))
        }.recover{ case e => 
          InternalServerError( Json.obj("status" -> "OK", "error" -> "mongo exception %s".format(e.getMessage) ) )
        }
      }
    }.recoverTotal{ e =>
      BadRequest(Json.obj("status" -> "KO", "error" -> JsError.toFlatJson(e)))
    }
  }
}