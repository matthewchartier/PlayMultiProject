import sbt._
import Keys._
import play.PlayImport._

object ApplicationBuild extends Build {

  val branch = "git rev-parse --abbrev-ref HEAD".!!.trim
  val commit = "git rev-parse --short HEAD".!!.trim
  val buildTime = (new java.text.SimpleDateFormat("yyyyMMdd-HHmmss")).format(new java.util.Date())
//  val appVersion = "%s-%s-%s".format(branch, commit, buildTime)
  val appVersion = "1.0.0"


  val commonDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )
  val serviceADependencies = Seq() // You can have service specific dependencies
  val serviceBDependencies = Seq()

  val scalaBuildOptions = Seq("-unchecked", "-deprecation", "-feature", "-language:reflectiveCalls",
    "-language:implicitConversions", "-language:postfixOps", "-language:dynamics","-language:higherKinds",
    "-language:existentials", "-language:experimental.macros", "-Xmax-classfile-name", "140")


  val common = Project("common", file("modules/common")).enablePlugins(play.PlayScala).settings(
    // Add common settings here
    version := appVersion,
    scalaVersion := "2.11.7",
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions += "-Dconfig.resource=common-application.conf"
  )

  val serviceA = Project("serviceA", file("modules/serviceA")).enablePlugins(play.PlayScala).settings(
    // Add serviceA settings here
    version := appVersion,
    scalaVersion := "2.11.7",
    libraryDependencies ++= commonDependencies,
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions += "-Dconfig.resource=serviceA-application.conf"
  ).dependsOn(common % "test->test;compile->compile").aggregate(common)

  val serviceB = Project("serviceB", file("modules/serviceB")).enablePlugins(play.PlayScala).settings(
    // Add serviceB settings here
    version := appVersion,
    scalaVersion := "2.11.7",
    libraryDependencies ++= commonDependencies,
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions += "-Dconfig.resource=serviceB-application.conf"
  ).dependsOn(common % "test->test;compile->compile").aggregate(common)


  // The default SBT project is based on the first project alphabetically. To force sbt to use this one,
  // we prefit it with 'aaa'
  val aaaMultiProject = Project("multiproject", file(".")).enablePlugins(play.PlayScala).settings(
    version := appVersion,
    scalaVersion := "2.11.7",
    libraryDependencies ++= commonDependencies,
    // This project runs both services together, which is mostly useful in development mode.
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions += "-Dconfig.resource=application.conf"
  ).dependsOn(common % "test->test;compile->compile", serviceA % "test->test;compile->compile", serviceB % "test->test;compile->compile").aggregate(common, serviceA, serviceB)

}
