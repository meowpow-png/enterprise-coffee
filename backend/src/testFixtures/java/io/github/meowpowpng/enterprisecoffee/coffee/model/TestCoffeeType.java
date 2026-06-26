package io.github.meowpowpng.enterprisecoffee.coffee.model;

public final class TestCoffeeType {

    private TestCoffeeType() {}

    public static CoffeeType create() {
        return new CoffeeType("ESPRESSO");
    }
}
