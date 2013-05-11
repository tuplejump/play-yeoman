package com.tuplejump.sbt.yeoman

import sbt._
import sbt.Keys._
import play.Project._
import java.net.InetSocketAddress
import scala.Some

/**
 * Created with IntelliJ IDEA.
 * User: rohit
 * Date: 5/10/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
object Yeoman extends Plugin {
  val yeomanDirectory = SettingKey[File]("yeoman-directory")

  val yeomanSettings: Seq[Project.Setting[_]] = Seq(
    // Turn off play's internal less compiler
    lessEntryPoints := Nil,

    // Turn off play's internal javascript compiler
    javascriptEntryPoints := Nil,

    // Where does the UI live?
    yeomanDirectory <<= (baseDirectory in Compile) {
      _ / "yeoman"
    },

    // Add the views to the dist
    playAssetsDirectories <+= (yeomanDirectory in Compile)(base => base / "app" / "views"),


    // Start grunt on play run
    playOnStarted <+= yeomanDirectory {
      base =>
        (address: InetSocketAddress) => {
          Grunt.process = Some(Process("grunt server", base).run)
        }: Unit
    },

    // Stop grunt when play run stops
    playOnStopped += {
      () => {
        Grunt.process.map(p => p.destroy())
        Grunt.process = None
      }: Unit
    },

    // Allow all the specified commands below to be run within sbt
    commands <++= yeomanDirectory {
      base =>
        Seq(
          "grunt",
          "bower",
          "yo",
          "npm",
          "reset"
        ).map(cmd(_, base))
    }
  )

  private def cmd(name: String, base: File): Command = {
    Command.args(name, "<" + name + "-command>") {
      (state, args) =>
        Process(name :: args.toList, base) !<;
        state
    }
  }
}

object Grunt {
  var process: Option[Process] = None
}
