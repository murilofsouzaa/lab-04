FROM docker.io/library/maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src src
RUN mvn -q package -DskipTests

FROM docker.io/library/eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/moeda-estudantil-1.0.0.jar app.jar
# No Render, DB_HOST/DB_PORT/DB_NAME vêm do banco Postgres; sem eles usa-se o H2 local
CMD ["sh", "-c", "if [ -n \"$DB_HOST\" ]; then export DATABASE_URL=jdbc:postgresql://$DB_HOST:${DB_PORT:-5432}/$DB_NAME; fi; exec java -jar app.jar"]
