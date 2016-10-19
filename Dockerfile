FROM dgellow/alpine-boot-clj
MAINTAINER Samuel El-Borai <samuel.elborai@gmail.com>

ADD . /app
WORKDIR /app

RUN rm -rf target
RUN /usr/bin/boot build
ENTRYPOINT /opt/jdk/bin/java -jar target/project.jar

EXPOSE 3000
