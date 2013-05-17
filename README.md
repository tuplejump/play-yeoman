play-yeoman
===========

Play + Yeoman integration sbt and play plugins. Inspired and copied to quite some extent from,
https://github.com/leon/play-grunt-angular-prototype

How to use it?
==============

play-yeoman provides 2 plugins one for SBT and the other for play. The SBT plugin provides the necessary settings and also adds the play plugin to the project's dependencies.

The play plugin in turn provides some routes to serve the angualr web app. This plugin assumes you have npm, yeoman, grunt-cli and bower installed.

To start using this plugin,

1. Add the plugin to your play project by adding these lines to  project/plugins.sbt,

```

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"


addSbtPlugin("com.tuplejump" % "sbt-yeoman" % "0.5.0-SNAPSHOT")

```

2. Add the yeoman project settings to your play project

```
  import com.tuplejump.sbt.yeoman._

  //Existing code...

  val appSettings = Yeoman.yeomanSettings ++ Seq(resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/")


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    appSettings: _*
  )
 

```

3. Create a folder called ui in the project route

4. Start sbt

5. Now you, use npm, yo, grunt and bower commands from the SBT console. They will always run the ui folder.

6. Run the following line to create your angular frontend,


```

yo angular

```

Licence
=======

This software is licensed under the Apache 2 license, quoted below.

Copyright 2013 Tuplejump Software Pvt. Ltd. (http://www.tuplejump.com).

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
