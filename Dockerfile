FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /opt/app

COPY --from=build /workspace/target/neural-cutting-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p /opt/app/uploads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
