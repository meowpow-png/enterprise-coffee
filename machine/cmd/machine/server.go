package main

import (
	"fmt"
	"net/http"

	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/api/order"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/api/progress"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/api/status"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/health"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/machine"
)

// Server creates the machine HTTP server.
type Server struct {
	port           int
	healthService  *health.Service
	machineService *machine.Service
}

// NewServer creates a new server.
func NewServer(
	port int,
	healthService *health.Service,
	machineService *machine.Service,
) *Server {
	if port <= 0 {
		panic("port must be positive")
	}
	if healthService == nil {
		panic("health service must not be nil")
	}
	if machineService == nil {
		panic("machine service must not be nil")
	}
	return &Server{
		port:           port,
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
		Addr:    fmt.Sprintf(":%d", s.port),
		Handler: mux,
	}
}
