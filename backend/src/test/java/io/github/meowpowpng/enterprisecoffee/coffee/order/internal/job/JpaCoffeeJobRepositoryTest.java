package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JpaCoffeeJobRepositoryTest {

    @Mock
    private JpaCoffeeJobCrudRepository repository;

    @Mock
    private EntityManager entityManager;

    @Nested
    @DisplayName("constructor")
    class ConstructorTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when repository is null")
        void should_ThrowNullPointerException_when_RepositoryIsNull() {
            assertThatThrownBy(() -> new JpaCoffeeJobRepository(null, entityManager))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when entity manager is null")
        void should_ThrowNullPointerException_when_EntityManagerIsNull() {
            assertThatThrownBy(() -> new JpaCoffeeJobRepository(repository, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class CreateMethodTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when job is null")
        void should_ThrowNullPointerException_when_JobIsNull() {
            assertThatThrownBy(() -> jobRepository().create(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should propagate CoffeeJobMappingException when creating coffee job")
        void should_PropagateCoffeeJobMappingException_when_CreatingCoffeeJob() {
            var exception = new CoffeeJobMappingException(
                    "mapping failed",
                    new RuntimeException("boom")
            );
            Mockito.doThrow(exception).when(entityManager).persist(Mockito.any());

            assertThatThrownBy(() -> jobRepository().create(TestCoffeeJob.create()))
                    .isInstanceOf(CoffeeJobMappingException.class);
        }

        @Test
        @DisplayName("Should throw CoffeeJobPersistenceException when coffee job already exists")
        void should_ThrowCoffeeJobPersistenceException_when_CoffeeJobAlreadyExists() {
            Mockito.doThrow(new EntityExistsException("already exists"))
                    .when(entityManager)
                    .persist(Mockito.any());

            assertThatThrownBy(() -> jobRepository().create(TestCoffeeJob.create()))
                    .isInstanceOf(CoffeeJobPersistenceException.class);
        }

        @Test
        @DisplayName("Should throw CoffeeJobPersistenceException when creating coffee job fails")
        void should_ThrowCoffeeJobPersistenceException_when_CreatingCoffeeJobFails() {
            Mockito.doThrow(new RuntimeException("boom"))
                    .when(entityManager)
                    .persist(Mockito.any());

            assertThatThrownBy(() -> jobRepository().create(TestCoffeeJob.create()))
                    .isInstanceOf(CoffeeJobPersistenceException.class);
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateMethodTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when job is null")
        void should_ThrowNullPointerException_when_JobIsNull() {
            assertThatThrownBy(() -> jobRepository().update(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should throw CoffeeJobPersistenceException when coffee job does not exist")
        void should_ThrowCoffeeJobPersistenceException_when_CoffeeJobDoesNotExist() {
            var job = TestCoffeeJob.create();

            Mockito.when(repository.update(
                    job.id().value(),
                    job.status(),
                    job.progress().value()
            )).thenReturn(0);

            assertThatThrownBy(() -> jobRepository().update(job))
                    .isInstanceOf(CoffeeJobPersistenceException.class);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdMethodTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when id is null")
        void should_ThrowNullPointerException_when_IdIsNull() {
            assertThatThrownBy(() -> jobRepository().findById(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should propagate CoffeeJobMappingException when finding coffee job by id")
        void should_PropagateCoffeeJobMappingException_when_FindingCoffeeJobById() {
            var job = TestCoffeeJob.create();
            var exception = new CoffeeJobMappingException(
                    "mapping failed",
                    new RuntimeException("boom")
            );
            Mockito.when(repository.findById(job.id().value()))
                    .thenThrow(exception);

            assertThatThrownBy(() -> jobRepository().findById(job.id()))
                    .isInstanceOf(CoffeeJobMappingException.class);
        }

        @Test
        @DisplayName("Should throw CoffeeJobPersistenceException when finding coffee job by id fails")
        void should_ThrowCoffeeJobPersistenceException_when_FindingCoffeeJobByIdFails() {
            var job = TestCoffeeJob.create();

            Mockito.when(repository.findById(job.id().value()))
                    .thenThrow(new RuntimeException("boom"));

            assertThatThrownBy(() -> jobRepository().findById(job.id()))
                    .isInstanceOf(CoffeeJobPersistenceException.class);
        }
    }

    @Nested
    @DisplayName("deleteNotUpdatedSince")
    class DeleteNotUpdatedSinceMethodTests {

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw NullPointerException when cutoff is null")
        void should_ThrowNullPointerException_when_CutoffIsNull() {
            assertThatThrownBy(() -> jobRepository().deleteNotUpdatedSince(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should throw CoffeeJobPersistenceException when deleting coffee jobs fails")
        void should_ThrowCoffeeJobPersistenceException_when_DeletingCoffeeJobsFails() {
            var cutoff = Instant.parse("2025-01-01T00:00:00Z");

            Mockito.when(repository.deleteByUpdatedAtBefore(cutoff))
                    .thenThrow(new RuntimeException("boom"));

            assertThatThrownBy(() -> jobRepository().deleteNotUpdatedSince(cutoff))
                    .isInstanceOf(CoffeeJobPersistenceException.class);
        }
    }

    private JpaCoffeeJobRepository jobRepository() {
        return new JpaCoffeeJobRepository(repository, entityManager);
    }
}
