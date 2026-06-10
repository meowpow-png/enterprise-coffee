package status

import "senthora.com/gatlingfx/enterprise-coffee/machine/internal/machine"

// Response represents machine status.
type Response struct {
	Status machine.Status `json:"status"`
}
