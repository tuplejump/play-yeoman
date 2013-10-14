/*
 * Copyright 2013 Tuplejump Software Pvt. Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.tuplejump.sbt.yeoman

import sbt._
import sbt.Keys._
import play.Project._
import java.net.InetSocketAddress
import scala.Some

object Yeoman extends Plugin {
  val yeomanDirectory = SettingKey[File]("yeoman-directory")
  val yeomanGruntfile = SettingKey[String]("yeoman-gruntfile")

  val doGrunt = inputKey[Unit]("Task to run grunt")

  val yeomanSettings: Seq[Project.Setting[_]] = Seq(
    libraryDependencies ++= Seq("com.tuplejump" %% "play-yeoman" % "0.6.3-SNAPSHOT" intransitive()),

    // Turn off play's internal less compiler
    lessEntryPoints := Nil,

    // Turn off play's internal javascript compiler
    javascriptEntryPoints := Nil,

    // Where does the UI live?
    yeomanDirectory <<= (baseDirectory in Compile) {
      _ / "ui"
    },

    yeomanGruntfile := "Gruntfile.js",

    doGrunt :=  {
        val base = (yeomanDirectory in Compile).value
        val gruntFile = (yeomanGruntfile in Compile).value
        //stringToProcess("grunt " + (Def.spaceDelimited("<arg>").parsed).mkString(" ")).!!,
        runGrunt(base, gruntFile, (Def.spaceDelimited("<arg>").parsed).mkString(" "))
    },

    // Add the views to the dist
    playAssetsDirectories <+= (yeomanDirectory in Compile)(base => base / "dist"),

    // Start grunt on play run
    playOnStarted <+= (yeomanDirectory, yeomanGruntfile) {
      (base, gruntFile) =>
        (address: InetSocketAddress) => {
          println(gruntFile)
          Grunt.process = runGrunt(base, gruntFile, " server --force")
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
          "npm"
        ).map(cmd(_, base))
    }
  )


  private def runGrunt(base: sbt.File, gruntFile: String, args: String) = {
    println(s"Will run: grunt --gruntfile=$gruntFile $args in ${base.getPath}")
    if (System.getProperty("os.name").startsWith("Windows"))
      Some(Process("cmd /c grunt --gruntfile=" + gruntFile, base).run)
    else
      Some(Process("grunt --gruntfile=" + gruntFile, base).run)
  }

  private def cmd(name: String, base: File): Command = {
    if (!base.exists()) (base.mkdirs())
    Command.args(name, "<" + name + "-command>") {
      (state, args) =>
        if (System.getProperty("os.name").startsWith("Windows"))
          Process("cmd" :: "/c" :: name :: args.toList, base) !<;
        else
          Process(name :: args.toList, base) !<;
        state
    }
  }
}

object Grunt {
  var process: Option[Process] = None
}
