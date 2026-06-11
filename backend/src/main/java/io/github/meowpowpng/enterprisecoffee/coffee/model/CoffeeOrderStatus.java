package io.github.meowpowpng.enterprisecoffee.coffee.model;

/**
 * Lifecycle status of a coffee
 * order processed by backend.
 */
public enum CoffeeOrderStatus {

    RECEIVED,
    DISPATCHED,
    COMPLETED,
    REJECTED,
    FAILED
}
