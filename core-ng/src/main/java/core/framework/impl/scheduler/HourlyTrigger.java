package core.framework.impl.scheduler;

import core.framework.scheduler.Trigger;

import java.time.ZonedDateTime;

import static core.framework.util.Strings.format;

/**
 * @author keith
 */
public final class HourlyTrigger implements Trigger {
    private final int minute;
    private final int second;

    public HourlyTrigger(int minute, int second) {
        this.minute = minute;
        this.second = second;

        if (minute < 0 || minute > 59) {
            throw new Error(format("minute out of range, please use 0-59, minute={}", minute));
        }
        if (second < 0 || second > 59) {
            throw new Error(format("second out of range, please use 0-59, second={}", second));
        }
    }

    @Override
    public ZonedDateTime next(ZonedDateTime previous) {
        return previous.withMinute(minute).withSecond(second).withNano(0).plusHours(1);
    }

    @Override
    public String toString() {
        return format("hourly@{}:{}", minute, second);
    }
}
