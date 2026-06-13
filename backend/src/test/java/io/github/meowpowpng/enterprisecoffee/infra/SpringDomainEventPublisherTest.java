package io.github.meowpowpng.enterprisecoffee.infra;

import org.springframework.context.ApplicationEventPublisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("DataFlowIssue")
class SpringDomainEventPublisherTest {

    @Test
    @DisplayName("Should throw NullPointerException when publisher is null")
    void should_ThrowNullPointerException_when_PublisherIsNull() {
        assertThatThrownBy(() -> new SpringDomainEventPublisher(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when event is null")
    void should_ThrowNullPointerException_when_EventIsNull() {
        var publisher = new SpringDomainEventPublisher(
                Mockito.mock(ApplicationEventPublisher.class)
        );
        assertThatThrownBy(() -> publisher.publish(null))
                .isInstanceOf(NullPointerException.class);
    }
}
