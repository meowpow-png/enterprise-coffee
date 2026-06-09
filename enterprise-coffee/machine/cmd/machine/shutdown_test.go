package main

import (
	"context"
	"net/http"
	"testing"
	"time"
)

func TestNewShutdownHandler_InitializesServer(t *testing.T) {
	t.Parallel()

	server := &http.Server{}
	timeout := time.Second

	handler := NewShutdownHandler(server, timeout)

	if handler.server != server {
		t.Fatal("expected server to be initialized")
	}
}

func TestNewShutdownHandler_InitializesTimeout(t *testing.T) {
	t.Parallel()

	server := &http.Server{}
	timeout := time.Second

	handler := NewShutdownHandler(server, timeout)

	if handler.timeout != timeout {
		t.Fatalf(
			"expected timeout %s, got %s",
			timeout,
			handler.timeout,
		)
	}
}

func TestNewShutdownHandler_PanicsWhenServerIsNil(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewShutdownHandler(nil, time.Second)
}

func TestNewShutdownHandler_PanicsWhenTimeoutIsNotPositive(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewShutdownHandler(&http.Server{}, 0)
}

func TestWait_ShutsDownServerWhenContextIsCancelled(t *testing.T) {
	t.Parallel()

	handler := NewShutdownHandler(
		&http.Server{},
		time.Second,
	)
	ctx, cancel := context.WithCancel(context.Background())
	done := make(chan struct{})

	go func() {
		handler.Wait(ctx)
		close(done)
	}()
	cancel()

	select {
	case <-done:
	case <-time.After(time.Second):
		t.Fatal("expected shutdown handler to return")
	}
}
