package main

import (
	"context"
	"errors"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/health"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/machine"
)

func main() {
	log.Println("starting coffee machine")
	ctx, stop := signal.NotifyContext(
		context.Background(),
		os.Interrupt,
		syscall.SIGTERM,
	)
	defer stop()

	flags := LoadFlags()
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
	log.Printf("listening on :%d", flags.Port)

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
