package io.github.meowpowpng.enterprisecoffee.coffee.internal.order;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

import org.jspecify.annotations.NullUnmarked;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@NullUnmarked
@SuppressWarnings("unused")
@Table(name = "coffee_order")
class CoffeeOrderEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 32, updatable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32, updatable = false)
    private CoffeeOrder.Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected CoffeeOrderEntity() {}

    CoffeeOrderEntity(UUID id, String type, CoffeeOrder.Status status) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");

        this.id = id;
        this.type = type;
        this.status = status;
    }

    UUID getId() {
        return id;
    }

    String getType() {
        return type;
    }

    CoffeeOrder.Status getStatus() {
        return status;
    }

    Instant getCreatedAt() {
        return createdAt;
    }
}
