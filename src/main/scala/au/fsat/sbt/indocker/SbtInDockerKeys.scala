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

trait SbtInDockerKeys {
  /**
   * Accepts the SBT input argument to be run under Centos 7 docker image.
   */
  lazy val centos7 = inputKey[Unit]("centos-7")

  /**
   * The name of the Centos 7 image to run the SBT task with.
   */
  lazy val centos7BaseImage = SettingKey[String]("centos7-base-image", "The name of the Centos 7 image to run the SBT task with.")

  /**
   * Accepts the SBT input argument to be run under Ubuntu Xenial docker image.
   */
  lazy val xenial = inputKey[Unit]("xenial")

  /**
   * The name of the Ubuntu Xenial image to run the SBT task with.
   */
  lazy val xenialBaseImage = SettingKey[String]("xenial-base-image", "The name of the Ubuntu Xenial image to run the SBT task with.")
}
