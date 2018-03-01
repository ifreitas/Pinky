name := """Pinky"""
organization := "ifreitas"

version := "0.1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.0"

EclipseKeys.preTasks := Seq(compile in Compile, compile in Test)

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test;

val akkaVersion = "2.5.11"
dependencyOverrides += "com.google.guava"       %  "guava"              % "22.0";
dependencyOverrides += "com.typesafe.akka"      %% "akka-actor"         % akkaVersion;
dependencyOverrides += "com.typesafe.akka"      %% "akka-stream"        % akkaVersion;
