name := """corl"""

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.1",
  "io.netty" % "netty" % "3.10.5.Final",
  "com.github.lolboxen" % "scala-json-traverse" % "2.0.3",
  "com.github.AzureAD" % "azure-activedirectory-library-for-java" % "1.1.2",
  "com.ning" % "async-http-client" % "1.9.35",
  "com.typesafe" % "config" % "1.2.1",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

lazy val corl = project
  .in(file("."))
  .settings(mainClass in Compile := Some("com.bhivelab.corl.Main"))
  .enablePlugins(JavaAppPackaging)
