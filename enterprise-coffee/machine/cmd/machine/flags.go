package main

import "flag"

// Flags contains command line flags.
type Flags struct {
	Port int
}

var port = flag.Int(
	"port",
	8080,
	"HTTP server port",
)

// LoadFlags loads command line flags.
func LoadFlags() Flags {
	flag.Parse()

	if *port <= 0 {
		panic("port must be positive")
	}
	return Flags{
		Port: *port,
	}
}
