FROM gcr.io/distroless/static-debian13:latest

WORKDIR /app

COPY build/bin/machine .
COPY config.json ./default-config.json

ENTRYPOINT ["/app/machine"]
CMD ["--config", "/app/default-config.json"]
