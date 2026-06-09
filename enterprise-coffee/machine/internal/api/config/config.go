package config

import (
	"encoding/json"
	"time"
)

type Duration time.Duration

// Config represents machine configuration.
type Config struct {
	Machine MachineConfig `json:"machine"`
	Server  ServerConfig  `json:"server"`
	Coffee  CoffeeConfig  `json:"coffee"`
}

// MachineConfig represents machine settings.
type MachineConfig struct {
	Name string `json:"name"`
}

// ServerConfig represents HTTP server settings.
type ServerConfig struct {
	Port int `json:"port"`
}

// CoffeeConfig represents supported coffee beverages.
type CoffeeConfig struct {
	Types []CoffeeTypeConfig `json:"types"`
}

// CoffeeTypeConfig represents a supported coffee beverage.
type CoffeeTypeConfig struct {
	Name     string   `json:"name"`
	Duration Duration `json:"duration"`
}

// UnmarshalJSON unmarshals a duration string.
func (d *Duration) UnmarshalJSON(data []byte) error {
	var value string

	if err := json.Unmarshal(data, &value); err != nil {
		return err
	}
	duration, err := time.ParseDuration(value)
	if err != nil {
		return err
	}
	*d = Duration(duration)

	return nil
}
