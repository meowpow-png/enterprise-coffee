package config

import (
	"testing"
	"time"
)

func TestUnmarshalJSON_ParsesDuration(t *testing.T) {
	t.Parallel()

	var duration Duration

	if err := duration.UnmarshalJSON([]byte(`"10s"`)); err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if duration != Duration(10*time.Second) {
		t.Fatalf(
			"expected duration %s, got %s",
			10*time.Second,
			time.Duration(duration),
		)
	}
}

func TestUnmarshalJSON_ReturnsErrorWhenValueIsNotString(t *testing.T) {
	t.Parallel()

	var duration Duration
	err := duration.UnmarshalJSON([]byte(`10`))

	if err == nil {
		t.Fatal("expected error")
	}
}

func TestUnmarshalJSON_ReturnsErrorWhenDurationIsInvalid(t *testing.T) {
	t.Parallel()

	var duration Duration
	err := duration.UnmarshalJSON([]byte(`"invalid"`))

	if err == nil {
		t.Fatal("expected error")
	}
}
