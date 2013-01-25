package services.github

import play.api.libs.ws._
import services.auth.AuthenticatedRequest
import play.api.libs.ws.WS.WSRequestHolder
import play.api.libs.concurrent.Execution.Implicits._

object GithubWS {

  def fetch(url: String, accept: String = "application/json")(implicit request: AuthenticatedRequest): WSRequestHolder = {
    WS.url("https://api.github.com" + url)
      .withQueryString("access_token" -> request.token.accessToken)
      .withHeaders("Accept" -> accept)
  }

  object User {

    def me(implicit request: AuthenticatedRequest) = {
      fetch("/users/user").get.map(_.json)
    }

    def info(user: String)(implicit request: AuthenticatedRequest) = {
      fetch(s"/users/$user").get.map(_.json)
    }
  }

}