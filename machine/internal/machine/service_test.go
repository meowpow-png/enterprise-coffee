package machine

import (
	"errors"
	"testing"
	"time"

	"github.com/meowpow-png/enterprise-coffee/machine/internal/config"
)

func TestNewService_InitializesBrewers(t *testing.T) {
	t.Parallel()

	coffees := []config.CoffeeTypeConfig{
		{
			Name:     "ESPRESSO",
			Duration: config.Duration(time.Second),
		},
		{
			Name:     "AMERICANO",
			Duration: config.Duration(2 * time.Second),
		},
	}
	service := NewService(coffees)

	if len(service.brewers) != len(coffees) {
		t.Fatalf(
			"expected %d brewers, got %d",
			len(coffees),
			len(service.brewers),
		)
	}
	if _, exists := service.brewers["ESPRESSO"]; !exists {
		t.Fatal("expected ESPRESSO brewer")
	}
	if _, exists := service.brewers["AMERICANO"]; !exists {
		t.Fatal("expected AMERICANO brewer")
	}
}

func TestNewService_PanicsWhenNoCoffeeTypesConfigured(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewService(nil)
}

func TestStatus_ReturnsReadyWhenNotBrewing(t *testing.T) {
	t.Parallel()

	service := NewService(testEspressoConfig(t))

	if service.Status() != StatusReady {
		t.Fatalf("expected status %q, got %q", StatusReady, service.Status())
	}
}

func TestStatus_ReturnsBrewingWhenBrewing(t *testing.T) {
	t.Parallel()

	service := NewService(testEspressoConfig(t))
	service.brew = NewBrew("ESPRESSO")

	if service.Status() != StatusBrewing {
		t.Fatalf("expected status %q, got %q", StatusBrewing, service.Status())
	}
}

func TestProgress_ReturnsZeroWhenNotBrewing(t *testing.T) {
	t.Parallel()

	service := NewService(testEspressoConfig(t))
	coffee, progress := service.Progress()

	if coffee != "" {
		t.Fatalf("expected empty coffee type, got %q", coffee)
	}
	if progress != 0 {
		t.Fatalf("expected progress 0, got %d", progress)
	}
}

func TestBrew_StartsBrewingOperation(t *testing.T) {
	t.Parallel()

	service := NewService(testEspressoConfig(t))

	if err := service.Brew("ESPRESSO"); err != nil {
		t.Fatalf("expected brewing to start, got %v", err)
	}
	if service.Status() != StatusBrewing {
		t.Fatalf("expected status %q, got %q", StatusBrewing, service.Status())
	}
}

func TestBrew_ReturnsErrInvalidCoffeeTypeWhenUnsupported(t *testing.T) {
	t.Parallel()

	service := NewService(testEspressoConfig(t))

	if err := service.Brew("AMERICANO"); !errors.Is(err, ErrInvalidCoffeeType) {
		t.Fatalf(
			"expected error %q, got %v",
			ErrInvalidCoffeeType,
			err,
		)
	}
}

func TestBrew_ReturnsErrMachineBusyWhenAlreadyBrewing(t *testing.T) {
	t.Parallel()

	service := NewService(testEspressoConfig(t))

	if err := service.Brew("ESPRESSO"); err != nil {
		t.Fatalf("expected first brew to start, got %v", err)
	}
	if err := service.Brew("ESPRESSO"); !errors.Is(err, ErrMachineBusy) {
		t.Fatalf(
			"expected error %q, got %v",
			ErrMachineBusy,
			err,
		)
	}
}

func testEspressoConfig(t *testing.T) []config.CoffeeTypeConfig {
	t.Helper()

	return []config.CoffeeTypeConfig{
		{
			Name:     "ESPRESSO",
			Duration: config.Duration(time.Second),
		},
	}
}
