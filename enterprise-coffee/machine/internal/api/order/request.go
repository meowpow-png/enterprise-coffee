package order

import "senthora.com/gatlingfx/enterprise-coffee/machine/internal/machine"

// Request represents a coffee order request.
type Request struct {
	Type machine.CoffeeType `json:"type"`
}
