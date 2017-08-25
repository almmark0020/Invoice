FROM frolvlad/alpine-oraclejdk8:slim

VOLUME /tmp

ADD @project.build.finalName@.@project.packaging@ @project.build.finalName@.@project.packaging@

RUN sh -c 'touch /@project.build.finalName@.@project.packaging@'

ENV JAVA_OPTS=""

EXPOSE @app.port@

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /@project.build.finalName@.@project.packaging@"]
