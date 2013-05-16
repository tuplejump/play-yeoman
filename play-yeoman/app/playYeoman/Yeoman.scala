package com.tuplejump.playYeoman

import play.api._
import play.api.mvc._
import com.sun.corba.se.spi.presentation.rmi.StubAdapter

object Yeoman extends controllers.AssetsBuilder {

  def index = Action {
    request =>
      Redirect((request.path + "/index.html").replace("//", "/"))
  }

  override def at(path: String, file: String): Action[AnyContent] = super.at(path, file)

}