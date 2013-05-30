play-yeoman
===========

**play-yeoman** is a sbt+play plugin that brings the streamlined frontend development workflow and optimized build system of [yeoman](http://yeoman.io) to [Play 2.0](http://playframework.org).

In this approach, you would use play for developing the application backend/services and develop the frontend/ui using the yeoman toolchain, all in a totally integrated workflow and single unified console.

Play + Yeoman integration sbt and play plugins. Inspired and copied to quite some extent from,
https://github.com/leon/play-grunt-angular-prototype

Support
=======

If you face any issues using this plugin, please feel free to report to <opensource [at] tuplejump [dot] com> or shout out at twitter mentioning @tuplejump or @milliondreams.
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

2) Add the yeoman sbt plugin to the project. Edit project/plugins.sbt to add the following line,

```
addSbtPlugin("com.tuplejump" % "sbt-yeoman" % "0.5.1")

```

3) Import Yeoman classes in the project build adding the following import to project/Build.scala,

```

import com.tuplejump.sbt.yeoman.Yeoman

```

4) In the same file, add the yeoman settings to your Play project like this,

```
  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    Yeoman.yeomanSettings : _*
  )

```

5) Add yeoman routes to the project, appending the following line in conf/routes files,

```

GET     /ui                         com.tuplejump.playYeoman.Yeoman.index

->	    /ui/                    yeoman.Routes


```

Optionally, you can also redirect your root url,

```

GET     /                           com.tuplejump.playYeoman.Yeoman.redirectRoot(base="/ui/")

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

10) Run your play application,

```
[yo-demo] run

```

11) Click on the liveReload plugin in the browser to connect and navigate to http://localhost:9000/ui/

### Taking it to production

The process to put your app in production remains the same as you do today with any play application, except for one minor change. We need to run the yeoman(grunt) build before staging/packaging the app.

So if you deploy an staged app, instead of `sbt stage`, run `sbt grunt stage`.
In case you build a packaged dist, instead of `sbt dist`, run `sbt grunt dist`.


Licence
=======

This software is licensed under the Apache 2 license, quoted below.

Copyright 2013 Tuplejump Software Pvt. Ltd. (http://www.tuplejump.com).

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
