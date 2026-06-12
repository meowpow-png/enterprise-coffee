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
