import sbt._

lazy val pluginVersion = {
  def pluginVersionFromFile: Option[String] =
    Option(Path("").asFile.getAbsoluteFile / "target" / "plugin-version")
      .filter(_.exists())
      .map(IO.read(_).trim)

  def pluginVersionFromSysProps: Option[String] =
    sys.props.get("plugin.version")

  pluginVersionFromSysProps.orElse(pluginVersionFromFile)
}

pluginVersion match {
  case Some(x) => addSbtPlugin("au.fsat" % "sbt-in-docker" % x)
  case _ => sys.error("Unable to detect plugin version")
}

