/*
 * Copyright 2018 Felix Satyaputra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.fsat.sbt.indocker

import sbt._
import scala.sys.process._

object SbtInDockerPlugin {

  /**
   * Runs SBT target within Docker image specified by `dockerBaseImage`.
   *
   * @param projectDir base directory of the current SBT project.
   * @param dockerBaseImage the base image to run the SBT task with.
   * @param sbtArgs the input arguments to the SBT command to be run within the Docker container.
   * @param log the SBT logger.
   */
  def runSbtInDocker(projectDir: File, dockerBaseImage: String, sbtArgs: Seq[String], log: Logger): Unit = {
    def mount(input: (File, String), mode: String): Seq[String] = {
      val (from, to) = input
      if (from.exists())
        Seq("-v", s"${from.absolutePath}:$to:$mode")
      else
        Seq.empty[String]
    }

    val dockerCommands =
      Seq("docker", "run") ++
        mount(projectDir.getAbsoluteFile -> "/opt/source", "rw") ++
        mount(Path.userHome / ".ivy2" / "cache" -> "/root/.ivy2/cache", "rw") ++
        mount(Path.userHome / ".sbt" / "preloaded" -> "/root/.sbt/preloaded", "ro") ++
        Seq(
          dockerBaseImage,
          "bash", "-c", s"cd /opt/source && sbt ${sbtArgs.mkString(" ")}")

    log.info(dockerCommands.mkString(" "))
    dockerCommands.!!(log)
  }
}
