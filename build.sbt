name := """spike_04"""

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  // Uncomment to use Akka
  //"com.typesafe.akka" % "akka-actor_2.11" % "2.3.6",
  "junit"             % "junit"           % "4.11"  % "test",
  "com.novocode"      % "junit-interface" % "0.10"  % "test"
)

libraryDependencies += "com.netflix.rxjava" % "rxjava-core" % "0.20.7"

libraryDependencies += "org.apache.poi" % "poi" % "3.11"

libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.11"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.3.6"

