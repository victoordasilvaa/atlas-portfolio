FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY . .
RUN mvn -B -ntp verify
FROM tomcat:9.0.98-jdk17-temurin AS server
RUN rm -rf /usr/local/tomcat/webapps/*
COPY docker/StartServer.java /opt/StartServer.java
ENV JAVA_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED -Duser.timezone=UTC"
EXPOSE 8080
CMD ["sh", "-c", "java /opt/StartServer.java && exec catalina.sh run"]
FROM server AS app
COPY --from=build /build/portfolio-app/target/portfolio.war /usr/local/tomcat/webapps/portfolio.war
FROM server AS mock
COPY --from=build /build/members-mock/target/members-mock.war /usr/local/tomcat/webapps/members-mock.war
