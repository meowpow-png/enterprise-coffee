package io.github.meowpowpng.enterprisecoffee.coffee.internal.job;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

import org.jspecify.annotations.NullUnmarked;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@NullUnmarked
@SuppressWarnings("unused")
@Table(name = "coffee_job")
public class CoffeeJobEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoffeeJob.Status status;

    @Column(nullable = false)
    private int progress;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    protected CoffeeJobEntity() {}

    public CoffeeJobEntity(UUID id, UUID orderId, CoffeeJob.Status status, int progress) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.progress = progress;
    }

    UUID getId() {
        return id;
    }

    UUID getOrderId() {
        return orderId;
    }

    CoffeeJob.Status getStatus() {
        return status;
    }

    int getProgress() {
        return progress;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    Instant getUpdatedAt() {
        return updatedAt;
    }
}
