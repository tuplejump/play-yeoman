play-yeoman
===========

[![Join the chat at https://gitter.im/tuplejump/play-yeoman](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/tuplejump/play-yeoman?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**play-yeoman** is a sbt+play plugin that brings the streamlined frontend development workflow and optimized build system of [yeoman](http://yeoman.io) to [Play 2.0](http://playframework.org).

In this approach, you would use play for developing the application backend/services and develop the frontend/ui using the yeoman toolchain, all in a totally integrated workflow and single unified console.

Play + Yeoman integration sbt and play plugins. Inspired and copied to quite some extent from,
https://github.com/leon/play-grunt-angular-prototype

Support
=======

If you face any issues using this plugin, please feel free to report to | opensource [at] tuplejump [dot] com | or shout out at twitter mentioning @tuplejump or @milliondreams.
You can also create a issue in the github issue tracker.

If you found a bug and fixed it, please do raise a pull request, all users will appreciate that.

If you want some new feature to be implemented, please mail us.


How to use it?
==============

### Prerequisites

* This assumes that you have [npm](https://npmjs.org/), [yo](http://yeoman.io), [grunt](http://gruntjs.com/) and [bower](http://bower.io/) installed.

* If you want to use [LiveReload](http://livereload.com/), you should install the [LiveReload plugin](http://feedback.livereload.com/knowledgebase/articles/86242-how-do-i-install-and-use-the-browser-extensions-)  for your browser.

### Let's get started,

1) Create a new play project or open an existing play project

2) Add the yeoman sbt plugin to the project. Edit `project/plugins.sbt` to add the following line,

```
addSbtPlugin("com.tuplejump" % "sbt-yeoman" % "play-compatible-version")

```
play-compatible-version = 0.7.1 for Play 2.3.0 and 0.6.4 for Play 2.2.x

Note: support for Scala 2.11 is available from version 0.7.1 


3) Import Yeoman classes in the project build adding the following import to `project/Build.scala`,

```scala

import com.tuplejump.sbt.yeoman.Yeoman

```

4) In the same file, add the yeoman settings to your Play project like this,

Using 0.6.4

```scala
  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    Yeoman.yeomanSettings : _*
  )

```


Using 0.7.1

```scala
  val appSettings = Seq(version := appVersion, libraryDependencies ++= appDependencies) ++
    Yeoman.yeomanSettings 

  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    // Add your own project settings here
    appSettings: _*
  )

```


Note: If you're using build.sbt instead of the full scala build, you need to place the 2 additions above into `build.sbt` as follows:

Using 0.6.4

```scala

import com.tuplejump.sbt.yeoman.Yeoman

name := "play-project"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)

play.Project.playJavaSettings ++ Yeoman.yeomanSettings

``` 

5) Add yeoman routes to the project, appending the following line in conf/routes files,

```

GET     /ui         com.tuplejump.playYeoman.Yeoman.index

->	    /ui/        yeoman.Routes


```

Optionally, you can also redirect your root url,

```

GET     /           com.tuplejump.playYeoman.Yeoman.redirectRoot(base="/ui/")

```

6) Start play/sbt in your project folder,

```
user yo-demo> sbt

```

7) Update the project to pull in the new dependencies,

```
[yo-demo] update

```

8) Generate the yeoman application

```
[yo-demo] yo angular

```

9) Edit the Gruntfile.js to disable the Yeoman "connect" server as we will be using play to serve our application, This can be done by commenting out the relevant line in the server task towards the end of the file,

```
  grunt.registerTask('server', [
    'clean:server',
    'coffee:dist',
    'compass:server',
    'livereload-start',
    //'connect:livereload',	//THIS LINE SHOULD BE COMMENTED or REMOVED
    'open',
    'watch'
  ]);

```

Note: If you are using Scala Templates support in play-yeoman 0.7.1, ensure that "htmlmin" task is not called during the "build" since it does not understand Scala templates. This can be done by updating the build task in Gruntfile.js,

```
    grunt.registerTask('build', [
        'clean:dist',
        'bower-install',
        'useminPrepare',
        'concurrent:dist',
        'autoprefixer',
        'concat',
        'ngmin',
        'copy:dist',
        'cdnify',
        'cssmin',
        'uglify',
        'rev',
        'usemin'/*,
        'htmlmin' */
    ]);
```

10) Run your play application,

```
[yo-demo] run

```

11) Click on the liveReload plugin in the browser to connect and navigate to http://localhost:9000/ui/


### Support for Scala Templates

To use Scala templates you have 2 options, 

1) The old way is to create your templates in app/views and create a route for,

```
    GET /ui/views/{view_name}.html      controllers.Application.{your_action_handler}
```

2) Begining, 0.6.3, play-yeoman supports compilation of views from the yeoman directory too. 

* All you have to do to enable it is add Yeoman.withTemplates settings to the app settings, so your play project will now look like this,

Using 0.6.4

```
  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    (Yeoman.yeomanSettings ++ Yeoman.withTemplates) : _*
  )

```

Using 0.7.1

```
   val appSettings = Seq(version := appVersion, libraryDependencies ++= appDependencies) ++
       Yeoman.yeomanSettings ++
       Yeoman.withTemplates
   
   val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
       // Add your own project settings here
      appSettings: _*
   )

```

* Once that is done play will compile the templates from yeoman directory too, and you can use them in your controllers. This helps you keep all your UI files together under the yeoman directory ('ui' by default)

* Look at the yo-demo project for details!

Note: In 0.7.1, play-yeoman supports compilation of views from the yeoman directory but cannot recompile them when they are modified with the server running. You will need to stop the server and start it again.

* If you use scala template support, you need to run grunt prior to compile else the template code will not be generated. This is not required if you execute run or stage directly since they have a dependency on grunt.  

### Taking it to production

From 0.6.3, play-yeoman updates Play's 'stage' and 'dist' tasks to depend on the grunt task. Thus you don't need any additional step putting this in production. when you run either `sbt dist` or `sbt stage` it will automatically run grunt as part of the build!


### Configuring the yeoman directory paths for Play

By default play-yeoman looks for assets in "/ui/dist" in production or "ui/app" and "ui/.tmp" in development mode. In some cases where you are not using these directories, for example you have your yeoman project in a directory other than "ui" or your dist is built at some other location, you may want to configure this.

For this purpose play-yeoman provides 2 settings that you can use in you play project's application.conf.

1) yeoman.distDir - This is a String that takes the location where yeoman/grunt build puts your web app distribution. This location will be used by play-yeoman when you are running in production i.e. after dist/stage.

2) yeoman.devDirs - This is a List of String that takes a list of locations where play-yeoman should look for files in development mode i.e. when run using sbt run.


Note: Starting from 0.7.1, it is possible to disable force option on execution of grunt tasks. This can be done by adding the following to the application build settings,

```
Yeoman.forceGrunt := false
```

Licence
=======

This software is licensed under the Apache 2 license, quoted below.

Copyright 2013-2014 Tuplejump, Inc (http://www.tuplejump.com).

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
