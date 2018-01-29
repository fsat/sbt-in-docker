//sys.props.get("plugin.version") match {
//  case Some(x) => addSbtPlugin("au.fsat" % "sbt-in-docker" % x)
//  case _ => sys.error("""|The system property 'plugin.version' is not defined.
//                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
//}

addSbtPlugin("au.fsat" % "sbt-in-docker" % "0.1.0-SNAPSHOT")
