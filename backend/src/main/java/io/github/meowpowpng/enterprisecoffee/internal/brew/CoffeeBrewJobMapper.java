package io.github.meowpowpng.enterprisecoffee.internal.brew;

/**
 * Maps coffee brewing jobs
 * between domain and persistence models.
 */
final class CoffeeBrewJobMapper {

    private CoffeeBrewJobMapper() {}

    static CoffeeBrewJobEntity toEntity(CoffeeBrewJob job) {
        return new CoffeeBrewJobEntity(
                job.id().value(),
                job.status(),
                job.progress()
        );
    }

    static CoffeeBrewJob toDomain(CoffeeBrewJobEntity entity) {
        return CoffeeBrewJob.restore(
                new CoffeeBrewJob.Identifier(entity.getId()),
                entity.getStatus(),
                entity.getProgress()
        );
    }
}
