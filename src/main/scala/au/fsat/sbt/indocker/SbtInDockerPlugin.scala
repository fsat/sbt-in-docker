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

import au.fsat.sbt.indocker.out.NoOpOutputStream
import sbt._
import sbt.Keys._
import sbt.internal.util.complete.Parsers.spaceDelimited

import scala.sys.process._
import scala.util.Try

object SbtInDockerPlugin extends AutoPlugin {
  object autoImport extends SbtInDockerKeys

  import autoImport._

  override def trigger = allRequirements

  private val InDocker = config("in-docker")

  override lazy val projectSettings: Seq[Def.Setting[_]] =
    inConfig(InDocker)(Seq(
      centos7BaseImage in LocalRootProject := "fsat/centos-7-jdk-8-sbt:latest",
      centos7ContainerName in LocalRootProject := "sbt-in-docker-centos7",
      centos7 in LocalRootProject := {
        val args: Seq[String] = spaceDelimited("<arg>").parsed
        val log = streams.value.log

        runSbtInDocker(
          (centos7ContainerName in LocalRootProject).value,
          (baseDirectory in LocalRootProject).value,
          (centos7BaseImage in LocalRootProject).value,
          args, log)
      },

      xenialBaseImage in LocalRootProject := "fsat/xenial-jdk-8-sbt:latest",
      xenial7ContainerName in LocalRootProject := "sbt-in-docker-xenial",
      xenial in LocalRootProject := {
        val args: Seq[String] = spaceDelimited("<arg>").parsed
        val log = streams.value.log

        runSbtInDocker(
          (xenial7ContainerName in LocalRootProject).value,
          (baseDirectory in LocalRootProject).value,
          (xenialBaseImage in LocalRootProject).value,
          args, log)
      }))

  /**
   * Runs SBT target within Docker image specified by `dockerBaseImage`.
   *
   * @param projectDir base directory of the current SBT project.
   * @param dockerBaseImage the base image to run the SBT task with.
   * @param sbtArgs the input arguments to the SBT command to be run within the Docker container.
   * @param log the SBT logger.
   */
  private[indocker] def runSbtInDocker(containerName: String, projectDir: File, dockerBaseImage: String, sbtArgs: Seq[String], log: Logger): Unit = {
    def mount(input: (File, String), mode: String): Seq[String] = {
      val (from, to) = input
      if (from.exists())
        Seq("-v", s"${from.absolutePath}:$to:$mode")
      else
        Seq.empty[String]
    }

    Try(Seq("docker", "rm", "-f", containerName).#>(noOpOutputStream).!)

    val dockerCommands =
      Seq("docker", "run") ++
        mount(projectDir.getAbsoluteFile -> "/opt/source", "rw") ++
        mount(Path.userHome / ".ivy2" / "cache" -> "/root/.ivy2/cache", "rw") ++
        mount(Path.userHome / ".ivy2" / "local" -> "/root/.ivy2/local", "rw") ++
        mount(Path.userHome / ".sbt" / "preloaded" -> "/root/.sbt/preloaded", "ro") ++
        Seq(
          "--name", containerName,
          dockerBaseImage,
          "bash", "-c", s"cd /opt/source && sbt ${sbtArgs.mkString(" ")}")

    // This needs to be formatted like this so it can be copy-pasted into the console
    log.info(s"""${dockerCommands.dropRight(1).mkString(" ")} "${dockerCommands.last}"""")
    dockerCommands.!(log)
  }

  private def noOpOutputStream: NoOpOutputStream = new NoOpOutputStream()
}
