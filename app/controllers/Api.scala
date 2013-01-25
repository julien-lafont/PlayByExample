package controllers

import play.api._
import play.api.mvc._
import services.github._
import play.api.libs.concurrent.Execution.Implicits._

import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import concurrent.Future

object Api extends GithubOAuthController {

  private def getLanguageField(name: String, field: String) = (__ \ name).json.copyFrom(
    (__ \ "files").json.pick[JsObject].map{ js =>
      js.fields.collectFirst{ case (k, v) if(v \ "language" != JsNull) => v \ field }.get
    }
  )

  private def getQuestionField(name: String, field: String) = (__ \ name).json.copyFrom(
    (__ \ "files").json.pick[JsObject].map{ js =>
      js.fields.collectFirst{ case (k, v) if(v \ "language" == JsNull) => v \ field }.get
    }
  )

  private val fullTransfForks = (
    (__ \ "url").json.pickBranch and
    (__ \ "id").json.pickBranch and
      getQuestionField("questionUrl", "raw_url") and
      getQuestionField("questionContent", "content") and
      getLanguageField("answerUrl", "raw_url") and
      getLanguageField("answerContent", "content")
    ).reduce

  def listForks(gistId: Long) = Authenticated { implicit req =>
    Async {
      GithubWS.Gist.forksId(gistId).flatMap{ list =>
        val listJsonFuture = list.map(forkId =>
          GithubWS.Gist.get(forkId).map(json =>
            json.transform(fullTransfForks).getOrElse(JsNull)))
        Future.sequence(listJsonFuture).map(listJson =>
          Ok(Json.toJson(listJson))
        )
      }
    }
  }

  def getQuestion(gistId: Long) = Authenticated { implicit req =>
    Async {
      GithubWS.Gist.get(gistId).map(json =>
        Ok(json.transform(
          ( getQuestionField("questionUrl", "raw_url") and
            getQuestionField("questionContent", "content")
          ).reduce).getOrElse(JsNull))
      )
    }
  }

}