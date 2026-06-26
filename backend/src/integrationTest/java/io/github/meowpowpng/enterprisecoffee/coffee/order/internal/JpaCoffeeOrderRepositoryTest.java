package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.support.JpaIntegrationTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            var order = TestCoffeeOrder.createOrder();
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
            var espresso = TestCoffeeOrder.createOrder("ESPRESSO");
            var latte = TestCoffeeOrder.createOrder("LATTE");

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
    }
}
