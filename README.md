# WIP - Run OS dependent SBT tasks

## Goal

Sometimes you will need to run SBT task that depends on OS specific features, for example packaging your application as an RPM archive.

The [sbt-native-packager](https://github.com/sbt/sbt-native-packager) supports this, but it requires [rpmbuild](http://ftp.rpm.org/max-rpm/rpmbuild.8.html) command to be present. The `rpmbuild` command is only specific for RPM based system.

If you are developing on non-RPM system (i.e. Ubuntu or MacOS), this plugin will come in handy.

Simply execute your SBT tasks using this plugin, i.e.

```bash
$ sbt "in-docker:centos7 my-module/rpm:packageBin"
```

This will invoke `my-module`'s `rpm:packageBin` task within Docker image configured for Centos 7. The resulting RPM file will be present in the `my-module`'s `target` directory.

> Important: Note the use of double quotes to enclose the `in-docker/centos7` and the subsequent task. The double quotes is important as it allows the subsequent task to be presented as input argument to the plugin.

Similarly for Debian.

```bash
$ sbt "in-docker:xenial my-module/rpm:packageBin"
```

## Pre-requisite

* [Docker](https://www.docker.com/) installed and running.

## How it works

The plugin relies on [inputKey](http://www.scala-sbt.org/1.0/docs/Input-Tasks.html) task to accept the tasks to be run within the Docker container.

The plugin starts a Docker container with appropriate image, mounting the following directories into the container.

| Host Dir            | Container Dir          | Mode | Comment |
| ------------------- | ---------------------- | ---- | ------- |
| Current project Dir | `/opt/source`          | `rw` | Read and write mount is required to write the result of the task into the project directory |  
| `~/.ivy2/cache`     | `/root/.ivy2/cache`    | `rw` | Read and write mount is required to read the cached jars and to write the resolved jars |  
| `~/.ivy2/local`     | `/root/.ivy2/local`    | `rw` | Read and write mount is required to read the cached SBT plugins and to write the resolved plugins |  
| `~/.sbt/preloaded`  | `/root/.sbt/preloaded` | `r`  | Read only is required to read the cached SBT artifacts. |  
  

## Dependency: Docker images

This plugin depends on the Docker images placed in the `docker-images/` directory.

At the moment the following Docker images are present.

#### `centos-7-jdk-8-sbt`

This image is created to facilitate building RPM archives on non-RPM distro.

The `centos:7` image is used as base image with the following additional items installed:

* Open JDK 8
* SBT `1.0.4`


#### `xenial-jdk-8-sbt`

This image is created to facilitate building Debian archives on non Debian based distro.

The `ubuntu:xenial` image is used as base image with the following additional items installed:

* Open JDK 8
* SBT `1.0.4`

