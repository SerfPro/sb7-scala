//// Some useful definitions ////

lazy val osName =
  System.getProperty("os.name").split(" ")(0).toLowerCase()

lazy val startOnFirst =
  if (osName == "mac")
    Some("-XstartOnFirstThread")
  else
    None

val lwjglVersion = "3.1.2"


//// Setting up native library extraction ////

ivyConfigurations += config("natives")

lazy val nativeExtractions = SettingKey[Seq[(String, NameFilter, File)]](
  "native-extractions",
  "(jar name partial, sbt.NameFilter of files to extract, destination directory)"
)

lazy val extractNatives = TaskKey[Unit](
  "extract-natives",
  "Extracts native files"
)

lazy val nativesFilter = new NameFilter {
  override def accept(name: String): Boolean =
    !(name.endsWith(".git") || name.endsWith(".sha1"))
}

//// Project Configuration ////

name := """scala-lwjgl"""

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-unused-import",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-encoding", "UTF-8",
  "-target:jvm-1.8"
)

javacOptions ++= Seq(
  "-Xlint",
  "-encoding", "UTF-8",
  "-source", "1.8",
  "-target", "1.8"
)

testOptions += Tests.Argument("-oD")

javaOptions ++= List(
  s"-Djava.library.path=${baseDirectory.value}/lib/$osName"
) ::: startOnFirst.toList

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",

  "org.lwjgl" % "lwjgl"          % lwjglVersion,
  "org.lwjgl" % "lwjgl-glfw"     % lwjglVersion,
  "org.lwjgl" % "lwjgl-jemalloc" % lwjglVersion,
  "org.lwjgl" % "lwjgl-openal"   % lwjglVersion,
  "org.lwjgl" % "lwjgl-opengl"   % lwjglVersion,
  "org.lwjgl" % "lwjgl-stb"      % lwjglVersion,

  "org.lwjgl" % "lwjgl"          % lwjglVersion % "natives" classifier "natives-windows" classifier "natives-linux" classifier "natives-macos",
  "org.lwjgl" % "lwjgl-glfw"     % lwjglVersion % "natives" classifier "natives-windows" classifier "natives-linux" classifier "natives-macos",
  "org.lwjgl" % "lwjgl-jemalloc" % lwjglVersion % "natives" classifier "natives-windows" classifier "natives-linux" classifier "natives-macos",
  "org.lwjgl" % "lwjgl-openal"   % lwjglVersion % "natives" classifier "natives-windows" classifier "natives-linux" classifier "natives-macos",
  "org.lwjgl" % "lwjgl-opengl"   % lwjglVersion % "natives" classifier "natives-windows" classifier "natives-linux" classifier "natives-macos",
  "org.lwjgl" % "lwjgl-stb"      % lwjglVersion % "natives" classifier "natives-windows" classifier "natives-linux" classifier "natives-macos"
)

extractNatives <<= (baseDirectory, update) map { (base, up) =>
  val jars = up.select(configurationFilter("natives"))

  jars foreach { jar =>
    val o_os = jar.getName match {
      case s if s.contains("natives-macos")   => Some("mac")
      case s if s.contains("natives-windows") => Some("windows")
      case s if s.contains("natives-linux")   => Some("linux")
      case _ => None
    }

    o_os foreach { os =>
      IO.unzip(jar, base / s"lib/$os", nativesFilter)
    }
  }
}

compile in Compile <<= (compile in Compile) dependsOn extractNatives

fork in run := true

cancelable := true

exportJars := true
