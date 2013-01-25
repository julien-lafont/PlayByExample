package controllers

import scala.concurrent.Future

import play.api._
import play.api.mvc._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import services.github._
import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Questions extends GithubOAuthController {

  val createReads = (
    (__ \ 'author).read[String] and
    (__ \ 'title).read[String] and
    (__ \ 'play).read[String] and
    (__ \ 'lang).read[String]
  ).tupled

  def readView = Action {
    Ok(views.html.questions.read())
  }

  def get(id: String) = Action { request =>
    // TODO: get question from GitHub
    Ok(Json.obj("title" -> "Test", "play" -> "2.1.RC2", "lang" -> "scala", "author" -> "Someone"))
  }

  def listView = Action {
    Ok(views.html.questions.list())
  }

  def createView = Authenticated { implicit request =>
    Ok(views.html.questions.create())
  }

  def create = Authenticated { implicit request =>
    request.body.asJson.get.validate(createReads).map{ case (author, title, play, lang) =>
      Async{
        GithubWS.Gist.create(title, lang, author).flatMap{
          case Some(id) =>
            val tags = Seq(Tag(play), Tag(lang))
            val q = Question(author, title, id, tags)
            Question.insert(q).map { lasterror =>
              Ok(Json.obj("status" -> "OK"))
            }.recover{ case e => 
              InternalServerError( Json.obj("status" -> "OK", "error" -> "mongo exception %s".format(e.getMessage) ) )
            }
          case None => Future.successful(InternalServerError( Json.obj("status" -> "OK", "error" -> "couldn't create Gist" ) ))
        }.recover{ 
          case e => BadRequest(Json.obj("status" -> "KO", "error" -> e.getMessage))
        }
      }
    }.recoverTotal{ e =>
      BadRequest(Json.obj("status" -> "KO", "error" -> JsError.toFlatJson(e)))
    }
  }
}