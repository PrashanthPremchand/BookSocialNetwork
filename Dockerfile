# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

# 1. Reach into the sub-folder to get the pom.xml
COPY book-network/pom.xml .
RUN mvn dependency:go-offline

# 2. Reach into the sub-folder to get the source code
COPY book-network/src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17
ARG PROFILE=dev
ARG APP_VERSION=1.0.0

WORKDIR /app

# 3. Use a wildcard to find the JAR and rename it to 'app.jar'
# This prevents the CMD from breaking when the version changes
COPY --from=build /build/target/book-network-*.jar /app/app.jar

EXPOSE 8088

# Environment variables matching your Docker Compose service names
ENV DB_URL=jdbc:postgresql://postgres:5432/book_social_network_db
ENV ACTIVE_PROFILE=${PROFILE}

# 4. Use ENTRYPOINT with the static 'app.jar' name
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${ACTIVE_PROFILE}", "-Dspring.datasource.url=${DB_URL}", "app.jar"]