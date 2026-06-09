package config

import "flag"

// Flags contains command line flags.
type Flags struct {
	Port       int
	ConfigFile string
}

var port = flag.Int(
	"port",
	8080,
	"HTTP server port",
)

var configFile = flag.String(
	"config",
	"config.json",
	"Configuration file",
)

// LoadFlags loads command line flags.
func LoadFlags() Flags {
	flag.Parse()

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
