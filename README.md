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
  

## TODO

* No support for SBT 0.13.*
* The SBT build output within container's `STDOUT` doesn't get displayed on the terminal.
  * Assign name.
  * Print docker logs.
* Clean up container after run
  * With option to disable.


## Known issues

* `sbt console` doesn't work within the Docker container, i.e. no interactive terminal.


