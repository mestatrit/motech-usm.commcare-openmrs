package org.gates.ethiopia.constants;

import org.motechproject.model.DayOfWeek;

public final class MotechConstants {

    // Schedule tracking constants
    public static final String SCHEDULE_NAME_KEY = "schedule_name";

    public static final String LATE_WINDOW = "late";

    public static final String SCHEDULE_NAME = "Health Worker Form Submission Tracking";

    public static final String MAPPING_FILE_NAME = "hew-schedule.json";

    public static final String DEMO_MAPPING_FILE_NAME = "hew-demo-schedule.json";

    // E-mail constants
    public static final String WOREDA_PLACEHOLDER = "[WOREDA]";

    public static final String FACILITY_PLACEHOLDER = "[HEALTHPOST]";

    public static final String REMINDER_BODY = "body";

    public static final String REMINDER_SUBJECT = "subject";

    // Time due constants
    public static final int DAY_DUE = DayOfWeek.Sunday.getValue();

    public static final int HOUR_DUE = 23;

    public static final int MINUTE_DUE = 59;

    public static final int SECONDS_IN_MINUTE = 60;

    public static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;

    public static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;

    public static final int SECONDS_IN_WEEK = SECONDS_IN_DAY * 7;

    // Querying events

    public static final String EXTERNAL_ID = "externalId";

    // Redelivery

    public static final int RANDOM_MINUTES = 239;

    public static final String RECIPIENT = "recipient";

    public static final String EMAIL_ADDRESS = "email";

    public static final String REGION = "region";

    public static final int NUM_DAYS_TO_CHECK = 7;

    public static final int YEAR = 2012;

    private MotechConstants() {
    }

}
