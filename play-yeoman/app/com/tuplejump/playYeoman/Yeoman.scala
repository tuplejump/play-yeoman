package com.tuplejump.playYeoman

import play.api._
import play.api.mvc._
import play.api.Play.current
import java.io.File
import play.api.libs.MimeTypes


object Yeoman extends controllers.AssetsBuilder {

  def index = Action {
    request =>
      Redirect((request.path + "/index.html").replace("//", "/"))
  }

  def style =
    if (Play.isProd) {
      super.at(path = "/ui/app", "styles/main.css")
    } else {
      Logger.logger.info("Running in dev mode. Serving from local folder -- " + new File("."))
      Action {
        Ok.sendFile(new File("ui/.tmp/styles/main.css")).as(MimeTypes.forFileName("main.css").getOrElse("text/css"))
      }
    }


  override def at(path: String, file: String): Action[AnyContent] = super.at(path, file)

}