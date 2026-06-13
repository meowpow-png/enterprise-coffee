package io.github.meowpowpng.enterprisecoffee.support;

public final class LoggingTestFixtures {

    private LoggingTestFixtures() {}

    public static void withoutLogging(Class<?> loggerClass, Runnable action) {
        var logger = (ch.qos.logback.classic.Logger)
                org.slf4j.LoggerFactory.getLogger(loggerClass);

        var originalLevel = logger.getLevel();
        logger.setLevel(ch.qos.logback.classic.Level.OFF);

        try {
            action.run();
        }
        finally {
            logger.setLevel(originalLevel);
        }
    }
}
