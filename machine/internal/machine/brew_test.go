package machine

import "testing"

func TestNewBrew_InitializesCoffee(t *testing.T) {
	t.Parallel()

	coffee := CoffeeType("ESPRESSO")
	brew := NewBrew(coffee)

	if brew.Coffee() != coffee {
		t.Fatalf("expected coffee %q, got %q", coffee, brew.Coffee())
	}
}

func TestNewBrew_InitializesProgress(t *testing.T) {
	t.Parallel()

	brew := NewBrew("ESPRESSO")

	if brew.Progress() != 0 {
		t.Fatalf("expected progress 0, got %d", brew.Progress())
	}
}

func TestSetProgress_UpdatesProgress(t *testing.T) {
	t.Parallel()

	brew := NewBrew("ESPRESSO")
	brew.SetProgress(50)

	if brew.Progress() != 50 {
		t.Fatalf("expected progress 50, got %d", brew.Progress())
	}
}

func TestSetProgress_PanicsWhenNegative(t *testing.T) {
	t.Parallel()

	brew := NewBrew("ESPRESSO")

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	brew.SetProgress(-1)
}

func TestSetProgress_PanicsWhenGreaterThanHundred(t *testing.T) {
	t.Parallel()

	brew := NewBrew("ESPRESSO")

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	brew.SetProgress(101)
}
