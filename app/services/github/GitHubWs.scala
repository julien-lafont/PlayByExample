package services.github

import play.api.libs.ws._
import services.auth.OAuth2Token
import play.api.libs.ws.WS.WSRequestHolder
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import concurrent.Future

object GithubWS {

  def fetch(url: String, accept: String = "application/json")(implicit token: OAuth2Token): WSRequestHolder = {
    WS.url("https://api.github.com" + url)
      .withQueryString("access_token" -> token.accessToken)
      .withHeaders("Accept" -> accept)
  }

  object User {

    /**
     * Return information about the connected user
     */
    def me(implicit token: OAuth2Token) = {
      fetch("/users/user").get.map(_.json)
    }

    /**
     * Return information about an user
     */
    def info(user: String)(implicit token: OAuth2Token) = {
      fetch(s"/users/$user").get.map(_.json)
    }
  }

  object Gist {

    /**
     * Create a new Gist with 2 files: the question and an empty answer
     * @return The Gist ID if success, otherwise None
     */
    def create(question: String, extension: String, author: String)(implicit token: OAuth2Token): Future[Option[Long]] = {
      val data = Json.obj(
        "description" -> s"PlayByExample: $question",
        "public" -> true,
        "files" -> Map(
          s"0. $question.txt" -> Map(
            "content" -> question
          ),
          s"1. Answer.$extension" -> Map(
            "content" -> "// Implement your solution here"
          )
        )
      )

      fetch("/gists").post(data).map(result =>
        result.status match {
          case 201 => (result.json \ "id").asOpt[String].map(_.toLong)
          case _ => None
        })
    }

    def star(gistId: Long)(implicit token: OAuth2Token) = {
      fetch(s"/gists/$gistId/star").put("")
    }

    def unstar(gistId: Long)(implicit token: OAuth2Token) = {
      fetch(s"/gists/$gistId/star").delete()
    }

    def get(gistId: Long)(implicit token: OAuth2Token) = {
      fetch(s"/gists/$gistId").get.map(_.json)
    }

    def forksId(gistId: Long)(implicit token: OAuth2Token) = {
      get(gistId).map(json =>
        (json \ "forks").as[JsArray].value.map(fork =>
          (fork \ "id").as[String].toLong))
    }
  }

}
