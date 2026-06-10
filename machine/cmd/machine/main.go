package main

import (
	"context"
	"errors"
	"fmt"
	"log"
	"net/http"
	"os"
	"os/signal"
	"strings"
	"syscall"
	"time"

	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/config"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/health"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/machine"
)

func main() {
	ctx, stop := signal.NotifyContext(
		context.Background(),
		os.Interrupt,
		syscall.SIGTERM,
	)
	defer stop()

	flags := config.LoadFlags()
	configuration, err := config.Load(flags)
	if err != nil {
		log.Fatalf("failed to load config: %v", err)
	}
	printBanner(configuration)

	healthService := health.NewService(5 * time.Second)

	go pulseHealth(ctx, healthService)

	machineService := machine.NewService(configuration.Coffee.Types)
	server := NewServer(
		configuration.Server.Port,
		healthService,
		machineService,
	).Build()

	go NewShutdownHandler(server, 5*time.Second).Wait(ctx)

	if err := server.ListenAndServe(); err != nil {
		if !errors.Is(err, http.ErrServerClosed) {
			log.Fatalf("server failed: %v", err)
		}
	}
}

func pulseHealth(ctx context.Context, service *health.Service) {
	ticker := time.NewTicker(time.Second)
	defer ticker.Stop()

	for {
		select {
		case <-ctx.Done():
			return
		case <-ticker.C:
			service.Pulse()
		}
	}
}

func printBanner(config config.Config) {
	name := config.Machine.Name
	types := coffeeTypes(config)
	port := config.Server.Port

	fmt.Printf(`
============================================================
 Welcome to %s
============================================================

Delivering enterprise-grade espresso, one request at a time.

Available endpoints:

  GET  /health    Service health status
  GET  /status    Machine status
  GET  /progress  Current brewing progress
  POST /order     Submit coffee order

Supported coffee types:

%s

Listening on: http://localhost:%d

Ready to brew.

`, name, types, port)
}

func coffeeTypes(config config.Config) string {
	var builder strings.Builder

	for _, coffee := range config.Coffee.Types {
		builder.WriteString("  ")
		builder.WriteString(coffee.Name)
		builder.WriteString("\n")
	}
	return builder.String()
}
