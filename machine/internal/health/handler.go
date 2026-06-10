package health

import "net/http"

// Handler handles health requests.
type Handler struct {
	service *Service
}

// NewHandler creates a new health handler.
func NewHandler(service *Service) *Handler {
	if service == nil {
		panic("service must not be nil")
	}
	return &Handler{
		service: service,
	}
}

// ServeHTTP serves health requests.
func (h *Handler) ServeHTTP(
	writer http.ResponseWriter,
	_ *http.Request,
) {
	if h.service.Healthy() {
		writer.WriteHeader(http.StatusOK)
		return
	}
	writer.WriteHeader(http.StatusServiceUnavailable)
}
