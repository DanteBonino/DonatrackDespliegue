# Etapa 1
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests package

# Etapa 2
FROM eclipse-temurin:17-jre AS donaciones
WORKDIR /app
COPY --from=build /src/donaciones-service/target/*-jar-with-dependencies.jar ./
# Punto de montaje del volumen "fotos" (ver docker-compose)
RUN mkdir -p /app/uploads/fotos
EXPOSE 7070
CMD ["java", "-jar", "servidor-jar-with-dependencies.jar"]

FROM eclipse-temurin:17-jre AS logistica
WORKDIR /app
COPY --from=build /src/logistica-service/target/*-jar-with-dependencies.jar ./
EXPOSE 8080
CMD ["java", "-jar", "servidor-jar-with-dependencies.jar"]