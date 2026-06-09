package health

import (
	"testing"
	"time"
)

func TestNewService_InitializesLastPulse(t *testing.T) {
	t.Parallel()

	before := time.Now()
	service := NewService(time.Second)
	after := time.Now()

	if service.lastPulse.Before(before) {
		t.Fatal("expected lastPulse to be initialized")
	}
	if service.lastPulse.After(after) {
		t.Fatal("expected lastPulse to be initialized")
	}
}

func TestNewService_InitializesMaxPulseAge(t *testing.T) {
	t.Parallel()

	maxPulseAge := time.Second
	service := NewService(maxPulseAge)

	if service.maxPulseAge != maxPulseAge {
		t.Fatalf(
			"expected maxPulseAge %s, got %s",
			maxPulseAge,
			service.maxPulseAge,
		)
	}
}

func TestNewService_PanicsWhenMaxPulseAgeIsNotPositive(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewService(0)
}

func TestPulse_UpdatesLastPulse(t *testing.T) {
	t.Parallel()

	service := NewService(time.Second)

	service.lastPulse = time.Time{}
	service.Pulse()

	if service.lastPulse.IsZero() {
		t.Fatal("expected lastPulse to be updated")
	}
}

func TestHealthy_ReturnsTrueWhenPulseIsRecent(t *testing.T) {
	t.Parallel()

	service := NewService(time.Second)
	service.lastPulse = time.Now()

	if !service.Healthy() {
		t.Fatal("expected service to be healthy")
	}
}

func TestHealthy_ReturnsFalseWhenPulseIsExpired(t *testing.T) {
	t.Parallel()

	service := NewService(time.Second)
	service.lastPulse = time.Now().Add(-2 * time.Second)

	if service.Healthy() {
		t.Fatal("expected service to be unhealthy")
	}
}
