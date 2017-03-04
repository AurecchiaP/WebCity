name := """webcity"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)



scalaVersion := "2.11.8"

libraryDependencies += filters

libraryDependencies += "com.google.code.gson" % "gson" % "2.8.0"
libraryDependencies += "com.github.javaparser" % "javaparser-core" % "3.1.0"

