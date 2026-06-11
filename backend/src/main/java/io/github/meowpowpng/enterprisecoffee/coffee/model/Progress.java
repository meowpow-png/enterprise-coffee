package io.github.meowpowpng.enterprisecoffee.coffee.model;

/**
 * Represents operation progress as a percentage.
 */
public final class Progress {

    private final int value;

    private Progress(int value) {
        if (value < 0 || value > 100) {
            var message = "progress must be between 0 and 100 but was " + value;
            throw new IllegalArgumentException(message);
        }
        this.value = value;
    }

    /**
     * Returns a progress value representing 0%.
     */
    public static Progress initial() {
        return new Progress(0);
    }

    /**
     * Returns a progress value for the specified percentage.
     *
     * @param value progress percentage
     *
     * @throws IllegalArgumentException if {@code value}
     * is outside valid range {@code 0-100}
     */
    public static Progress of(int value) {
        return new Progress(value);
    }

    /**
     * Returns the progress percentage.
     */
    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Progress other
                && value == other.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
