package order

import "github.com/meowpow-png/enterprise-coffee/machine/internal/machine"

// Request represents a coffee order request.
type Request struct {
	Type machine.CoffeeType `json:"type"`
}
