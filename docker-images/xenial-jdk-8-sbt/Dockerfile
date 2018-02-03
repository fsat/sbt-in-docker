FROM ubuntu:xenial

RUN apt-get update
RUN apt-get install -y openjdk-8-jdk
RUN apt-get install -y curl

RUN curl -L -o /tmp/sbt-1.0.4.tgz https://github.com/sbt/sbt/releases/download/v1.0.4/sbt-1.0.4.tgz
RUN tar zxvf /tmp/sbt-1.0.4.tgz -C /opt
RUN echo "export PATH=/opt/sbt/bin:$PATH" >> /root/.bashrc
ENV PATH="/opt/sbt/bin:${PATH}"
