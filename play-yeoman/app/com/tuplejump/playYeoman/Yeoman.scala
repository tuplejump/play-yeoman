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
      if (request.path.endsWith("/"))
        at("index.html").apply(request)
      else
        Redirect(request.path + "/")
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

  lazy val atHandler = if (Play.isProd) assetHandler(_: String) else DevAssets.assetHandler(_: String)

  def at(file: String): Action[AnyContent] = {
    atHandler(file)
  }

}

object DevAssets extends Controller {
  // paths to the grunt compile directory or else the application directory, in order of importance
  val basePaths = List(Play.application.getFile("ui/.tmp"), Play.application.getFile("ui/app"))

  /**
   * Construct the temporary and real path under the application.
   *
   * The play application path is prepended to the full path, to make sure the
   * absolute path is in the correct SBT sub-project.
   */
  def assetHandler(fileName: String): Action[AnyContent] = Action {
    val targetPaths = basePaths.view map { new File(_, fileName) } // generate a non-strict (lazy) list of the full paths

    // take the files that exist and generate the response that they would return
    val responses = targetPaths filter { _.exists() } map { Ok.sendFile(_, inline = true).withHeaders(CACHE_CONTROL -> "no-store")}

    // return the first valid path, return NotFound if no valid path exists
    responses.headOption getOrElse NotFound
  }
}