package io.github.meowpowpng.enterprisecoffee.coffee.internal.order;

import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

/**
 * JPA-backed coffee order repository.
 */
@Repository
public class JpaCoffeeOrderRepository implements CoffeeOrderRepository {

    private final JpaCoffeeOrderCrudRepository repository;

    JpaCoffeeOrderRepository(JpaCoffeeOrderCrudRepository repository) {
        Objects.requireNonNull(repository, "repository must not be null");
        this.repository = repository;
    }

    @Override
    public void save(CoffeeOrder order) {
        Objects.requireNonNull(order, "order");
        try {
            var entity = CoffeeOrderMapper.toEntity(order);
            repository.save(entity);
        }
        catch (CoffeeOrderMappingException e) {
            throw e;
        }
        catch (RuntimeException e) {
            var message = "failed to save coffee order";
            throw new CoffeeOrderPersistenceException(message, e);
        }
    }

    @Override
    public Optional<CoffeeOrder> findById(CoffeeOrder.Id id) {
        Objects.requireNonNull(id, "id");
        try {
            return repository.findById(id.value())
                    .map(CoffeeOrderMapper::toDomain);
        }
        catch (CoffeeOrderMappingException e) {
            throw e;
        }
        catch (RuntimeException e) {
            var message = "failed to find coffee order";
            throw new CoffeeOrderPersistenceException(message, e);
        }
    }
}
