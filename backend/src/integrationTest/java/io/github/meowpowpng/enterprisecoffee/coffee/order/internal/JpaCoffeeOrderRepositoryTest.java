package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.support.JpaIntegrationTest;
import io.github.meowpowpng.enterprisecoffee.support.TestClock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;

import org.awaitility.Durations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JpaIntegrationTest
@Import(JpaCoffeeOrderRepository.class)
class JpaCoffeeOrderRepositoryTest {

    @Autowired
    private JpaCoffeeOrderRepository repository;

    @Nested
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("Should persist coffee order when order is saved")
        void should_PersistCoffeeOrder_when_OrderIsSaved() {
            var order = TestCoffeeOrder.create();
            repository.save(order);

            var result = repository.findById(order.id());
            assertThat(result).contains(order);
        }
    }

    @Nested
    @DisplayName("find")
    class FindTests {

        @Autowired
        private EntityManager entityManager;

        @Test
        @DisplayName("Should return coffee order when identifier exists")
        void should_ReturnCoffeeOrder_when_IdentifierExists() {
            var espresso = TestCoffeeOrder.create("ESPRESSO");
            var latte = TestCoffeeOrder.create("LATTE");

            repository.save(espresso);
            repository.save(latte);

            entityManager.flush();
            entityManager.clear();

            var result = repository.findById(latte.id());
            assertThat(result).contains(latte);
        }

        @Test
        @DisplayName("Should return empty result when identifier does not exist")
        void should_ReturnEmptyResult_when_IdentifierDoesNotExist() {
            var id = CoffeeOrder.Id.generate();
            assertThat(repository.findById(id)).isEmpty();
        }

        @Test
        @DisplayName("Should return latest coffee orders when orders exist")
        void should_ReturnLatestCoffeeOrders_when_OrdersExist() {
            var clock = TestClock.create(
                    Instant.parse("2026-01-01T10:02:00Z")
            );
            var minusOneSecond = Durations.ONE_SECOND.negated();
            List<CoffeeOrder> orders = List.of(
                    TestCoffeeOrder.create("ESPRESSO", clock.instant()),
                    TestCoffeeOrder.create("LATTE", clock.advance(minusOneSecond)),
                    TestCoffeeOrder.create("CAPPUCCINO", clock.advance(minusOneSecond))
            );
            orders.forEach(order -> repository.save(order));

            entityManager.flush();

            var result = repository.findLatest(3);
            assertThat(result).containsExactlyElementsOf(orders);
        }

        @Test
        @DisplayName("Should respect limit when latest coffee orders are requested")
        void should_RespectLimit_when_LatestCoffeeOrdersAreRequested() {
            var clock = TestClock.create(
                    Instant.parse("2026-01-01T10:02:00Z")
            );
            var minusOneSecond = Durations.ONE_SECOND.negated();
            List<CoffeeOrder> orders = List.of(
                    TestCoffeeOrder.create("ESPRESSO", clock.instant()),
                    TestCoffeeOrder.create("LATTE", clock.advance(minusOneSecond)),
                    TestCoffeeOrder.create("CAPPUCCINO", clock.advance(minusOneSecond))
            );
            orders.forEach(repository::save);

            entityManager.flush();

            var result = repository.findLatest(2);

            assertThat(result).containsExactly(
                    orders.get(0),  // ESPRESSO
                    orders.get(1)   // LATTE
            );
        }

        @Test
        @DisplayName("Should return empty list when no coffee orders exist")
        void should_ReturnEmptyList_when_NoCoffeeOrdersExist() {
            assertThat(repository.findLatest(10)).isEmpty();
        }
    }
}
