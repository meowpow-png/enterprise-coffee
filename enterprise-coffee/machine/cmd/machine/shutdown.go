package main

import (
	"context"
	"log"
	"net/http"
	"time"
)

// ShutdownHandler gracefully shuts down the server.
type ShutdownHandler struct {
	server  *http.Server
	timeout time.Duration
}

// NewShutdownHandler creates a new shutdown handler.
func NewShutdownHandler(server *http.Server, timeout time.Duration) *ShutdownHandler {
	if server == nil {
		panic("server must not be nil")
	}
	if timeout <= 0 {
		panic("timeout must be positive")
	}
	return &ShutdownHandler{
		server:  server,
		timeout: timeout,
	}
}

// Wait blocks until shutdown is requested.
func (h *ShutdownHandler) Wait(ctx context.Context) {
	<-ctx.Done()

	shutdownCtx, cancel := context.WithTimeout(
		context.Background(),
		h.timeout,
	)
	defer cancel()

	log.Println("shutdown requested")
	_ = h.server.Shutdown(shutdownCtx)
}
