package com.tuplejump.playYeoman

import play.api._
import play.api.mvc._
import play.api.Play.current
import java.io.File
import play.api.libs.MimeTypes
import controllers.{ExternalAssets, Assets}


object Yeoman extends Controller {

  def index = Action {
    request =>
      if (request.path.endsWith("/")) {
        AsyncResult {
          at("index.html").apply(request)
        }
      } else {
        Redirect(request.path + "/")
      }
  }


  def redirectRoot(base: String = "/ui/") = Action {
    request =>
      if (base.endsWith("/")) {
        Redirect(base)
      } else {
        Redirect(base + "/")
      }
  }

  def assetHandler(file: String) = {
    Assets.at("/ui/dist", file)
  }

  /**
   * Serve either compiled files (from .tmp) if they exist, or otherwise from app.
   */
  def extAssetHandler(file: String) = {
    val tmpPath = "ui/.tmp"
    val appPath = "ui/app"

    val f = new File(tmpPath, file)
    if (f.exists)
      DevAssets.at(tmpPath, file, CACHE_CONTROL -> "no-store")
    else
      DevAssets.at(appPath, file, CACHE_CONTROL -> "no-store")
  }

  lazy val atHandler: (String => Action[AnyContent]) = if (Play.isProd) assetHandler(_: String) else extAssetHandler(_: String)

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
