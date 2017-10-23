package com.tuplejump.playYeoman

import java.io.File
import javax.inject.Inject

import controllers.Assets
import play.api._
import play.api.mvc._

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Yeoman @Inject() (environment: play.api.Environment,
                        devAssets: DevAssets) extends Controller {

  def at(file: String): Action[AnyContent] = atHandler(file)

  def index = Action.async {
    request =>
      if (request.path.endsWith("/")) {
        at("index.html").apply(request)
      } else {
        Future(Redirect(request.path + "/"))
      }
  }

  def assetHandler(file: String): Action[AnyContent] = {
    Assets.at("/public", file)
  }

  def redirectRoot(base: String = "/ui/") = Action {
    request =>
      if (base.endsWith("/")) {
        Redirect(base)
      } else {
        Redirect(base + "/")
      }
  }

  lazy val atHandler: String => Action[AnyContent] = if (environment.mode==Mode.Prod) {
    assetHandler(_: String)
  } else devAssets.assetHandler(_: String)

}

class DevAssets @Inject() (environment: play.api.Environment,
                           configuration: play.api.Configuration,
                           implicit val mimeTypes: play.api.http.FileMimeTypes) extends Controller {
  // paths to the grunt compile directory or else the application directory, in order of importance
  val runtimeDirs = configuration.getStringList("yeoman.devDirs")
  val basePaths: List[java.io.File] = runtimeDirs match {
    case Some(dirs) => dirs.asScala.map(environment.getFile _).toList
    case None => List(environment.getFile("ui/.tmp"), environment.getFile("ui/app"),
      //added ui to defaults since the newer projects have bower_components in ui directory instead of ui/app/components
      environment.getFile("ui"))
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
      file =>
        file.exists()
    } map {
      file =>
        Ok.sendFile(file, inline = true).withHeaders(CACHE_CONTROL -> "no-store")
    }

    // return the first valid path, return NotFound if no valid path exists
    responses.headOption getOrElse NotFound
  }
}
