# ---- Stage 1: Build JAR ----
    FROM eclipse-temurin:21-jdk-alpine AS builder

    WORKDIR /src/eventsphere-be
    COPY . .
    RUN chmod +x gradlew
    RUN ./gradlew clean bootJar  
    
    # ---- Stage 2: Run the Application ----
    FROM eclipse-temurin:21-jdk-alpine AS runner
    
    ARG USER_NAME=eventsphere-be
    ARG USER_UID=1000
    ARG USER_GID=${USER_UID}
    
    # Create a non-root user for security
    RUN addgroup -g ${USER_GID} ${USER_NAME} \
        && adduser -h /opt/eventsphere-be -D -u ${USER_UID} -G ${USER_NAME} ${USER_NAME}
    
    # Switch to the new user
    USER ${USER_NAME}
    WORKDIR /opt/eventsphere-be
    
    # Copy the latest built JAR from the builder stage
    COPY --from=builder --chown=${USER_UID}:${USER_GID} /src/eventsphere-be/build/libs/*.jar app.jar
    
    EXPOSE 8080
    
    ENTRYPOINT ["java"]
    CMD ["-jar", "app.jar"]