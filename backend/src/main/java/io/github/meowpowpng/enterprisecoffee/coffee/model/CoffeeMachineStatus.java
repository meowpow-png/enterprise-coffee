package io.github.meowpowpng.enterprisecoffee.coffee.model;

/**
 * Operational status of the coffee machine.
 */
public enum CoffeeMachineStatus {

    READY,
    BREWING,
    OFFLINE;

    public boolean isReady() {
        return this == READY;
    }

    public boolean isBrewing() {
        return this == BREWING;
    }

    public boolean isOffline() {
        return this == OFFLINE;
    }
}
