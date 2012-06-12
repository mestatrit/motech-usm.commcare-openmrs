package org.motechproject.model;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public enum DayOfWeek {
    Monday(1, "MON"),
    Tuesday(2, "TUE"),
    Wednesday(3, "WED"),
    Thursday(4, "THU"),
    Friday(5, "FRI"),
    Saturday(6, "SAT"),
    Sunday(7, "SUN");

    private int value;
    private String shortName;

    private DayOfWeek(int value, String shortName) {
        this.value = value;
        this.shortName = shortName;
    }

    public int getValue() {
        return value;
    }

    public static List<DayOfWeek> daysStarting(DayOfWeek day, int numberOfDays) {
        List<DayOfWeek> days = new ArrayList<DayOfWeek>();
        for (int i = 0; i <= numberOfDays; i++) {
            days.add(getDayOfWeek(DateUtil.today().withDayOfWeek(day.getValue()).plusDays(i)));
        }
        return days;
    }

    public static DayOfWeek getDayOfWeek(int dayOfWeek) throws IllegalArgumentException {
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.getValue() == dayOfWeek)
                return day;
        }

        throw new IllegalArgumentException("Not a valid day");
    }

    public static DayOfWeek getDayOfWeek(LocalDate date) {
        return DayOfWeek.getDayOfWeek(date.dayOfWeek().get());
    }

    public String getShortName() {
        return shortName;
    }

    public static DayOfWeek getDayOfWeek(DateTime.Property property) {
        return DayOfWeek.getDayOfWeek(property.get());
    }
}