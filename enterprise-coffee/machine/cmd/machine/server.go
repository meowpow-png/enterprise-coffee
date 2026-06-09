package main

import (
	"net/http"

	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/api/order"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/api/progress"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/api/status"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/health"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/machine"
)

// Server creates the machine HTTP server.
type Server struct {
	healthService  *health.Service
	machineService *machine.Service
}

// NewServer creates a new server.
func NewServer(
	healthService *health.Service,
	machineService *machine.Service,
) *Server {
	return &Server{
		healthService:  healthService,
		machineService: machineService,
	}
}

// Build creates the HTTP server.
func (s *Server) Build() *http.Server {
	mux := http.NewServeMux()

	mux.Handle("/health", health.NewHandler(s.healthService))
	mux.Handle("/status", status.NewStatusHandler(s.machineService))
	mux.Handle("/progress", progress.NewProgressHandler(s.machineService))
	mux.Handle("/order", order.NewOrderHandler(s.machineService))

	return &http.Server{
		Addr:    ":8080",
		Handler: mux,
	}
}
