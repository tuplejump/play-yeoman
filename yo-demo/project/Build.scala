import sbt._
import Keys._
import play.Project._
import com.tuplejump.sbt.yeoman.Yeoman

object ApplicationBuild extends Build {

  val appName         = "yo-demo"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here 
    Yeoman.yeomanSettings : _*     
  )

}
