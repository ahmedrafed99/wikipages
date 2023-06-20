This / scalaVersion := "2.12.15"
libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "com.typesafe.play" %% "play-json" % "2.9.3",
  "com.github.scopt" %% "scopt" % "4.1.0"
)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test"
