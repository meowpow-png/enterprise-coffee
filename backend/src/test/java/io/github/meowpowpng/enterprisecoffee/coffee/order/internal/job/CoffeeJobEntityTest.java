package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

class CoffeeJobEntityTest {

    @Test
    @DisplayName("Should create entity when values are provided")
    void should_CreateEntity_when_ValuesAreProvided() {
        var id = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        var status = CoffeeJob.Status.PENDING;
        var progress = 42;

        var entity = new CoffeeJobEntity(
                id,
                orderId,
                status,
                progress
        );
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getOrderId()).isEqualTo(orderId);
        assertThat(entity.getStatus()).isEqualTo(status);
        assertThat(entity.getProgress()).isEqualTo(progress);
    }

    @Test
    @DisplayName("Should throw NullPointerException when id is null")
    void should_ThrowNullPointerException_when_IdIsNull() {
        var thrown = catchThrowable(() -> new CoffeeJobEntity(
                null,
                UUID.randomUUID(),
                CoffeeJob.Status.PENDING,
                0
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when order id is null")
    void should_ThrowNullPointerException_when_OrderIdIsNull() {
        var thrown = catchThrowable(() -> new CoffeeJobEntity(
                UUID.randomUUID(),
                null,
                CoffeeJob.Status.PENDING,
                0
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when status is null")
    void should_ThrowNullPointerException_when_StatusIsNull() {
        var thrown = catchThrowable(() -> new CoffeeJobEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                0
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }
}
