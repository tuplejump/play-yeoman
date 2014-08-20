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
import java.net.InetSocketAddress
import scala.Some
import play.Play.autoImport._
import PlayKeys._
import com.typesafe.sbt.web.Import._

import com.typesafe.sbt.packager.universal.Keys._
import play.PlayRunHook
import play.twirl.sbt.Import._

object Yeoman extends Plugin {
  val yeomanDirectory = SettingKey[File]("yeoman-directory")
  val yeomanGruntfile = SettingKey[String]("yeoman-gruntfile")
  val yeomanExcludes = sbt.SettingKey[Seq[String]]("yeoman-excludes")

  val grunt = inputKey[Unit]("Task to run grunt")

  private val gruntDist = TaskKey[Unit]("Task to run dist grunt")

  private val gruntClean = TaskKey[Unit]("Task to run grunt clean")


  val yeomanSettings: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq("com.tuplejump" %% "play-yeoman" % "0.7.1-SNAPSHOT" intransitive()),

    // Turn off play's internal less compiler
    lessEntryPoints := Nil,

    // Turn off play's internal javascript compiler
    javascriptEntryPoints := Nil,

    // Where does the UI live?
    yeomanDirectory <<= (baseDirectory in Compile) {
      _ / "ui"
    },

    yeomanGruntfile := "Gruntfile.js",

    grunt := {
      val base = (yeomanDirectory in Compile).value
      val gruntFile = (yeomanGruntfile in Compile).value
      //stringToProcess("grunt " + (Def.spaceDelimited("<arg>").parsed).mkString(" ")).!!,
      runGrunt(base, gruntFile, Def.spaceDelimited("<arg>").parsed.toList).get.exitValue()
    },

    gruntDist := {
      val base = (yeomanDirectory in Compile).value
      val gruntFile = (yeomanGruntfile in Compile).value
      //stringToProcess("grunt " + (Def.spaceDelimited("<arg>").parsed).mkString(" ")).!!,
      runGrunt(base, gruntFile, List("build-dist")).get.exitValue()
    },

    gruntClean := {
      val base = (yeomanDirectory in Compile).value
      val gruntFile = (yeomanGruntfile in Compile).value
      //stringToProcess("grunt " + (Def.spaceDelimited("<arg>").parsed).mkString(" ")).!!,
      runGrunt(base, gruntFile, List("clean")).get.exitValue()
    },

    dist <<= dist dependsOn (gruntDist),

    stage <<= stage dependsOn (gruntDist),

    clean <<= clean dependsOn (gruntClean),

    // Add the views to the dist
    unmanagedResourceDirectories in Assets <+= (yeomanDirectory in Compile)(base => base / "dist"),

    playRunHooks <+= (yeomanDirectory, yeomanGruntfile).map { (base, gruntFile) => Grunt(base, gruntFile)},

    // Allow all the specified commands below to be run within sbt
    commands <++= yeomanDirectory {
      base =>
        Seq(
          //"grunt",
          "bower",
          "yo",
          "npm"
        ).map(cmd(_, base))
    }
  )


  val withTemplates = Seq(
    sourceDirectories in TwirlKeys.compileTemplates in Compile ++= Seq(Yeoman.yeomanDirectory.value / "twirl"),
    yeomanExcludes <<= (yeomanDirectory)(yd => Seq(
      yd + "/dist/components/",
      yd + "/dist/images/",
      yd + "/dist/styles/"
    )),
    excludeFilter in unmanagedSources <<=
      (excludeFilter in unmanagedSources, yeomanExcludes) {
        (currentFilter: FileFilter, ye) =>
          currentFilter || new FileFilter {
            def accept(pathname: File): Boolean = {
              (true /: ye.map(s => pathname.getAbsolutePath.startsWith(s)))(_ && _)
            }
          }
      }
  )


  private def runGrunt(base: sbt.File, gruntFile: String, args: List[String]) = {
    //println(s"Will run: grunt --gruntfile=$gruntFile $args in ${base.getPath}")
    if (System.getProperty("os.name").startsWith("Windows")) {
      val process: ProcessBuilder = Process("cmd" :: "/c" :: "grunt" :: "--gruntfile=" + gruntFile :: args, base)
      println(s"Will run: ${process.toString} in ${base.getPath}")
      Some(process.run)
    }
    else {
      val process: ProcessBuilder = Process("grunt" :: "--gruntfile=" + gruntFile :: args, base)
      println(s"Will run: ${process.toString} in ${base.getPath}")
      Some(process.run)
    }
  }

  import scala.language.postfixOps
  private def cmd(name: String, base: File): Command = {
    if (!base.exists()) (base.mkdirs())
    Command.args(name, "<" + name + "-command>") {
      (state, args) =>
        if (System.getProperty("os.name").startsWith("Windows")) {
          Process("cmd" :: "/c" :: name :: args.toList, base) !<
        } else {
          Process(name :: args.toList, base) !<
        }
        state
    }
  }

  object Grunt {
    def apply(base: File, gruntFile: String): PlayRunHook = {

      object GruntProcess extends PlayRunHook {
        var process: Option[Process] = None

        override def beforeStarted(): Unit = {
          runGrunt(base, gruntFile, "build-dev" :: Nil)
        }

        override def afterStarted(addr: InetSocketAddress): Unit = {
          process = runGrunt(base, gruntFile, "watch" :: Nil)
        }

        override def afterStopped(): Unit = {
          process.map(p => p.destroy())
          process = None
        }
      }

      GruntProcess
    }
  }

}


