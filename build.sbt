name := "Scopus"
organization := "co.horn"
version := "0.5.5.3"
scalaVersion := "2.13.6"
crossScalaVersions := Seq("2.12.15", "2.13.6")
scalacOptions ++= Seq(
  "-deprecation"
)
fork in Test := true

organizationName := "8eo, Inc."
startYear := Some(2020)
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("https://github.com/8eo/scopus"))

libraryDependencies ++= List(
  "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.9" % "test",
  "org.scalatest" %% "scalatest-funspec"        % "3.2.9" % "test"
)

publishMavenStyle := true

// publishTo := Some("Github packages" at "https://maven.pkg.github.com/8eo/scopus")

pomIncludeRepository := { _ =>
  false
}

// pomExtra :=
//   <scm>
//       <url>git@github.com:8eo/scopus.git</url>
//       <connection>scm:git:git@github.com:8eo/scopus.git</connection>
//     </scm>
//     <developers>
//       <developer>
//         <id>davidmweber</id>
//         <name>David Weber</name>
//         <url>https://github.com/davidmweber</url>
//       </developer>
//       <developer>
//         <id>8eo</id>
//         <name>8eo, Inc.</name>
//         <url>https://github.com/8eo</url>
//       </developer>
//     </developers>
