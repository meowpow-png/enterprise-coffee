package io.github.meowpowpng.enterprisecoffee.support;

import java.time.*;

public final class TestClock extends Clock {

    private final ZoneId zoneId;
    private Instant instant;

    private TestClock(Instant instant, ZoneId zoneId) {
        this.instant = instant;
        this.zoneId = zoneId;
    }

    public static TestClock create(Instant instant) {
        return new TestClock(instant, ZoneOffset.UTC);
    }

    public static TestClock create() {
        return create(Instant.parse("2025-01-01T00:00:00Z"));
    }

    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new TestClock(instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }

    public void advance(Duration duration) {
        instant = instant.plus(duration);
    }
}
