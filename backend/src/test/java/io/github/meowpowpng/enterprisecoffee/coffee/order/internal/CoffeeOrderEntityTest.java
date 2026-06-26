package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

class CoffeeOrderEntityTest {

    private static final TestClock CLOCK = TestClock.create();

    @Test
    @DisplayName("Should throw NullPointerException when id is null")
    void should_ThrowNullPointerException_when_IdIsNull() {
        var thrown = catchThrowable(() -> new CoffeeOrderEntity(
                null,
                "ESPRESSO",
                CoffeeOrder.Status.PENDING,
                CLOCK.instant()
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when type is null")
    void should_ThrowNullPointerException_when_TypeIsNull() {
        var thrown = catchThrowable(() -> new CoffeeOrderEntity(
                UUID.randomUUID(),
                null,
                CoffeeOrder.Status.PENDING,
                CLOCK.instant()
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when status is null")
    void should_ThrowNullPointerException_when_StatusIsNull() {
        var thrown = catchThrowable(() -> new CoffeeOrderEntity(
                UUID.randomUUID(),
                "ESPRESSO",
                null,
                CLOCK.instant()
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when createdAt is null")
    void should_ThrowNullPointerException_when_CreatedAtIsNull() {
        var thrown = catchThrowable(() -> new CoffeeOrderEntity(
                UUID.randomUUID(),
                "ESPRESSO",
                CoffeeOrder.Status.PENDING,
                null
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should create entity when values are provided")
    void should_CreateEntity_when_ValuesAreProvided() {
        var id = UUID.randomUUID();
        var type = "ESPRESSO";
        var status = CoffeeOrder.Status.PENDING;
        var createdAt = CLOCK.instant();

        var entity = new CoffeeOrderEntity(id, type, status, createdAt);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getType()).isEqualTo(type);
        assertThat(entity.getStatus()).isEqualTo(status);
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
    }
}
