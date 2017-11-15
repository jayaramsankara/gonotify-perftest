name := "gonotify-perftest"

version := "1.0"

scalaVersion := "2.11.5"

enablePlugins(GatlingPlugin)

libraryDependencies ++= Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.5" % "test",
  "io.gatling"            % "gatling-test-framework"    % "2.1.5" ,
  "io.gatling"            % "gatling-bundle"            % "2.1.5"  artifacts (Artifact("gatling-bundle", "zip", "zip", "bundle"))
)

