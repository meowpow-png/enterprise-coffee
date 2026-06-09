package machine

import "sync"

// Brew represents a coffee brewing operation.
type Brew struct {
	mu       sync.RWMutex
	coffee   CoffeeType
	progress int
}

// NewBrew creates a new brewing operation.
func NewBrew(coffee CoffeeType) *Brew {
	return &Brew{
		coffee:   coffee,
		progress: 0,
	}
}

// Coffee returns the coffee being brewed.
func (b *Brew) Coffee() CoffeeType {
	return b.coffee
}

// Progress returns the current brewing progress.
func (b *Brew) Progress() int {
	b.mu.RLock()
	defer b.mu.RUnlock()

	return b.progress
}

// SetProgress updates the brewing progress.
func (b *Brew) SetProgress(progress int) {
	b.mu.Lock()
	defer b.mu.Unlock()

	b.progress = progress
}
