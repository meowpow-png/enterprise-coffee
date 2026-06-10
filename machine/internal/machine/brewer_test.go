package machine

import (
	"testing"
	"time"
)

func TestNewBrewer_InitializesDuration(t *testing.T) {
	t.Parallel()

	duration := 10 * time.Second
	brewer := NewBrewer(duration)

	if brewer.duration != duration {
		t.Fatalf("expected duration %s, got %s", duration, brewer.duration)
	}
}

func TestNewBrewer_PanicsWhenDurationIsNotPositive(t *testing.T) {
	t.Parallel()

	defer func() {
		if recover() == nil {
			t.Fatal("expected panic")
		}
	}()
	NewBrewer(0)
}

func TestProcess_UpdatesProgressToOneHundred(t *testing.T) {
	t.Parallel()

	brewer := NewBrewer(10 * time.Millisecond)
	brew := NewBrew("ESPRESSO")

	brewer.Process(brew)

	if brew.Progress() != 100 {
		t.Fatalf("expected progress 100, got %d", brew.Progress())
	}
}
