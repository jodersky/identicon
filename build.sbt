// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

version in ThisBuild := {
  import sys.process._
  ("git describe --always --dirty=-SNAPSHOT --match v[0-9].*" !!).tail.trim
}

lazy val identicon = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(
    scalacOptions in Compile += "-deprecation",
    scalaVersion := crossScalaVersions.value.head
  )
  .jvmSettings(
    crossScalaVersions := "2.13.0-M5" :: "2.12.7" :: "2.11.12" :: Nil
  )
  .jsSettings(
    crossScalaVersions := "2.12.6" :: "2.11.12" :: Nil
  )
  .nativeSettings(
    crossScalaVersions := "2.11.12" :: Nil
  )
