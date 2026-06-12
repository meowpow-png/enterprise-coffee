package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

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
