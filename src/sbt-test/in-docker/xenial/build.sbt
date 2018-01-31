import scala.sys.process._

name := "xenial"
scalaVersion := "2.12.4"

TaskKey[Unit]("savePluginVersionToFile") := {
  val pluginVersionFile = target.value / "plugin-version"
  IO.write(pluginVersionFile, version.value)
}

lazy val unameFile = settingKey[File]("uname-file")
unameFile := target.value / "uname-output"

TaskKey[Unit]("getOs") := {
  val osName = "cat /etc/lsb-release".!!
  IO.write(unameFile.value, osName)
}

TaskKey[Unit]("check") := {
  val result = IO.readLines(unameFile.value).map(_.trim).mkString("\n")
  if (result !=
    """DISTRIB_ID=Ubuntu
      |DISTRIB_RELEASE=16.04
      |DISTRIB_CODENAME=xenial
      |DISTRIB_DESCRIPTION="Ubuntu 16.04.3 LTS"""".stripMargin)
    sys.error(s"Unexpected OS [$result]")
}

