FROM dgellow/alpine-boot-clj
MAINTAINER Samuel El-Borai <samuel.elborai@gmail.com>

# RUN boot build

ADD . /app
WORKDIR /app

RUN rm -rf target
RUN /usr/bin/boot build
RUN /opt/jdk/bin/java -jar project.jar
