package com.tuplejump.playYeoman

import play.api._
import play.api.mvc._
import controllers.{ExternalAssets, Assets}
import play.api.Play.current
import java.io.File
import play.api.libs.MimeTypes
import scala.concurrent.Future
import scala.collection.JavaConversions._
import play.api.libs.concurrent.Execution.Implicits._


object Yeoman extends Controller {

  def index = Action.async {
    request =>
      if (request.path.endsWith("/")) {
        //AsyncResult {
        at("index.html").apply(request)
        //}
      } else {
        Future {
          Redirect(request.path + "/")
        }
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
    Play.configuration.getString("yeoman.distDir") match {
      case Some(distDir) => Assets.at(distDir, file)
      case None => Assets.at("/ui/dist", file)
    }
  }

  lazy val atHandler = if (Play.isProd) assetHandler(_: String) else DevAssets.assetHandler(_: String)

  def at(file: String): Action[AnyContent] = {
    atHandler(file)
  }

}

object DevAssets extends Controller {
  // paths to the grunt compile directory or else the application directory, in order of importance
  val runtimeDirs = Play.configuration.getStringList("yeoman.devDirs")
  val basePaths: List[java.io.File] = runtimeDirs match {
    case Some(dirs) => dirs.map(Play.application.getFile _).toList
    case None => List(Play.application.getFile("ui/.tmp"), Play.application.getFile("ui/app"))
  }

  /**
   * Construct the temporary and real path under the application.
   *
   * The play application path is prepended to the full path, to make sure the
   * absolute path is in the correct SBT sub-project.
   */
  def assetHandler(fileName: String): Action[AnyContent] = Action {
    val targetPaths = basePaths.view map {
      new File(_, fileName)
    } // generate a non-strict (lazy) list of the full paths

    // take the files that exist and generate the response that they would return
    val responses = targetPaths filter {
      _.exists()
    } map {
      Ok.sendFile(_, inline = true).withHeaders(CACHE_CONTROL -> "no-store")
    }

    // return the first valid path, return NotFound if no valid path exists
    responses.headOption getOrElse NotFound
  }
}
