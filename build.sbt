import com.typesafe.sbt.SbtScalariform.ScalariformKeys

import scalariform.formatter.preferences._

// sbt-docker
import com.typesafe.sbt.packager.docker._

// Resolvers
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("websudos", "oss-releases"),
  "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases",
  Resolver.bintrayRepo("hseeberger", "maven")
)


// Dependencies
val compilerPlugins = Seq(
  compilerPlugin("org.spire-math"  %% "kind-projector"         % "0.7.1")
)

val rootDependencies = Seq(
  "de.heikoseeberger"              %% "akka-http-circe"        % "1.10.0",
  "com.softwaremill.reactivekafka" %% "reactive-kafka-core"    % "0.10.0",
  "com.typesafe.akka"              %% "akka-http-experimental" % "2.4.4",
  "io.circe"                       %% "circe-core"             % "0.4.1",
  "io.circe"                       %% "circe-generic"          % "0.4.1",
  "io.circe"                       %% "circe-parser"           % "0.4.1",
  "com.websudos"                   %% "phantom-dsl"            % "1.25.4"
)

val testDependencies = Seq (
  "com.typesafe.akka"              %% "akka-http-testkit"      % "2.4.4"  % "test",
  "org.scalatest"                  %% "scalatest"              % "2.2.6"  % "test",
  "org.specs2"                     %% "specs2-core"            % "3.8"    % "test",
  "org.specs2"                     %% "specs2-scalacheck"      % "3.8"    % "test",
  "org.specs2"                     %% "specs2-mock"            % "3.8"    % "test"
)

val dependencies =
  compilerPlugins ++
    rootDependencies ++
    testDependencies

// Settings
//
val compileSettings = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:_",
  "-target:jvm-1.8",
  "-unchecked",
  "-Ybackend:GenBCode",
  "-Ydelambdafy:method",
  "-Xfuture",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused-import",
  "-Ywarn-value-discard"
)

val dockerSettings = Seq(
  defaultLinuxInstallLocation in Docker := "/opt/microservice",
  dockerCommands := Seq(
    Cmd("FROM", "alpine:3.3"),
    Cmd("RUN apk upgrade --update && apk add --update openjdk8-jre"),
    Cmd("ADD", "opt /opt"),
    ExecCmd("RUN", "mkdir", "-p", "/var/log/microservice"),
    ExecCmd("ENTRYPOINT", "/opt/app/bin/microservice")
  ),
  version in Docker := version.value
)

val forkedJvmOption = Seq(
  "-server",
  "-Dfile.encoding=UTF8",
  "-Duser.timezone=GMT",
  "-Xss1m",
  "-Xms2048m",
  "-Xmx2048m",
  "-XX:+CMSClassUnloadingEnabled",
  "-XX:ReservedCodeCacheSize=256m",
  "-XX:+DoEscapeAnalysis",
  "-XX:+UseConcMarkSweepGC",
  "-XX:+UseParNewGC",
  "-XX:+UseCodeCacheFlushing",
  "-XX:+UseCompressedOops"
)

val pluginsSettings =
  dockerSettings ++
  scalariformSettings

val settings = Seq(
  name := "Microservice Blueprint",
  organization := "com.alaphi.app",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.8",
  libraryDependencies ++= dependencies,
  fork in run := true,
  fork in Test := true,
  fork in testOnly := true,
  connectInput in run := true,
  javaOptions in run ++= forkedJvmOption,
  javaOptions in Test ++= forkedJvmOption,
  scalacOptions := compileSettings,
  mainClass in (Compile, run) := Option("com.alaphi.app.microservice.Boot"),
  ScalariformKeys.preferences := PreferencesImporterExporter.loadPreferences((file(".") / "formatter.preferences").getPath)
)

val main =
  project
    .in(file("."))
    .settings(
      pluginsSettings ++ settings:_*
    )
    .enablePlugins(AshScriptPlugin)
