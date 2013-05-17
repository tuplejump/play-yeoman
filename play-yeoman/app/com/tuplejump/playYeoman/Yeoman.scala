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

  //This is only used in dev mode. In production the styles files is `someidentifier`.main.css
  def style = Action {
    //if(Play.isDev)
    Ok.sendFile(new File("ui/.tmp/styles/main.css")).as(MimeTypes.forFileName("main.css").getOrElse("text/css"))
  }

  def assetHandler(file: String) = {
    Assets.at("/ui/dist", file)
  }

  def extAssetHandler(file: String) = {
    ExternalAssets.at("ui/app", file)
  }

  lazy val atHandler = if (Play.isProd) assetHandler(_: String) else extAssetHandler(_: String)

  def at(file: String): Action[AnyContent] = {
    atHandler(file)
  }

}