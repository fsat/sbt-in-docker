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

import java.io.{ File, OutputStream }
import java.nio.file.{ Files, Path, Paths }

import org.scalatest.{ FunSpec, Inside, Matchers }
import SbtInDockerPlugin._
import au.fsat.sbt.indocker.out.{ NoOpOutputStream, StdoutWrapperOutputStream }

import scala.util.Success

class SbtInDockerPluginTest extends FunSpec with Matchers with Inside {
  describe("sbt-in-docker plugin") {
    describe("#runSbtInDocker") {
      it("stops existing container before running the provided commands") {
        withTempDir { tempDir =>
          val userHome = tempDir
          val projectDir = tempDir.resolve("my-project")

          Seq(
            projectDir,
            tempDir.resolve(".ivy2").resolve("cache"),
            tempDir.resolve(".ivy2").resolve("local"),
            tempDir.resolve(".sbt").resolve("preloaded")).foreach(Files.createDirectories(_))

          var commands: Seq[(Commands, OutputStream)] = Seq.empty
          var loggedCommands: Seq[String] = Seq.empty

          val exec: RunProcess = { (command, outputStream) =>
            commands :+= (command -> outputStream)
            Success(1)
          }

          val logCommands: LogCommands = loggedCommands :+= _

          SbtInDockerPlugin.runSbtInDocker(
            exec,
            userHome = userHome.toFile,
            projectDir = projectDir.toFile,
            dockerBaseImage = "my-base-image",
            containerName = "my-container",
            sbtArgs = Seq("test", "one", "two"),
            logCommands)

          inside(commands) {
            case Seq(deleteContainer, runContainer) =>

              val (deleteContainerCommand, deleteContainerOutputStream) = deleteContainer
              deleteContainerCommand shouldBe Seq("docker", "rm", "-f", "my-container")
              deleteContainerOutputStream shouldBe a[NoOpOutputStream]

              val (runContainerCommand, runContainerOutputstream) = runContainer
              runContainerCommand shouldBe Seq(
                "docker", "run",
                "-v", s"$projectDir:/opt/source:rw",
                "-v", s"$userHome/.ivy2/cache:/root/.ivy2/cache:rw",
                "-v", s"$userHome/.ivy2/local:/root/.ivy2/local:rw",
                "-v", s"$userHome/.sbt/preloaded:/root/.sbt/preloaded:ro",
                "--name", "my-container",
                "my-base-image",
                "bash", "-c", "cd /opt/source && sbt test one two")
              runContainerOutputstream shouldBe a[StdoutWrapperOutputStream]
          }

          loggedCommands shouldBe Seq(
            s"""docker run -v $projectDir:/opt/source:rw -v $userHome/.ivy2/cache:/root/.ivy2/cache:rw -v $userHome/.ivy2/local:/root/.ivy2/local:rw -v $userHome/.sbt/preloaded:/root/.sbt/preloaded:ro --name my-container my-base-image bash -c "cd /opt/source && sbt test one two"""")
        }
      }
    }
  }

  private def withTempDir[T](f: Path => T): T = {
    val tempDir = Files.createTempDirectory("sbt-in-docker")
    try {
      f(tempDir)
    } finally {
      tempDir.toFile.delete()
    }
  }
}
