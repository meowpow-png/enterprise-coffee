package io.github.meowpowpng.enterprisecoffee.coffee.machine.api;

import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineProtocolException;
import io.github.meowpowpng.enterprisecoffee.coffee.machine.api.exception.CoffeeMachineUnavailableException;
import io.github.meowpowpng.enterprisecoffee.coffee.model.CoffeeType;
import io.github.meowpowpng.enterprisecoffee.coffee.model.Progress;

import org.jspecify.annotations.NullMarked;

@NullMarked
final class StubCoffeeMachineClient implements CoffeeMachineClient {

    public static final String UNAVAILABLE_MESSAGE = "machine unavailable";
    public static final String PROTOCOL_FAILURE_MESSAGE = "protocol failure";

    private CoffeeMachineStatus status;
    private MachineCoffeeProgress progress;
    private boolean isAvailable, protocolFails;

    StubCoffeeMachineClient() {
        this.status = CoffeeMachineStatus.READY;
        this.progress = new MachineCoffeeProgress(
                new CoffeeType("ESPRESSO"),
                Progress.of(50)
        );
        this.isAvailable = true;
    }

    @Override
    public CoffeeMachineStatus status() {
        if (!isAvailable) {
            throw new CoffeeMachineUnavailableException(
                    UNAVAILABLE_MESSAGE,
                    new RuntimeException("boom")
            );
        }
        if (protocolFails) {
            throw new CoffeeMachineProtocolException(
                    PROTOCOL_FAILURE_MESSAGE,
                    new RuntimeException("boom")
            );
        }
        return status;
    }

    @Override
    public MachineCoffeeProgress progress() {
        return progress;
    }

    @Override
    public MachineOrderResult order(CoffeeType type) {
        return MachineOrderResult.ACCEPTED;
    }

    void status(CoffeeMachineStatus status) {
        this.status = status;
    }

    void progress(MachineCoffeeProgress progress) {
        this.progress = progress;
    }

    void markUnavailable() {
        isAvailable = false;
    }

    void markProtocolFailure() {
        protocolFails = true;
    }

    void reset() {
        this.status = CoffeeMachineStatus.READY;
        this.progress = new MachineCoffeeProgress(
                new CoffeeType("ESPRESSO"),
                Progress.of(50)
        );
        isAvailable = true;
        protocolFails = false;
    }
}
