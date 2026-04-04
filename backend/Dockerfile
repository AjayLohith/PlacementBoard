# Build from backend folder (Render: Root Directory = backend, Dockerfile path = Dockerfile)
# docker build -t placement-portal-api .

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/placement-portal-1.0.0.jar app.jar

EXPOSE 5001
ENTRYPOINT ["java", "-jar", "app.jar"]
