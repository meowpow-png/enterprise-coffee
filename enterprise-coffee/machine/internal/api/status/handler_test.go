package status

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/config"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/machine"
)

func TestNewStatusHandler_InitializesService(t *testing.T) {
	t.Parallel()

	service := machine.NewService(testEspressoConfig(t))
	handler := NewStatusHandler(service)

	if handler.service != service {
		t.Fatal("expected service to be initialized")
	}
}

func TestNewStatusHandler_PanicsWhenServiceIsNil(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewStatusHandler(nil)
}

func TestServeHTTP_ReturnsStatusResponse(t *testing.T) {
	t.Parallel()

	handler := NewStatusHandler(
		machine.NewService(testEspressoConfig(t)),
	)
	request := httptest.NewRequest(
		http.MethodGet,
		"/status",
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
	if body.Status != machine.StatusReady {
		t.Fatalf(
			"expected status %q, got %q",
			machine.StatusReady,
			body.Status,
		)
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
