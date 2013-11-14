package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.json._
import play.api.templates.Html

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def templateDemo = Action {
    Ok(views.html.demo("Scala template in Angular")
      (Html("This is a play scala template in angular views folder which is compiled and used inplace!</div>"))
    )
  }

  def json(keyword: String) = Action {
    Ok(JsObject("keyword" -> JsString(keyword) :: Nil))
  }

  def serveTemplate = Action {
    Ok(views.html.templ("Compiled from a scala template!"))
  }
}