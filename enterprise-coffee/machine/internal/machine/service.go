package machine

import "sync"

// Service manages coffee machine state.
type Service struct {
	mu     sync.RWMutex
	brew   *Brew
	brewer *Brewer
}

// NewService creates a new machine service.
func NewService(brewer *Brewer) *Service {
	return &Service{
		brewer: brewer,
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

	if s.brew != nil {
		s.mu.Unlock()
		return false
	}
	brew := NewBrew(coffee)
	s.brew = brew

	s.mu.Unlock()

	go func() {
		s.brewer.Process(brew)

		s.mu.Lock()
		defer s.mu.Unlock()

		if s.brew == brew {
			s.brew = nil
		}
	}()
	return true
}
