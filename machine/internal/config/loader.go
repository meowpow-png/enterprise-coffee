package config

import (
	"encoding/json"
	"errors"
	"fmt"
	"os"
)

// Load reads configuration from the given file path.
func Load(flags Flags) (Config, error) {
	data, err := os.ReadFile(flags.ConfigFile)
	if err != nil {
		return Config{}, fmt.Errorf(
			"read configuration file %q: %w",
			flags.ConfigFile,
			err,
		)
	}
	var config Config

	if err := json.Unmarshal(data, &config); err != nil {
		return Config{}, fmt.Errorf(
			"parse configuration file %q: %w",
			flags.ConfigFile,
			err,
		)
	}
	config = applyOverrides(config, flags)
	return config.Validate()
}

// Validate validates the configuration.
func (c Config) Validate() (Config, error) {
	if c.Machine.Name == "" {
		return c, errors.New("machine name must not be empty")
	}
	if c.Server.Port <= 0 {
		return c, errors.New("server port must be positive")
	}
	if len(c.Coffee.Types) == 0 {
		return c, errors.New("at least one coffee type must be configured")
	}
	for _, coffee := range c.Coffee.Types {
		if coffee.Name == "" {
			return c, errors.New("coffee name must not be empty")
		}
		if coffee.Duration <= 0 {
			return c, fmt.Errorf(
				"coffee type %q duration must be positive",
				coffee.Name,
			)
		}
	}
	return c, nil
}

func applyOverrides(config Config, flags Flags) Config {
	if IsFlagSet("port") {
		config.Server.Port = flags.Port
	}
	return config
}
