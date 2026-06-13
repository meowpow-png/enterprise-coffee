package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

class CoffeeOrderEntityTest {

    @Test
    void should_ThrowNullPointerException_when_IdIsNull() {
        var thrown = catchThrowable(() -> new CoffeeOrderEntity(
                null,
                "ESPRESSO",
                CoffeeOrder.Status.PENDING
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_ThrowNullPointerException_when_TypeIsNull() {
        var thrown = catchThrowable(() -> new CoffeeOrderEntity(
                UUID.randomUUID(),
                null,
                CoffeeOrder.Status.PENDING
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_ThrowNullPointerException_when_StatusIsNull() {
        var thrown = catchThrowable(() -> new CoffeeOrderEntity(
                UUID.randomUUID(),
                "ESPRESSO",
                null
        ));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_CreateEntity_when_ValuesAreProvided() {
        var id = UUID.randomUUID();
        var type = "ESPRESSO";
        var status = CoffeeOrder.Status.PENDING;

        var entity = new CoffeeOrderEntity(id, type, status);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getType()).isEqualTo(type);
        assertThat(entity.getStatus()).isEqualTo(status);
    }
}
