package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JpaCoffeeOrderRepositoryTest {

    @Mock
    private JpaCoffeeOrderCrudRepository repository;

    @Nested
    @DisplayName("constructor")
    class ConstructorTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when repository is null")
        void should_ThrowNullPointerException_when_RepositoryIsNull() {
            assertThatThrownBy(() -> new JpaCoffeeOrderRepository(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("save")
    class SaveMethodTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when order is null")
        void should_ThrowNullPointerException_when_OrderIsNull() {
            var orderRepository = new JpaCoffeeOrderRepository(repository);

            assertThatThrownBy(() -> orderRepository.save(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should propagate CoffeeOrderMappingException when saving coffee order")
        void should_PropagateCoffeeOrderMappingException_when_SavingCoffeeOrder() {
            var orderRepository = new JpaCoffeeOrderRepository(repository);
            var order = TestCoffeeOrder.create();

            var exception = new CoffeeOrderMappingException(
                    "mapping failed",
                    new RuntimeException("boom")
            );
            Mockito.doThrow(exception).when(repository).save(Mockito.any());

            assertThatThrownBy(() -> orderRepository.save(order))
                    .isInstanceOf(CoffeeOrderMappingException.class);
        }

        @Test
        @DisplayName("Should throw CoffeeOrderPersistenceException when saving coffee order fails")
        void should_ThrowCoffeeOrderPersistenceException_when_SavingCoffeeOrderFails() {
            var orderRepository = new JpaCoffeeOrderRepository(repository);
            var order = TestCoffeeOrder.create();

            Mockito.doThrow(new RuntimeException("boom"))
                    .when(repository)
                    .save(Mockito.any());

            assertThatThrownBy(() -> orderRepository.save(order))
                    .isInstanceOf(CoffeeOrderPersistenceException.class);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdMethodTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when id is null")
        void should_ThrowNullPointerException_when_IdIsNull() {
            var orderRepository = new JpaCoffeeOrderRepository(repository);

            assertThatThrownBy(() -> orderRepository.findById(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should propagate CoffeeOrderMappingException when finding coffee order by id")
        void should_PropagateCoffeeOrderMappingException_when_FindingCoffeeOrderById() {
            var orderRepository = new JpaCoffeeOrderRepository(repository);
            var order = TestCoffeeOrder.create();

            var exception = new CoffeeOrderMappingException(
                    "mapping failed",
                    new RuntimeException("boom")
            );
            Mockito.when(repository.findById(order.id().value()))
                    .thenThrow(exception);

            assertThatThrownBy(() -> orderRepository.findById(order.id()))
                    .isInstanceOf(CoffeeOrderMappingException.class);
        }

        @Test
        @DisplayName("Should throw CoffeeOrderPersistenceException when finding coffee order by id fails")
        void should_ThrowCoffeeOrderPersistenceException_when_FindingCoffeeOrderByIdFails() {
            var orderRepository = new JpaCoffeeOrderRepository(repository);
            var order = TestCoffeeOrder.create();

            Mockito.when(repository.findById(order.id().value()))
                    .thenThrow(new RuntimeException("boom"));

            assertThatThrownBy(() -> orderRepository.findById(order.id()))
                    .isInstanceOf(CoffeeOrderPersistenceException.class);
        }
    }

    @Nested
    @DisplayName("findLatest")
    class FindLatestMethodTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when limit is less than one")
        void should_ThrowIllegalArgumentException_when_LimitIsLessThanOne() {
            var orderRepository = new JpaCoffeeOrderRepository(repository);

            assertThatThrownBy(() -> orderRepository.findLatest(0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should propagate CoffeeOrderMappingException when finding latest coffee orders")
        void should_PropagateCoffeeOrderMappingException_when_FindingLatestCoffeeOrders() {
            var orderRepository = new JpaCoffeeOrderRepository(repository);

            var exception = new CoffeeOrderMappingException(
                    "mapping failed",
                    new RuntimeException("boom")
            );
            Mockito.when(repository.findByOrderByCreatedAtDesc(Mockito.any()))
                    .thenThrow(exception);

            assertThatThrownBy(() -> orderRepository.findLatest(10))
                    .isInstanceOf(CoffeeOrderMappingException.class);
        }

        @Test
        @DisplayName("Should throw CoffeeOrderPersistenceException when finding latest coffee orders fails")
        void should_ThrowCoffeeOrderPersistenceException_when_FindingLatestCoffeeOrdersFails() {
            var orderRepository = new JpaCoffeeOrderRepository(repository);

            Mockito.when(repository.findByOrderByCreatedAtDesc(Mockito.any()))
                    .thenThrow(new RuntimeException("boom"));

            assertThatThrownBy(() -> orderRepository.findLatest(10))
                    .isInstanceOf(CoffeeOrderPersistenceException.class);
        }
    }
}
