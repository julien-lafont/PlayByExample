package controllers

import play.api._
import play.api.mvc._
import services.github._
import play.api.libs.concurrent.Execution.Implicits._

object Application extends GithubOAuthController {

  def index = Action {
    Ok(views.html.index("Play By Example"))
  }

  def testGithub = Authenticated { implicit request =>
    Async {
      GithubWS.User.me.map(Ok(_))
    }
  }

}
