package progress

import (
	"encoding/json"
	"net/http"

	"github.com/meowpow-png/enterprise-coffee/machine/internal/machine"
)

// Handler handles brewing progress requests.
type Handler struct {
	service *machine.Service
}

// NewProgressHandler creates a new progress handler.
func NewProgressHandler(service *machine.Service) *Handler {
	if service == nil {
		panic("service must not be nil")
	}
	return &Handler{
		service: service,
	}
}

// ServeHTTP serves brewing progress requests.
func (h *Handler) ServeHTTP(
	writer http.ResponseWriter,
	_ *http.Request,
) {
	coffee, progress := h.service.Progress()

	response := Response{
		Type:     coffee,
		Progress: progress,
	}
	writer.Header().Set("Content-Type", "application/json")
	_ = json.NewEncoder(writer).Encode(response)
}
