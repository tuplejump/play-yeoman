import com.tuplejump.sbt.yeoman.Yeoman

name := "yo-demo"

version := "1.0.0"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayScala, Yeoman)

Yeoman.withTemplates
