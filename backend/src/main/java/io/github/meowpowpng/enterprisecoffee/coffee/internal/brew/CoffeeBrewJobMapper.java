package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew;

import io.github.meowpowpng.enterprisecoffee.coffee.internal.order.CoffeeOrder;

/**
 * Maps coffee brewing jobs
 * between domain and persistence models.
 */
final class CoffeeBrewJobMapper {

    private CoffeeBrewJobMapper() {}

    /**
     * Converts a coffee-brewing job into a persistence entity.
     *
     * @param job coffee-brewing job to convert
     *
     * @throws CoffeeBrewJobMappingException if the job
     * cannot be converted to a persistence entity
     */
    static CoffeeBrewJobEntity toEntity(CoffeeBrewJob job) {
        try {
            return new CoffeeBrewJobEntity(
                    job.orderId().value(),
                    job.id().value(),
                    job.status(),
                    job.progress().value()
            );
        }
        catch (RuntimeException e) {
            var message = "Failed to map job to entity";
            throw new CoffeeBrewJobMappingException(message, e);
        }
    }

    /**
     * Reconstructs a coffee-brewing job from a persistence entity.
     *
     * @param entity persistence entity to convert
     *
     * @throws CoffeeBrewJobMappingException if the entity
     * cannot be converted to a coffee-brewing job
     */
    static CoffeeBrewJob toDomain(CoffeeBrewJobEntity entity) {
        try {
            return CoffeeBrewJob.restore(
                    new CoffeeBrewJob.Identifier(entity.getId()),
                    new CoffeeOrder.Identifier(entity.getOrderId()),
                    entity.getStatus(),
                    entity.getProgress()
            );
        }
        catch (RuntimeException e) {
            var message = "Failed to map entity to job";
            throw new CoffeeBrewJobMappingException(message, e);
        }
    }
}
