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

  val forceGrunt = SettingKey[Boolean]("key to enable/disable grunt tasks with force option")

  val gruntDev  = TaskKey[Unit]("Task to run grunt build-dev")
  val gruntDist = TaskKey[Unit]("Task to run grunt build-dist")
  val gruntClean = TaskKey[Unit]("Task to run grunt clean")


  val yeomanSettings: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq("com.tuplejump" %% "play-yeoman" % "0.8.4-SNAPSHOT" intransitive()),

    // Where does the UI live?
    yeomanDirectory <<= (baseDirectory in Compile) {
      _ / "ui"
    },

    yeomanGruntfile := "Gruntfile.js",

    forceGrunt := true,
    grunt := {
      val base = (yeomanDirectory in Compile).value
      val gruntFile = (yeomanGruntfile in Compile).value
      val isForceEnabled = (forceGrunt in Compile).value
      runGrunt1(base, gruntFile, Def.spaceDelimited("<arg>").parsed.toList, isForceEnabled)
    },

    gruntDev := {
      val base = (yeomanDirectory in Compile).value
      val gruntFile = (yeomanGruntfile in Compile).value
      runGrunt1(base, gruntFile, List("build-dev"))
    },

    gruntDist := {
      val base = (yeomanDirectory in Compile).value
      val gruntFile = (yeomanGruntfile in Compile).value
      runGrunt1(base, gruntFile, List("build-dist"))
    },

    gruntClean := {
      val base = (yeomanDirectory in Compile).value
      val gruntFile = (yeomanGruntfile in Compile).value
      runGrunt1(base, gruntFile, List("clean"))
    },

    dist <<= dist dependsOn (gruntDist),

    stage <<= stage dependsOn (gruntDist),

    clean <<= clean dependsOn (gruntClean),

    // Add the views to the dist
    unmanagedResourceDirectories in Assets <+= (yeomanDirectory in Compile)(base => base / "dist"),

    playRunHooks <+= (yeomanDirectory, yeomanGruntfile, forceGrunt).map {
      (base, gruntFile, isForceEnabled) => Grunt(base, gruntFile, isForceEnabled)
    },

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
    sourceDirectories in TwirlKeys.compileTemplates in Compile ++= Seq(Yeoman.yeomanDirectory.value / "dist"),
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
      },
    // I'm unsure, but think that it is needed:
    playMonitoredFiles <<= playMonitoredFiles dependsOn gruntDev
  )

  private def runGrunt(base: sbt.File, gruntFile: String,
                       args: List[String] = List.empty,
                       isForceEnabled: Boolean = true): Option[Process] = {
    //println(s"Will run: grunt --gruntfile=$gruntFile $args in ${base.getPath}")

    val arguments = if (isForceEnabled) {
      args ++ List("--force")
    } else args

    if (isForceEnabled)
      println("'force' enabled")
    else
      println("'force' not enabled")


    if (System.getProperty("os.name").startsWith("Windows")) {
      val process: ProcessBuilder = Process("cmd" :: "/c" :: "grunt" :: "--gruntfile=" + gruntFile :: arguments, base)
      println(s"Will run: ${process.toString} in ${base.getPath}")
      Option(process.run)
    } else {
      val process: ProcessBuilder = Process("grunt" :: "--gruntfile=" + gruntFile :: arguments, base)
      println(s"Will run: ${process.toString} in ${base.getPath}")
      Option(process.run)
    }
  }

  /**
   * Wrapper for runGrunt() to reduce code duplication.
   * @param args cmd line arguments.
   * @param isForceEnabled grunt force flag.
   * @return Process return code.
   */
  private def runGrunt1(base: File, gruntFile: String, args: List[String] = List.empty,  isForceEnabled: Boolean = true): Int = {
    runGrunt(base, gruntFile, args, isForceEnabled)
      .get
      .exitValue()
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
    def apply(base: File, gruntFile: String, isForceEnabled: Boolean): PlayRunHook = {

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


