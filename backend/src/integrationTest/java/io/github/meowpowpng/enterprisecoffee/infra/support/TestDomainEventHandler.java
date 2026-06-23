package io.github.meowpowpng.enterprisecoffee.infra.support;

import org.springframework.context.event.EventListener;

public final class TestDomainEventHandler {

    private TestDomainEvent event;

    @EventListener
    public void handle(TestDomainEvent event) {
        this.event = event;
    }

    public TestDomainEvent lastHandledEvent() {
        return event;
    }
}
