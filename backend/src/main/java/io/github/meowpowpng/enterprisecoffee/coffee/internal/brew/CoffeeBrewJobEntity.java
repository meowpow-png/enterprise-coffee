package io.github.meowpowpng.enterprisecoffee.coffee.internal.brew;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.jspecify.annotations.NullUnmarked;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@NullUnmarked
@SuppressWarnings("unused")
@Table(name = "coffee_brew_job")
public class CoffeeBrewJobEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoffeeBrewJob.Status status;

    @Column(nullable = false)
    private int progress;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    protected CoffeeBrewJobEntity() {}

    public CoffeeBrewJobEntity(UUID id, CoffeeBrewJob.Status status, int progress) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.progress = progress;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CoffeeBrewJob.Status getStatus() {
        return status;
    }

    public void setStatus(CoffeeBrewJob.Status status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
