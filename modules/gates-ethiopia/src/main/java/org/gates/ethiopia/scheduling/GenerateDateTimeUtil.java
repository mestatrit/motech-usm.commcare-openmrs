package org.gates.ethiopia.scheduling;

import org.gates.ethiopia.constants.MotechConstants;
import org.joda.time.DateTime;

public final class GenerateDateTimeUtil {

    private GenerateDateTimeUtil() {
    }

    public static DateTime nextTime() {

        DateTime now = DateTime.now();

        DateTime enrollTime;

        if (now.dayOfWeek().get() == MotechConstants.DAY_DUE) {
            enrollTime = now.withHourOfDay(MotechConstants.HOUR_DUE).withMinuteOfHour(MotechConstants.MINUTE_DUE).minusWeeks(1);
        } else if (now.dayOfWeek().get() > MotechConstants.DAY_DUE) {
            enrollTime = now.withHourOfDay(MotechConstants.HOUR_DUE).withMinuteOfHour(MotechConstants.MINUTE_DUE).withDayOfWeek(MotechConstants.DAY_DUE);
        } else {
            enrollTime = now.withHourOfDay(MotechConstants.HOUR_DUE).withMinuteOfHour(MotechConstants.MINUTE_DUE).withDayOfWeek(MotechConstants.DAY_DUE).minusWeeks(1);
        }

        return enrollTime;
    }
    
    public static DateTime nextTime(int day) {

        DateTime now = DateTime.now();

        DateTime enrollTime;

        if (now.dayOfWeek().get() == day) {
            enrollTime = now.withHourOfDay(MotechConstants.HOUR_DUE).withMinuteOfHour(MotechConstants.MINUTE_DUE).minusWeeks(1);
        } else if (now.dayOfWeek().get() > day) {
            enrollTime = now.withHourOfDay(MotechConstants.HOUR_DUE).withMinuteOfHour(MotechConstants.MINUTE_DUE).withDayOfWeek(MotechConstants.DAY_DUE);
        } else {
            enrollTime = now.withHourOfDay(MotechConstants.HOUR_DUE).withMinuteOfHour(MotechConstants.MINUTE_DUE).withDayOfWeek(MotechConstants.DAY_DUE).minusWeeks(1);
        }

        return enrollTime;
    }
}
