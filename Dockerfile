# 빌드
FROM gradle:7.5.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar -x test --no-daemon

# 실행
FROM eclipse-temurin:17-jre
COPY --from=build /app/build/libs/*.jar app.jar
ENV PROD_SERVER_PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar /app.jar --server.port=${PROD_SERVER_PORT}"]