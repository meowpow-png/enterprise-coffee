package main

import (
	"testing"
	"time"

	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/config"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/health"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/machine"
)

func TestNewServer_InitializesPort(t *testing.T) {
	t.Parallel()

	server := NewServer(
		8080,
		health.NewService(time.Second),
		machine.NewService(testEspressoConfig(t)),
	)
	if server.port != 8080 {
		t.Fatalf("expected port %d, got %d", 8080, server.port)
	}
}

func TestNewServer_InitializesHealthService(t *testing.T) {
	t.Parallel()

	healthService := health.NewService(time.Second)
	machineService := machine.NewService(testEspressoConfig(t))

	server := NewServer(
		8080,
		healthService,
		machineService,
	)
	if server.healthService != healthService {
		t.Fatal("expected health service to be initialized")
	}
}

func TestNewServer_InitializesMachineService(t *testing.T) {
	t.Parallel()

	healthService := health.NewService(time.Second)
	machineService := machine.NewService(testEspressoConfig(t))

	server := NewServer(
		8080,
		healthService,
		machineService,
	)
	if server.machineService != machineService {
		t.Fatal("expected machine service to be initialized")
	}
}

func TestNewServer_PanicsWhenPortIsNotPositive(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewServer(
		0,
		health.NewService(time.Second),
		machine.NewService(testEspressoConfig(t)),
	)
}

func TestNewServer_PanicsWhenHealthServiceIsNil(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewServer(
		8080,
		nil,
		machine.NewService(testEspressoConfig(t)),
	)
}

func TestNewServer_PanicsWhenMachineServiceIsNil(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewServer(
		8080,
		health.NewService(time.Second),
		nil,
	)
}

func TestBuild_ReturnsServer(t *testing.T) {
	t.Parallel()

	s := NewServer(
		8080,
		health.NewService(time.Second),
		machine.NewService(testEspressoConfig(t)),
	)
	server := s.Build()

	if server == nil {
		t.Fatal("expected server to be non-nil")
	}

	if server.Addr != ":8080" {
		t.Fatalf("expected addr %q, got %q", ":8080", server.Addr)
	}

	if server.Handler == nil {
		t.Fatal("expected handler to be non-nil")
	}
}

func testEspressoConfig(t *testing.T) []config.CoffeeTypeConfig {
	t.Helper()

	return []config.CoffeeTypeConfig{
		{
			Name:     "ESPRESSO",
			Duration: config.Duration(time.Second),
		},
	}
}
