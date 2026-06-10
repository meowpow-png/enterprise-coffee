package io.github.meowpowpng.enterprisecoffee.internal.brew;

import java.util.Optional;

public interface CoffeeBrewJobRepository {

    CoffeeBrewJob save(CoffeeBrewJob job);

    Optional<CoffeeBrewJob> findById(CoffeeBrewJob.Identifier id);
}
