# Remove build artifacts
machine-clean:
    rm -rf build

# Build binary artifacts
machine-build:
    just build-machine

# Build and run coffee machine
machine-run:
    just build-machine
    ./build/bin/machine --port=8080

# Run go tests
[arg("args", help="test arguments")]
machine-test *args:
	go test -race -count=1 {{args}}

# Run Go tests with coverage
machine-test-coverage:
	@mkdir -p build/coverage
	just machine-test \
		-coverprofile=build/coverage/coverage.out \
		./...

	go tool cover -html=build/coverage/coverage.out \
		-o build/coverage/coverage.html

[private]
build-machine:
    @mkdir -p build/bin
    CGO_ENABLED=0 GOOS=linux GOARCH=amd64 \
    go build -ldflags="-s -w" -o build/bin/machine ./cmd/machine
