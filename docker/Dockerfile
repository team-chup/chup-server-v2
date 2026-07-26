FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/thup-*.jar app.jar
RUN useradd --system --no-create-home --shell /usr/sbin/nologin appuser \
    && chown appuser:appuser app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
