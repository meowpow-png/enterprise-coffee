FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

COPY build/libs/enterprise-coffee-backend.jar backend.jar

RUN jar -xf backend.jar

RUN jdeps \
    --ignore-missing-deps \
    --recursive \
    --multi-release 21 \
    --print-module-deps \
    --class-path 'BOOT-INF/lib/*' \
    BOOT-INF/classes \
    > modules.txt

RUN jlink \
    --module-path "${JAVA_HOME}/jmods" \
    --add-modules "$(cat modules.txt)" \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --compress=2 \
    --output /runtime

FROM alpine:3.23

WORKDIR /app

COPY --from=builder /runtime /opt/java
COPY build/libs/enterprise-coffee-backend.jar backend.jar

EXPOSE 8081

ENTRYPOINT ["/opt/java/bin/java", "-jar", "backend.jar"]
