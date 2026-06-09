package config

import (
	"flag"
	"testing"
)

func TestLoadFlags_ReturnsDefaultFlags(t *testing.T) {
	t.Parallel()

	fs := flag.NewFlagSet("test", flag.ContinueOnError)
	flags := loadFlags(fs, nil)

	if flags.Port != defaultPort {
		t.Fatalf(
			"expected port %d, got %d",
			defaultPort,
			flags.Port,
		)
	}
	if flags.ConfigFile != defaultConfigFile {
		t.Fatalf(
			"expected config file %q, got %q",
			defaultConfigFile,
			flags.ConfigFile,
		)
	}
}

func TestLoadFlags_ReturnsParsedFlags(t *testing.T) {
	t.Parallel()

	flags := loadFlags(
		flag.NewFlagSet("test", flag.ContinueOnError),
		[]string{
			"--port=9090",
			"--config=custom.json",
		},
	)
	if flags.Port != 9090 {
		t.Fatalf("expected port %d, got %d", 9090, flags.Port)
	}
	if flags.ConfigFile != "custom.json" {
		t.Fatalf(
			"expected config file %q, got %q",
			"custom.json",
			flags.ConfigFile,
		)
	}
}

func TestLoadFlags_PanicsWhenPortIsNotPositive(t *testing.T) {
	t.Parallel()

	fs := flag.NewFlagSet("test", flag.ContinueOnError)

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	loadFlags(fs, []string{"--port=0"})
}

func TestLoadFlags_PanicsWhenConfigFileIsEmpty(t *testing.T) {
	t.Parallel()

	fs := flag.NewFlagSet("test", flag.ContinueOnError)

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	loadFlags(fs, []string{"--config="})
}

func TestIsFlagSet_ReturnsFalseWhenFlagIsNotSet(t *testing.T) {
	old := flag.CommandLine
	defer func() {
		flag.CommandLine = old
	}()
	fs := flag.NewFlagSet("test", flag.ContinueOnError)
	flag.CommandLine = fs

	if err := fs.Parse(nil); err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if IsFlagSet("port") {
		t.Fatal("expected flag to be unset")
	}
}

func TestIsFlagSet_ReturnsTrueWhenFlagIsSet(t *testing.T) {
	old := flag.CommandLine
	defer func() {
		flag.CommandLine = old
	}()
	fs := flag.NewFlagSet("test", flag.ContinueOnError)
	flag.CommandLine = fs

	fs.Int("port", defaultPort, "")

	if err := fs.Parse([]string{"--port=9090"}); err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if !IsFlagSet("port") {
		t.Fatal("expected flag to be set")
	}
}
