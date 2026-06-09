package machine

import (
	"sync"
	"time"

	"senthora.com/gatlingfx/enterprise-coffee/machine/internal/api/config"
)

// Service manages coffee machine state.
type Service struct {
	mu      sync.RWMutex
	brew    *Brew
	brewers map[CoffeeType]*Brewer
}

// NewService creates a new machine service.
func NewService(
	coffees []config.CoffeeTypeConfig,
) *Service {
	if len(coffees) == 0 {
		panic("coffees must not be empty")
	}
	brewers := make(map[CoffeeType]*Brewer)

	for _, coffee := range coffees {
		brewers[CoffeeType(coffee.Name)] =
			NewBrewer(time.Duration(coffee.Duration))
	}
	return &Service{
		brewers: brewers,
	}
}

// Status returns the current machine status.
func (s *Service) Status() Status {
	s.mu.RLock()
	defer s.mu.RUnlock()

	if s.brew != nil {
		return StatusBrewing
	}
	return StatusReady
}

// Progress returns the current brewing progress.
func (s *Service) Progress() (CoffeeType, int) {
	s.mu.RLock()
	defer s.mu.RUnlock()

	if s.brew == nil {
		return "", 0
	}
	return s.brew.Coffee(), s.brew.Progress()
}

// Brew attempts to start a brewing operation.
func (s *Service) Brew(coffee CoffeeType) bool {
	s.mu.Lock()

	brewer, exists := s.brewers[coffee]
	if !exists {
		s.mu.Unlock()
		return false
	}
	if s.brew != nil {
		s.mu.Unlock()
		return false
	}
	brew := NewBrew(coffee)
	s.brew = brew

	s.mu.Unlock()

	go func() {
		brewer.Process(brew)

		s.mu.Lock()
		defer s.mu.Unlock()

		if s.brew == brew {
			s.brew = nil
		}
	}()
	return true
}
