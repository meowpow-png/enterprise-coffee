package health

import (
	"sync"
	"time"
)

// Service tracks application health.
type Service struct {
	mu          sync.RWMutex
	lastPulse   time.Time
	maxPulseAge time.Duration
}

// NewService creates a new health service.
func NewService(maxPulseAge time.Duration) *Service {
	if maxPulseAge <= 0 {
		panic("maxPulseAge must be positive")
	}
	return &Service{
		lastPulse:   time.Now(),
		maxPulseAge: maxPulseAge,
	}
}

// Pulse records a health heartbeat.
func (s *Service) Pulse() {
	s.mu.Lock()
	defer s.mu.Unlock()

	s.lastPulse = time.Now()
}

// Healthy reports whether the application is healthy.
func (s *Service) Healthy() bool {
	s.mu.RLock()
	defer s.mu.RUnlock()

	return time.Since(s.lastPulse) <= s.maxPulseAge
}
