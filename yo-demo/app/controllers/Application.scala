package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.json._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def json(keyword: String) = Action {
    Ok(JsObject("keyword" -> JsString(keyword) :: Nil))
  }

}