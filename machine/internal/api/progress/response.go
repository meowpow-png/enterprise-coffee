package progress

import "github.com/meowpow-png/enterprise-coffee/machine/internal/machine"

// Response represents brewing progress.
type Response struct {
	Type     machine.CoffeeType `json:"type"`
	Progress int                `json:"progress"`
}
