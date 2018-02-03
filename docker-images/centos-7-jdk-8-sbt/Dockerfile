FROM centos:7

RUN yum install -y java-1.8.0-openjdk

RUN curl -L -o /tmp/sbt-1.0.4.tgz https://github.com/sbt/sbt/releases/download/v1.0.4/sbt-1.0.4.tgz
RUN tar zxvf /tmp/sbt-1.0.4.tgz -C /opt
RUN echo "export PATH=/opt/sbt/bin:$PATH" >> /root/.bashrc
ENV PATH="/opt/sbt/bin:${PATH}"

RUN yum install -y rpm-build
