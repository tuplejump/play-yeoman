// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += Resolver.file("Local Play Repository", file("/home/rohit/work/personal/Play20/repository/local"))( Resolver.ivyStylePatterns)

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2-SNAPSHOT")

