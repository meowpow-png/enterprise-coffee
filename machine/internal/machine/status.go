package machine

// Status represents the current state of the coffee machine.
type Status string

const (
	// StatusReady indicates the machine is ready to brew coffee.
	StatusReady Status = "READY"

	// StatusBrewing indicates the machine is currently brewing coffee.
	StatusBrewing Status = "BREWING"
)
