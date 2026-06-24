package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrder;
import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.JpaCoffeeOrderRepository;
import io.github.meowpowpng.enterprisecoffee.support.JpaIntegrationTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JpaIntegrationTest
@Import({
        JpaCoffeeOrderRepository.class,
        JpaCoffeeJobRepository.class
})
class JpaCoffeeJobRepositoryTest {

    @Autowired
    private JpaCoffeeJobRepository jobRepository;

    @Autowired
    private JpaCoffeeOrderRepository orderRepository;

    @Nested
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Should persist coffee job when job is created")
        void should_PersistCoffeeJob_when_JobIsCreated() {
            var order = CoffeeOrder.create(
                    new CoffeeType("ESPRESSO")
            );
            var job = CoffeeJob.create(order.id());

            orderRepository.save(order);
            jobRepository.create(job);

            var result = jobRepository.findById(job.id());
            assertThat(result).contains(job);
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTests {

        @Autowired
        private EntityManager entityManager;

        @Test
        @DisplayName("Should update coffee job when job exists")
        void should_UpdateCoffeeJob_when_JobExists() {
            var order = CoffeeOrder.create(
                    new CoffeeType("ESPRESSO")
            );
            var job = CoffeeJob.create(order.id());

            orderRepository.save(order);
            jobRepository.create(job);

            job.start();
            job.updateProgress(Progress.of(50));

            jobRepository.update(job);

            entityManager.flush();
            entityManager.clear();

            var result = jobRepository.findById(job.id());
            assertThat(result).contains(job);
        }
    }

    @Nested
    @DisplayName("find")
    class FindTests {

        @Test
        @DisplayName("Should return coffee job when identifier exists")
        void should_ReturnCoffeeJob_when_IdentifierExists() {
            var order1 = CoffeeOrder.create(
                    new CoffeeType("ESPRESSO")
            );
            var order2 = CoffeeOrder.create(
                    new CoffeeType("LATTE")
            );
            orderRepository.save(order1);
            orderRepository.save(order2);

            var espressoJob = CoffeeJob.create(order1.id());
            var latteJob = CoffeeJob.create(order2.id());

            jobRepository.create(espressoJob);
            jobRepository.create(latteJob);

            var result = jobRepository.findById(latteJob.id());
            assertThat(result).contains(latteJob);
        }

        @Test
        @DisplayName("Should return empty result when identifier does not exist")
        void should_ReturnEmptyResult_when_IdentifierDoesNotExist() {
            var id = CoffeeJob.Id.generate();
            assertThat(jobRepository.findById(id)).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTests {

        @Test
        @DisplayName("Should delete coffee jobs not updated since cutoff")
        void should_DeleteCoffeeJobsNotUpdatedSinceCutoff() {
            var time = Instant.now().plusSeconds(10);
            var _deleted = jobRepository.deleteNotUpdatedSince(time);
            assertThat(_deleted).isZero();

            var order = CoffeeOrder.create(
                    new CoffeeType("ESPRESSO")
            );
            var job = CoffeeJob.create(order.id());

            orderRepository.save(order);
            jobRepository.create(job);

            var cutoff = Instant.now().plusSeconds(10);
            var deleted = jobRepository.deleteNotUpdatedSince(cutoff);

            assertThat(deleted).isEqualTo(1);
            assertThat(jobRepository.findById(job.id())).isEmpty();
        }

        @Test
        @DisplayName("Should return deleted coffee job count when jobs are deleted")
        void should_ReturnDeletedCoffeeJobCount_when_JobsAreDeleted() {
            var espressoOrder = CoffeeOrder.create(
                    new CoffeeType("ESPRESSO")
            );
            var latteOrder = CoffeeOrder.create(
                    new CoffeeType("LATTE")
            );
            orderRepository.save(espressoOrder);
            orderRepository.save(latteOrder);

            var espressoJob = CoffeeJob.create(espressoOrder.id());
            var latteJob = CoffeeJob.create(latteOrder.id());

            jobRepository.create(espressoJob);
            jobRepository.create(latteJob);

            var cutoff = Instant.now().plusSeconds(10);
            var deleted = jobRepository.deleteNotUpdatedSince(cutoff);

            assertThat(deleted).isEqualTo(2);
        }
    }
}
