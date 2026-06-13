package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrderTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class CoffeeOrderMapperTest {

    @Nested
    @DisplayName("toEntity")
    class ToEntityMethodTests {

        @Test
        void should_MapOrderToEntity_when_OrderIsValid() {
            var order = validCoffeeOrder();
            var entity = CoffeeOrderMapper.toEntity(order);

            assertThat(entity.getId()).isEqualTo(order.id().value());
            assertThat(entity.getType()).isEqualTo(order.type().value());
            assertThat(entity.getStatus()).isEqualTo(order.status());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void should_ThrowCoffeeOrderMappingException_when_OrderCannotBeMapped() {
            assertThatThrownBy(() -> CoffeeOrderMapper.toEntity(null))
                    .isInstanceOf(CoffeeOrderMappingException.class);
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomainMethodTests {

        @Test
        void should_MapEntityToOrder_when_EntityIsValid() {
            var entity = new CoffeeOrderEntity(
                    UUID.randomUUID(),
                    "ESPRESSO",
                    CoffeeOrder.Status.PENDING
            );
            var order = CoffeeOrderMapper.toDomain(entity);

            assertThat(order.id().value()).isEqualTo(entity.getId());
            assertThat(order.type().value()).isEqualTo(entity.getType());
            assertThat(order.status()).isEqualTo(entity.getStatus());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void should_ThrowCoffeeOrderMappingException_when_EntityCannotBeMapped() {
            assertThatThrownBy(() -> CoffeeOrderMapper.toDomain(null))
                    .isInstanceOf(CoffeeOrderMappingException.class);
        }
    }
}
