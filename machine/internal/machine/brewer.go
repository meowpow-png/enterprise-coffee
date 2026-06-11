package machine

import (
	"fmt"
	"log"
	"time"
)

// Brewer simulates coffee brewing.
type Brewer struct {
	duration time.Duration
}

// NewBrewer creates a new brewer.
func NewBrewer(duration time.Duration) *Brewer {
	if duration <= 0 {
		panic("duration must be positive")
	}
	return &Brewer{
		duration: duration,
	}
}

// Process processes a brewing operation.
func (b *Brewer) Process(brew *Brew) {
	log.Printf(
		"started brewing %s (estimated time: %s)",
		brew.Coffee(),
		b.duration,
	)
	fmt.Println()
	step := b.duration / 10

	for progress := 10; progress <= 100; progress += 10 {
		time.Sleep(step)
		brew.SetProgress(progress)

		log.Printf("%s... %d%%", brew.Coffee(), progress)
	}
	// give clients a chance to observe 100%
	time.Sleep(step)

	fmt.Println()
	log.Printf("completed brewing %s", brew.Coffee())
}
