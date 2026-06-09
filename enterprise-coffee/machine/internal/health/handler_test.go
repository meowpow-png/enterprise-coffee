package health

import (
	"net/http"
	"net/http/httptest"
	"testing"
	"time"
)

func TestNewHandler_InitializesService(t *testing.T) {
	t.Parallel()

	service := &Service{}
	handler := NewHandler(service)

	if handler.service != service {
		t.Fatal("expected service to be initialized")
	}
}

func TestNewHandler_PanicsWhenServiceIsNil(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewHandler(nil)
}

func TestServeHTTP_ReturnsOkWhenHealthy(t *testing.T) {
	t.Parallel()

	service := NewService(time.Second)
	service.lastPulse = time.Now()

	handler := NewHandler(service)

	request := httptest.NewRequest(
		http.MethodGet,
		"/health",
		nil,
	)
	response := httptest.NewRecorder()

	handler.ServeHTTP(response, request)

	if response.Code != http.StatusOK {
		t.Fatalf(
			"expected status %d, got %d",
			http.StatusOK,
			response.Code,
		)
	}
}

func TestServeHTTP_ReturnsServiceUnavailableWhenUnhealthy(t *testing.T) {
	t.Parallel()

	service := NewService(time.Second)
	service.lastPulse = time.Now().Add(-2 * time.Second)

	handler := NewHandler(service)

	request := httptest.NewRequest(
		http.MethodGet,
		"/health",
		nil,
	)
	response := httptest.NewRecorder()

	handler.ServeHTTP(response, request)

	if response.Code != http.StatusServiceUnavailable {
		t.Fatalf(
			"expected status %d, got %d",
			http.StatusServiceUnavailable,
			response.Code,
		)
	}
}
