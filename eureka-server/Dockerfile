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
EXPOSE 8761
COPY --from=build /app/target/eureka-server-*.jar /eureka-server.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/eureka-server.jar"]