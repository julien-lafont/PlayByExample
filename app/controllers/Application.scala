package controllers

import play.api._
import play.api.mvc._
import services.github._
import play.api.libs.concurrent.Execution.Implicits._

object Application extends GithubOAuthController {
  def main = Action {
    Ok(views.html.main())
  }

  def index = Action {
    Ok(views.html.index())
  }

  def testGithub = Authenticated { implicit request =>
    Async {

      // Return user details
      //GithubWS.User.me.map(Ok(_))

      // Create new gist
      GithubWS.Gist.create("My awesome question", "scala", "YoTsumi").map(r =>
        Ok(r.map("Gist ID = "+_).getOrElse("Cannot create gist")))
    }
  }
}
