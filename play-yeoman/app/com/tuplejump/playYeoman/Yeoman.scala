package com.tuplejump.playYeoman

import play.api._
import play.api.mvc._
import play.api.Play.current
import java.io.File
import play.api.libs.MimeTypes
import controllers.{ExternalAssets, Assets}


object Yeoman extends Controller {
  lazy val path = if (Play.isProd) "ui/dist" else "ui/app"

  def index = Action {
    request =>
      Redirect((request.path + "/index.html").replace("//", "/"))
  }

  def assetHandler(file: String) = {
    Assets.at("/ui/dist", file)
  }

  /**
   * Serve either compiled files (from .tmp) if they exist, or otherwise from app.
   */
  def extAssetHandler(file: String) = {
    val f = new File("ui/.tmp", file)
    if (f.exists)
      DevAssets.at("ui/.tmp", file, CACHE_CONTROL -> "no-store")
    else
      DevAssets.at("ui/app", file, CACHE_CONTROL -> "no-store")
  }

  lazy val atHandler = if (Play.isProd) assetHandler(_: String) else extAssetHandler(_: String)

  def at(file: String): Action[AnyContent] = {
    atHandler(file)
  }

}

object DevAssets extends Controller {
  def at(rootPath: String, file: String, headers: (String, String)*): Action[AnyContent] = Action {
    val fileToServe = new File(Play.application.getFile(rootPath), file)

    if (fileToServe.exists) {
      Ok.sendFile(fileToServe, inline = true).withHeaders(headers: _*)
    } else {
      NotFound
    }
  }
}
