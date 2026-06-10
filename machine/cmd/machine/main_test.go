package main

import (
	"fmt"
	"io"
	"net"
	"net/http"
	"testing"
	"time"

	"github.com/meowpow-png/enterprise-coffee/machine/internal/health"
	"github.com/meowpow-png/enterprise-coffee/machine/internal/machine"
)

func TestHealthEndpoint_ReturnsOK(t *testing.T) {
	healthService := health.NewService(5 * time.Second)
	machineService := machine.NewService(testEspressoConfig(t))

	listener, err := net.Listen("tcp", "127.0.0.1:0")
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	defer func(listener net.Listener) {
		_ = listener.Close()
	}(listener)

	addr := listener.Addr().(*net.TCPAddr)

	server := NewServer(
		addr.Port,
		healthService,
		machineService,
	).Build()

	go func() {
		_ = server.Serve(listener)
	}()
	client := &http.Client{
		Timeout: time.Second,
	}
	//goland:noinspection HttpUrlsUsage
	url := fmt.Sprintf("http://%s/health", listener.Addr().String())

	resp, err := client.Get(url)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	defer func(Body io.ReadCloser) {
		_ = Body.Close()
	}(resp.Body)

	if resp.StatusCode != http.StatusOK {
		t.Fatalf("expected %d, got %d", http.StatusOK, resp.StatusCode)
	}
}
