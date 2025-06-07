# Dockerfile
FROM eclipse-temurin:21-jdk-alpine

# Establece el directorio de trabajo
WORKDIR /app

# Copia el jar construido
COPY target/*.jar app.jar

# Expone el puerto (ajústalo por módulo)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
