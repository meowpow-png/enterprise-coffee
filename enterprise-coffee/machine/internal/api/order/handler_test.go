package order

import (
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/api/config"
	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/machine"
)

func TestNewOrderHandler_InitializesService(t *testing.T) {
	t.Parallel()

	service := machine.NewService(testEspressoConfig(t))
	handler := NewOrderHandler(service)

	if handler.service != service {
		t.Fatal("expected service to be initialized")
	}
}

func TestNewOrderHandler_PanicsWhenServiceIsNil(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewOrderHandler(nil)
}

func TestServeHTTP_ReturnsBadRequestWhenRequestIsInvalid(t *testing.T) {
	t.Parallel()

	handler := NewOrderHandler(
		machine.NewService(testEspressoConfig(t)),
	)
	request := httptest.NewRequest(
		http.MethodPost,
		"/order",
		strings.NewReader("{"),
	)
	response := httptest.NewRecorder()

	handler.ServeHTTP(response, request)

	if response.Code != http.StatusBadRequest {
		t.Fatalf(
			"expected status %d, got %d",
			http.StatusBadRequest,
			response.Code,
		)
	}
}

func TestServeHTTP_ReturnsConflictWhenOrderIsRejected(t *testing.T) {
	t.Parallel()

	handler := NewOrderHandler(
		machine.NewService(testEspressoConfig(t)),
	)
	request := httptest.NewRequest(
		http.MethodPost,
		"/order",
		strings.NewReader(`{"type":"AMERICANO"}`),
	)
	response := httptest.NewRecorder()

	handler.ServeHTTP(response, request)

	if response.Code != http.StatusConflict {
		t.Fatalf(
			"expected status %d, got %d",
			http.StatusConflict,
			response.Code,
		)
	}
}

func TestServeHTTP_ReturnsAcceptedWhenOrderIsAccepted(t *testing.T) {
	t.Parallel()

	handler := NewOrderHandler(
		machine.NewService(testEspressoConfig(t)),
	)
	request := httptest.NewRequest(
		http.MethodPost,
		"/order",
		strings.NewReader(`{"type":"ESPRESSO"}`),
	)
	response := httptest.NewRecorder()

	handler.ServeHTTP(response, request)

	if response.Code != http.StatusAccepted {
		t.Fatalf(
			"expected status %d, got %d",
			http.StatusAccepted,
			response.Code,
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
