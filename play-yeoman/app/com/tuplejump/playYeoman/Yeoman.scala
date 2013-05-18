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
    if(f.exists)
      ExternalAssets.at("ui/.tmp", file)
    else
      ExternalAssets.at("ui/app", file)
  }

  lazy val atHandler = if (Play.isProd) assetHandler(_: String) else extAssetHandler(_: String)

  def at(file: String): Action[AnyContent] = {
    atHandler(file)
  }

}
