package main

import (
	"context"
	"errors"
	"fmt"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/api/config"
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
	printBanner(flags.Port)

	healthService := health.NewService(5 * time.Second)

	go pulseHealth(ctx, healthService)

	brewer := machine.NewBrewer(10 * time.Second)
	machineService := machine.NewService(brewer)
	server := NewServer(
		flags.Port,
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

func printBanner(port int) {
	fmt.Printf(`
============================================================
 Welcome to BrewMaster 3000
============================================================

Delivering enterprise-grade espresso, one request at a time.

Available endpoints:

  GET  /health    Service health status
  GET  /status    Machine status
  GET  /progress  Current brewing progress
  POST /order     Submit coffee order

Supported coffee types:

  ESPRESSO
  AMERICANO
  CAPPUCCINO

Listening on: http://localhost:%d

Ready to brew.

`, port)
}
