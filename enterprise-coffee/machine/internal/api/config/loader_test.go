package config

import (
	"flag"
	"fmt"
	"os"
	"path/filepath"
	"slices"
	"testing"
	"time"
)

func TestLoad_ReturnsConfig(t *testing.T) {
	t.Parallel()

	config, err := Load(Flags{
		ConfigFile: writeTestConfig(t),
	})
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	assertConfigEqual(t, testConfig(t), config)
}

func TestLoad_ReturnsErrorWhenFileDoesNotExist(t *testing.T) {
	t.Parallel()

	_, err := Load(Flags{
		ConfigFile: "does-not-exist.json",
	})
	if err == nil {
		t.Fatal("expected error")
	}
}

func TestLoad_ReturnsErrorWhenConfigIsInvalidJson(t *testing.T) {
	t.Parallel()

	path := filepath.Join(t.TempDir(), "config.json")

	if err := os.WriteFile(
		path,
		[]byte(`{`),
		0644,
	); err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	_, err := Load(Flags{
		ConfigFile: path,
	})
	if err == nil {
		t.Fatal("expected error")
	}
}

func TestLoad_AppliesPortOverride(t *testing.T) {
	old := flag.CommandLine
	defer func() {
		flag.CommandLine = old
	}()

	fs := flag.NewFlagSet("test", flag.ContinueOnError)
	fs.Int("port", defaultPort, "")

	if err := fs.Parse([]string{"--port=9090"}); err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	flag.CommandLine = fs

	config, err := Load(Flags{
		Port:       9090,
		ConfigFile: writeTestConfig(t),
	})
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if config.Server.Port != 9090 {
		t.Fatalf(
			"expected port %d, got %d",
			9090,
			config.Server.Port,
		)
	}
}

func TestValidate_ReturnsConfig(t *testing.T) {
	t.Parallel()

	expected := testConfig(t)

	config, err := expected.Validate()
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	assertConfigEqual(t, expected, config)
}

func TestValidate_ReturnsErrorWhenMachineNameIsEmpty(t *testing.T) {
	t.Parallel()

	config := testConfig(t)
	config.Machine.Name = ""

	if _, err := config.Validate(); err == nil {
		t.Fatal("expected error")
	}
}

func TestValidate_ReturnsErrorWhenServerPortIsNotPositive(t *testing.T) {
	t.Parallel()

	config := testConfig(t)
	config.Server.Port = 0

	if _, err := config.Validate(); err == nil {
		t.Fatal("expected error")
	}
}

func TestValidate_ReturnsErrorWhenNoCoffeeTypesConfigured(t *testing.T) {
	t.Parallel()

	config := testConfig(t)
	config.Coffee.Types = nil

	if _, err := config.Validate(); err == nil {
		t.Fatal("expected error")
	}
}

func TestValidate_ReturnsErrorWhenCoffeeNameIsEmpty(t *testing.T) {
	t.Parallel()

	config := testConfig(t)
	config.Coffee.Types[0].Name = ""

	if _, err := config.Validate(); err == nil {
		t.Fatal("expected error")
	}
}

func TestValidate_ReturnsErrorWhenCoffeeDurationIsNotPositive(t *testing.T) {
	t.Parallel()

	config := testConfig(t)
	config.Coffee.Types[0].Duration = 0

	if _, err := config.Validate(); err == nil {
		t.Fatal("expected error")
	}
}

func testConfig(t *testing.T) Config {
	t.Helper()

	return Config{
		Machine: MachineConfig{
			Name: "BrewMaster 3000",
		},
		Server: ServerConfig{
			Port: 8080,
		},
		Coffee: CoffeeConfig{
			Types: []CoffeeTypeConfig{
				{
					Name:     "ESPRESSO",
					Duration: Duration(10 * time.Second),
				},
			},
		},
	}
}

func writeConfig(t *testing.T, config Config) string {
	t.Helper()

	path := filepath.Join(t.TempDir(), "config.json")
	data := fmt.Sprintf(`{
		  "machine": {
			"name": %q
		  },
		  "server": {
			"port": %d
		  },
		  "coffee": {
			"types": [
			  {
				"name": %q,
				"duration": %q
			  }
			]
		  }
		}`,
		config.Machine.Name,
		config.Server.Port,
		config.Coffee.Types[0].Name,
		time.Duration(config.Coffee.Types[0].Duration),
	)
	if err := os.WriteFile(path, []byte(data), 0644); err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	return path
}

func writeTestConfig(t *testing.T) string {
	t.Helper()

	return writeConfig(t, testConfig(t))
}

func assertConfigEqual(
	t *testing.T,
	expected Config,
	actual Config,
) {
	t.Helper()

	if expected.Machine != actual.Machine {
		t.Fatalf(
			"expected machine %+v, got %+v",
			expected.Machine,
			actual.Machine,
		)
	}
	if expected.Server != actual.Server {
		t.Fatalf(
			"expected server %+v, got %+v",
			expected.Server,
			actual.Server,
		)
	}
	if !slices.Equal(
		expected.Coffee.Types,
		actual.Coffee.Types,
	) {
		t.Fatalf(
			"expected coffee types %v, got %v",
			expected.Coffee.Types,
			actual.Coffee.Types,
		)
	}
}
