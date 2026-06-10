package status

import "github.com/meowpow-png/enterprise-coffee/machine/internal/machine"

// Response represents machine status.
type Response struct {
	Status machine.Status `json:"status"`
}
