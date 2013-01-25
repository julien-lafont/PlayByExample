package controllers

import play.api._
import play.api.mvc._
import services.github._
import play.api.libs.concurrent.Execution.Implicits._

import play.api.libs.json._
import play.api.libs.json.Json._
import concurrent.Future

object Api extends GithubOAuthController {

  def listGistsWithForks(gistId: Long) = Authenticated { implicit req =>
    Async {
      GithubWS.Gist.forksId(gistId).flatMap{ list =>
        val listJsonFuture = list.map(forkId =>
          GithubWS.Gist.get(forkId).map(json =>
            // TODO EXTRAIRE DONNEES JSON
            json
          )
        )
        Future.sequence(listJsonFuture).map(listJson =>
          Ok(Json.toJson(listJson))
        )
      }
    }
  }

  /*def gistInfo(gistId: String) = Authenticated { implicit req =>
    Async {
      /*val extractGist: Reads[JsObject] = (
        (__ \ 'comments_url).json.pick and
        (__ \ 'git_push_url).json.pick
      ).reduce*/

      GithubWS.Gist.get(4634703).map{ json =>
        //val r = json.validate[extractGist]

        Ok(json)
      }
    }
  }*/
}