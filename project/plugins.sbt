// Comment to get more information during initialization
//
logLevel := Level.Warn

// Resolvers
//
resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

// Dependency graph
//
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

// Revolver
//
addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")

// Scalariform
//
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

// Update plugin
//
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.6")

// Native packager
//
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.0")
