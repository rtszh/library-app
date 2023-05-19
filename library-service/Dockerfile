FROM openjdk:17.0.1-jdk-slim as base

WORKDIR /app

# for copying dependencies from local computer into image
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# copying source code into image
COPY src ./src

# build for prod
FROM base as build
RUN ./mvnw package

# prod
FROM openjdk:17.0.1-jdk-slim as production

# install curl
RUN apt-get -y update
RUN apt-get -y install curl

EXPOSE 9191
COPY --from=build /app/target/library-service-*.jar /library-service.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/library-service.jar"]