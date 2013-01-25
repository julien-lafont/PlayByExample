package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Application extends GithubOAuthController {

  def index = Action {
    Ok(views.html.index("Play By Example"))
  }

  def testGithub = Authenticated { implicit request =>
    Async {
      Ws.url("https://api.github.com/users/studiodev/gists").get.map(r => Ok(r.json))
    }
  }

}
