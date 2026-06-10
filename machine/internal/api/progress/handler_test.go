package progress

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"github.com/meowpow-png/enterprise-coffee/machine/internal/config"
	"github.com/meowpow-png/enterprise-coffee/machine/internal/machine"
)

func TestNewProgressHandler_InitializesService(t *testing.T) {
	t.Parallel()

	service := machine.NewService(testEspressoConfig(t))
	handler := NewProgressHandler(service)

	if handler.service != service {
		t.Fatal("expected service to be initialized")
	}
}

func TestNewProgressHandler_PanicsWhenServiceIsNil(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewProgressHandler(nil)
}

func TestServeHTTP_ReturnsProgressResponse(t *testing.T) {
	t.Parallel()

	service := machine.NewService(testEspressoConfig(t))
	service.Brew("ESPRESSO")

	handler := NewProgressHandler(service)
	request := httptest.NewRequest(
		http.MethodGet,
		"/progress",
		nil,
	)
	response := httptest.NewRecorder()

	handler.ServeHTTP(response, request)

	if response.Header().Get("Content-Type") != "application/json" {
		t.Fatalf(
			"expected content type %q, got %q",
			"application/json",
			response.Header().Get("Content-Type"),
		)
	}
	var body Response

	if err := json.NewDecoder(response.Body).Decode(&body); err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if body.Type != "ESPRESSO" {
		t.Fatalf("expected type %q, got %q", "ESPRESSO", body.Type)
	}
}

func testEspressoConfig(t *testing.T) []config.CoffeeTypeConfig {
	t.Helper()

	return []config.CoffeeTypeConfig{
		{
			Name:     "ESPRESSO",
			Duration: config.Duration(time.Second),
		},
	}
}
