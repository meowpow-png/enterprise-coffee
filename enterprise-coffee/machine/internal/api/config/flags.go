package config

import (
	"flag"
	"os"
)

const (
	defaultPort       = 8080
	defaultConfigFile = "config.json"
)

// Flags contains command line flags.
type Flags struct {
	Port       int
	ConfigFile string
}

// LoadFlags loads command line flags.
func LoadFlags() Flags {
	return loadFlags(flag.CommandLine, os.Args[1:])
}

func loadFlags(fs *flag.FlagSet, args []string) Flags {
	port := fs.Int(
		"port",
		defaultPort,
		"HTTP server port",
	)
	configFile := fs.String(
		"config",
		defaultConfigFile,
		"Configuration file",
	)
	if err := fs.Parse(args); err != nil {
		panic(err)
	}
	if *port <= 0 {
		panic("port must be positive")
	}
	if *configFile == "" {
		panic("config file must not be empty")
	}
	return Flags{
		Port:       *port,
		ConfigFile: *configFile,
	}
}

func IsFlagSet(name string) bool {
	var found bool

	flag.Visit(func(f *flag.Flag) {
		if f.Name == name {
			found = true
		}
	})
	return found
}
