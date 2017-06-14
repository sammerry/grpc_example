logLevel := Level.Warn

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M7")

addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.4.1")

addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.15.0")

resolvers += "Era7 maven releases" at "https://s3-eu-west-1.amazonaws.com/releases.era7.com"

addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.3")
libraryDependencies += "com.trueaccord.scalapb" %% "compilerplugin" % "0.6.0-pre4"

