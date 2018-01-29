import scala.sys.process._

name := "centos7"
scalaVersion := "2.12.4"

lazy val unameFile = settingKey[File]("uname-file")

unameFile := target.value / "uname-output"

TaskKey[Unit]("getOs") := {
  val osName = "cat /etc/redhat-release".!!
  IO.write(unameFile.value, osName)
}

TaskKey[Unit]("check") := {
  val result = IO.read(unameFile.value).trim
  if (result != "CentOS Linux release 7.4.1708 (Core)")
    sys.error(s"Unexpected OS [$result]")
}

