import com.tuplejump.sbt.yeoman.Yeoman

name := "yo-demo"

version := "2.0.0"

scalaVersion := "2.11.6"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

Yeoman.yeomanSettings ++ Yeoman.withTemplates
