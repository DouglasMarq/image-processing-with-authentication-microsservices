FROM eclipse-temurin:21-jdk-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY build/libs/images-service.jar /app/images-service.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java","-Dcom.amazonaws.sdk.disableCertChecking=true", "-jar", "images-service.jar"]