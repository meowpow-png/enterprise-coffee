package io.github.meowpowpng.enterprisecoffee.coffee.order.internal;

import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;

/**
 * Maps coffee orders between
 * domain and persistence models.
 */
final class CoffeeOrderMapper {

    private CoffeeOrderMapper() {}

    /**
     * Converts a coffee order into a persistence entity.
     *
     * @param order coffee order to convert
     *
     * @throws CoffeeOrderMappingException if the order
     * cannot be converted to a persistence entity
     */
    static CoffeeOrderEntity toEntity(CoffeeOrder order) {
        try {
            return new CoffeeOrderEntity(
                    order.id().value(),
                    order.type().value(),
                    order.status()
            );
        }
        catch (RuntimeException e) {
            var message = "Failed to map order to entity";
            throw new CoffeeOrderMappingException(message, e);
        }
    }

    /**
     * Reconstructs a coffee order from a persistence entity.
     *
     * @param entity persistence entity to convert
     *
     * @throws CoffeeOrderMappingException if the entity
     * cannot be converted to a coffee order
     */
    static CoffeeOrder toDomain(CoffeeOrderEntity entity) {
        try {
            return CoffeeOrder.restore(
                    new CoffeeOrder.Id(entity.getId()),
                    new CoffeeType(entity.getType()),
                    entity.getStatus()
            );
        }
        catch (RuntimeException e) {
            var message = "Failed to map entity to order";
            throw new CoffeeOrderMappingException(message, e);
        }
    }
}
