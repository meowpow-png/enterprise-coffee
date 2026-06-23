package io.github.meowpowpng.enterprisecoffee.infra;

import io.github.meowpowpng.enterprisecoffee.common.DomainEventPublisher;
import io.github.meowpowpng.enterprisecoffee.infra.support.TestDomainEvent;
import io.github.meowpowpng.enterprisecoffee.infra.support.TestDomainEventHandler;
import io.github.meowpowpng.enterprisecoffee.support.IntegrationTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest(classes = {
        TestDomainEventHandler.class,
        SpringDomainEventPublisher.class
})
class SpringDomainEventPublisherTest {

    @Autowired
    private DomainEventPublisher publisher;

    @Autowired
    private TestDomainEventHandler handler;

    @Test
    @DisplayName("Should publish event when domain event is published")
    void should_PublishEvent_when_DomainEventIsPublished() {
        var event = new TestDomainEvent();

        publisher.publish(event);

        assertThat(handler.lastHandledEvent()).isSameAs(event);
    }
}
