package io.github.meowpowpng.enterprisecoffee.internal.brew;

/**
 * Suspends execution of the current thread.
 */
public interface ThreadSleeper {

    /**
     * Suspends execution of the current thread.
     *
     * @throws IllegalStateException if the
     * current thread is interrupted
     */
    void sleep();
}
