# --- builder ---
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace
# copy pom + sources
COPY pom.xml .
COPY src ./src
# build jar (skip tests to speed dev; remove -DskipTests for CI gate)
RUN mvn -B -DskipTests package

# --- runtime ---
FROM eclipse-temurin:17-jre
ARG JAR_FILE=/workspace/target/*-SNAPSHOT.jar
COPY --from=build ${JAR_FILE} /app/app.jar
ENV JAVA_OPTS=""
EXPOSE 8080 9090
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget -q -O - http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
