package io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.github.meowpowpng.enterprisecoffee.coffee.order.internal.job.CoffeeJobTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoffeeJobMapperTest {

    @Nested
    @DisplayName("toEntity")
    class ToEntityMethodTests {

        @Test
        @DisplayName("Should map job to entity when job is valid")
        void should_MapJobToEntity_when_JobIsValid() {
            var job = validCoffeeJob();
            var entity = CoffeeJobMapper.toEntity(job);

            assertThat(entity.getId()).isEqualTo(job.id().value());
            assertThat(entity.getOrderId()).isEqualTo(job.orderId().value());
            assertThat(entity.getStatus()).isEqualTo(job.status());
            assertThat(entity.getProgress()).isEqualTo(job.progress().value());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw CoffeeJobMappingException when job cannot be mapped")
        void should_ThrowCoffeeJobMappingException_when_JobCannotBeMapped() {
            assertThatThrownBy(() -> CoffeeJobMapper.toEntity(null))
                    .isInstanceOf(CoffeeJobMappingException.class);
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomainMethodTests {

        @Test
        @DisplayName("Should map entity to job when entity is valid")
        void should_MapEntityToJob_when_EntityIsValid() {
            var entity = new CoffeeJobEntity(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    CoffeeJob.Status.IN_PROGRESS,
                    42
            );
            var job = CoffeeJobMapper.toDomain(entity);

            assertThat(job.id().value()).isEqualTo(entity.getId());
            assertThat(job.orderId().value()).isEqualTo(entity.getOrderId());
            assertThat(job.status()).isEqualTo(entity.getStatus());
            assertThat(job.progress().value()).isEqualTo(entity.getProgress());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        @DisplayName("Should throw CoffeeJobMappingException when entity cannot be mapped")
        void should_ThrowCoffeeJobMappingException_when_EntityCannotBeMapped() {
            assertThatThrownBy(() -> CoffeeJobMapper.toDomain(null))
                    .isInstanceOf(CoffeeJobMappingException.class);
        }
    }
}
