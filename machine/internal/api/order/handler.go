package order

import (
	"encoding/json"
	"log"
	"net/http"

	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/machine"
)

// Handler handles coffee order requests.
type Handler struct {
	service *machine.Service
}

// NewOrderHandler creates a new order handler.
func NewOrderHandler(service *machine.Service) *Handler {
	if service == nil {
		panic("service must not be nil")
	}
	return &Handler{
		service: service,
	}
}

// ServeHTTP serves coffee order requests.
func (h *Handler) ServeHTTP(
	writer http.ResponseWriter,
	request *http.Request,
) {
	var order Request

	if err := json.NewDecoder(request.Body).Decode(&order); err != nil {
		log.Printf("invalid order request: %v", err)

		writer.WriteHeader(http.StatusBadRequest)
		return
	}
	if !h.service.Brew(order.Type) {
		log.Printf("rejected %s order", order.Type)

		writer.WriteHeader(http.StatusConflict)
		return
	}
	log.Printf("accepted %s order", order.Type)

	writer.WriteHeader(http.StatusAccepted)
}
