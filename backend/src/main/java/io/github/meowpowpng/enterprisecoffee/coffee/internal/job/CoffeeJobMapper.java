package io.github.meowpowpng.enterprisecoffee.coffee.internal.job;

import io.github.meowpowpng.enterprisecoffee.coffee.order.internal.CoffeeOrder;

/**
 * Maps coffee jobs between
 * domain and persistence models.
 */
final class CoffeeJobMapper {

    private CoffeeJobMapper() {}

    /**
     * Converts a coffee job into a persistence entity.
     *
     * @param job coffee job to convert
     *
     * @throws CoffeeJobMappingException if the job
     * cannot be converted to a persistence entity
     */
    static CoffeeJobEntity toEntity(CoffeeJob job) {
        try {
            return new CoffeeJobEntity(
                    job.id().value(),
                    job.orderId().value(),
                    job.status(),
                    job.progress().value()
            );
        }
        catch (RuntimeException e) {
            var message = "Failed to map job to entity";
            throw new CoffeeJobMappingException(message, e);
        }
    }

    /**
     * Reconstructs a coffee job from a persistence entity.
     *
     * @param entity persistence entity to convert
     *
     * @throws CoffeeJobMappingException if the entity
     * cannot be converted to a coffee job
     */
    static CoffeeJob toDomain(CoffeeJobEntity entity) {
        try {
            return CoffeeJob.restore(
                    new CoffeeJob.Id(entity.getId()),
                    new CoffeeOrder.Id(entity.getOrderId()),
                    entity.getStatus(),
                    entity.getProgress()
            );
        }
        catch (RuntimeException e) {
            var message = "Failed to map entity to job";
            throw new CoffeeJobMappingException(message, e);
        }
    }
}
