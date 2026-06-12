package order

import (
	"encoding/json"
	"errors"
	"log"
	"net/http"

	"github.com/meowpow-png/enterprise-coffee/machine/internal/machine"
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
	err := h.service.Brew(order.Type)
	if err != nil {
		log.Printf("rejected %s order: %v", order.Type, err)

		switch {
		case errors.Is(err, machine.ErrMachineBusy):
			writer.WriteHeader(http.StatusConflict)
		case errors.Is(err, machine.ErrInvalidCoffeeType):
			writer.WriteHeader(http.StatusBadRequest)
		default:
			writer.WriteHeader(http.StatusInternalServerError)
		}
		return
	}
	log.Printf("accepted %s order", order.Type)

	writer.WriteHeader(http.StatusAccepted)
}
