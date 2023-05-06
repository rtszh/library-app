FROM openjdk:11-jdk-slim as base

WORKDIR /app

# for copying dependencies from local computer into image
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# for download dependencies into started container
# RUN ./mvnw dependency:resolve

# copying source code into image
COPY src ./src

# 2. dev
# run commands into container for start application and mysql db
FROM base as development
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000'"]

# 3. build for prod
FROM base as build
RUN ./mvnw package

# 4. prod
FROM openjdk:11-jre-slim as production
EXPOSE 8080
COPY --from=build /app/target/hw-34-docker-*.jar /hw-34-docker.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/hw-34-docker.jar"]