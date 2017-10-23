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

import java.net.InetSocketAddress

import com.typesafe.sbt.packager.Keys._
import play.sbt.PlayImport.PlayKeys._
import play.sbt.PlayRunHook
import play.twirl.sbt.Import.TwirlKeys
import sbt.Keys._
import sbt._
import scala.sys.process._

object Yeoman extends AutoPlugin {

  object autoImport {
    lazy val yeomanDirectory = SettingKey[File]("yeoman-directory")
    lazy val yeomanGruntfile = SettingKey[String]("yeoman-gruntfile")
    lazy val yeomanExcludes = sbt.SettingKey[Seq[String]]("yeoman-excludes")
    lazy val forceGrunt = SettingKey[Boolean]("key to enable/disable grunt tasks with force option")
    lazy val grunt = inputKey[Unit]("Task to run grunt")

    lazy val runGruntInDist = SettingKey[Boolean]("key to enable/disable running grunt prior to dist/stage")
  }

  import autoImport._
  import com.typesafe.sbt.web.Import._

  def gruntDist = Def.task {
    if (runGruntInDist.value) {
      val result = runGrunt(yeomanDirectory.value, yeomanGruntfile.value,
        forceGrunt.value).exitValue()
      if (result == 0) {
        result
      } else throw new Exception("grunt failed")
    }
  }

  def gruntClean = Def.task {
    val result = runGrunt(yeomanDirectory.value, yeomanGruntfile.value,
      forceGrunt.value, List("clean")).exitValue()
    if (result == 0) {
      result
    } else throw new Exception("grunt failed")
  }

//  lazy val withTemplates = {
//    Seq(sourceDirectories in TwirlKeys.compileTemplates in Compile ++= Seq(yeomanDirectory.value / "dist"),
//      yeomanExcludes := Seq(
//        yeomanDirectory.value + "/dist/components/",
//        yeomanDirectory.value + "/dist/images/",
//        yeomanDirectory.value + "/dist/styles/"
//      ),
//      excludeFilter in unmanagedSources ~=
//        (currentFilter => {
//            currentFilter || new FileFilter {
//              def accept(pathname: File): Boolean = {
//                yeomanExcludes.isEmpty || yeomanExcludes.map(s => pathname.getAbsolutePath.startsWith(s)).reduceLeft(_ && _)
//              }
//            }
//        }),
//      (compile in Compile) dependsOn gruntDist
//    )
//  }

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] = super.projectSettings ++ Seq(
    libraryDependencies ++= Seq("com.tuplejump" %% "play-yeoman" % "0.9.1" intransitive()),

    // Where does the UI live?
    yeomanDirectory := (baseDirectory in Compile).value / "ui",

    yeomanGruntfile := "Gruntfile.js",

    forceGrunt := true,
    grunt := {
      val base = (yeomanDirectory in Compile).value
      val gruntFile = (yeomanGruntfile in Compile).value
      //stringToProcess("grunt " + (Def.spaceDelimited("<arg>").parsed).mkString(" ")).!!,
      val isForceEnabled = (forceGrunt in Compile).value
      runGrunt(base, gruntFile, isForceEnabled, Def.spaceDelimited("<arg>").parsed.toList).exitValue()
    },

    dist := (dist dependsOn gruntDist).value,

    stage := (stage dependsOn gruntDist).value,

    clean := (clean dependsOn gruntClean).value,

    // Add the views to the dist
    unmanagedResourceDirectories in Assets += (yeomanDirectory in Compile).value / "dist",

    playRunHooks += Grunt(yeomanDirectory.value, yeomanGruntfile.value, forceGrunt.value),

    // Allow all the specified commands below to be run within sbt
    commands ++=  Seq("bower", "yo", "npm").map(v => cmd(v, yeomanDirectory.value)),
    runGruntInDist := true
  )

  private def runGrunt(base: sbt.File,
                       gruntFile: String,
                       isForceEnabled: Boolean = true,
                       args: List[String] = List.empty): Process = {
    //println(s"Will run: grunt --gruntfile=$gruntFile $args in ${base.getPath}")

    val arguments = if (isForceEnabled) {
      args ++ List("--force")
    } else args

    if (isForceEnabled)
      println("'force' enabled")
    else
      println("'force' not enabled")


    val process = if (System.getProperty("os.name").startsWith("Windows")) {
      Process("cmd" :: "/c" :: "grunt" :: "--gruntfile=" + gruntFile :: arguments, base)
    } else {
      Process("grunt" :: "--gruntfile=" + gruntFile :: arguments, base)
    }
    println(s"Will run: ${process.toString} in ${base.getPath}")
    process.run()
  }

  import scala.language.postfixOps

  private def cmd(name: String, base: File): Command = {
    if (!base.exists()) {
      base.mkdirs()
    }
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

        override def afterStarted(addr: InetSocketAddress): Unit = {
          process = Some(runGrunt(base, gruntFile, isForceEnabled, "watch" :: Nil))
        }

        override def afterStopped(): Unit = {
          process.foreach(p => p.destroy())
          process = None
        }
      }

      GruntProcess
    }
  }

}
