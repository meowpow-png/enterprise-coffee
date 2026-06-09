package status

import (
	"encoding/json"
	"net/http"

	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/machine"
)

// Handler handles machine status requests.
type Handler struct {
	service *machine.Service
}

// NewStatusHandler creates a new status handler.
func NewStatusHandler(service *machine.Service) *Handler {
	if service == nil {
		panic("service must not be nil")
	}
	return &Handler{
		service: service,
	}
}

// ServeHTTP serves machine status requests.
func (h *Handler) ServeHTTP(
	writer http.ResponseWriter,
	_ *http.Request,
) {
	response := Response{
		Status: h.service.Status(),
	}
	writer.Header().Set("Content-Type", "application/json")
	_ = json.NewEncoder(writer).Encode(response)
}
