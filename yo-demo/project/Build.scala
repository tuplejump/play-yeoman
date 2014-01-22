import sbt._
import Keys._
import play.Project._
import com.tuplejump.sbt.yeoman.Yeoman

object ApplicationBuild extends Build {

  val appName = "yo-demo"
  val appVersion = "1.6.4"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )

  val appSettings = Yeoman.yeomanSettings ++ Yeoman.withTemplates

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    appSettings: _*
  )

}
