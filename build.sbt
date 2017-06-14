import com.trueaccord.scalapb.compiler.Version.scalapbVersion

enablePlugins(
  sbtdocker.DockerPlugin,
  JavaAppPackaging)

name := "grpc_examlpe"
version := "1.0"
scalaVersion := "2.12.2"

awsProfile := "default"
publishMavenStyle := false
s3overwrite := false

resolvers ++= Seq[Resolver](
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("public"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("cakesolutions", "maven"),
  Resolver.typesafeRepo("releases"))

libraryDependencies ++= {
  val akkaV       = "2.5.0"
  val akkaHttpV   = "10.0.5"
  val jacksonV    = "2.8.6"

  Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonV,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonV,
    "com.getsentry.raven"    % "raven-logback" % "8.0.3",

    "com.trueaccord.scalapb" %% "scalapb-runtime" % scalapbVersion % "protobuf",
    "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % scalapbVersion,
    "io.grpc" % "grpc-netty" % "1.4.0",

    "com.typesafe.akka"       %% "akka-http-testkit" % akkaHttpV % "test",
    "com.typesafe.akka"       %% "akka-testkit" % akkaV % "test",
    "org.scalatest"           %% "scalatest" % "3.0.1" % "test",
    "org.mockito"             % "mockito-core" % "1.8.5" % "test",
    "com.storm-enroute"       %% "scalameter" % "0.8.2" % "test"
  )
}

PB.protoSources.in(Compile) := Seq(
  sourceDirectory.in(Compile).value / "proto")

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value)
