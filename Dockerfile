# Etapa 1
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests package

# Etapa 2
# Imagen liviana: solo JRE (para ejecutar), sin Maven ni compilador.
FROM eclipse-temurin:17-jre AS donaciones
WORKDIR /app
# Te quedas con los jars de la etapa 1
COPY --from=build /src/donaciones-service/target/*-jar-with-dependencies.jar ./
# Documenta el puerto donde escucha la app. No publica nada: eso lo hace el compose.
EXPOSE 7070
CMD ["java", "-jar", "servidor-jar-with-dependencies.jar"]

FROM eclipse-temurin:17-jre AS logistica
WORKDIR /app
COPY --from=build /src/logistica-service/target/*-jar-with-dependencies.jar ./
EXPOSE 8080
CMD ["java", "-jar", "servidor-jar-with-dependencies.jar"]