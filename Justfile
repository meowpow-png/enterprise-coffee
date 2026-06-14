set shell := ["bash", "-eu", "-o", "pipefail", "-c"]

mod machine "machine/Justfile"
mod backend "backend/Justfile"

machine-url := "http://localhost:8080"

# List available recipes
default:
    @just --list

# Execute docker compose command
compose +args:
    docker compose -p enterprise-coffee {{args}}

# Build machine and backend
build:
    @just machine::build
    @just backend::gradlew assemble

# Remove build artifacts
clean:
    @just machine::clean
    @just backend::gradlew clean

# Run machine and backend tests
test:
    @just machine::test ./...
    just backend::gradlew test
