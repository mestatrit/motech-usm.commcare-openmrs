package org.gates.ethiopia.constants;

public final class EventConstants {

    public static final String BASE_SUBJECT = "org.motechproject.ethiopia.";

    public static final String EXCEPTION_EVENT = BASE_SUBJECT + "exception";

    public static final String UNENROLL_ATTEMPT = BASE_SUBJECT + "audit.unenroll.attempt";

    public static final String ENROLL_ATTEMPT = BASE_SUBJECT + "audit.enroll.attempt";

    public static final String LATE_EVENT = BASE_SUBJECT + "late";

    public static final String POLL_EVENT_SUBJECT = BASE_SUBJECT + "poll";

    public static final String EMAIL_DELIVERY_FAILURE = BASE_SUBJECT + "email.delivery.exception";

    public static final String REDELIVERY_POLL = BASE_SUBJECT + "redelivery.event";

    public static final String EMAIL_DELIVERY = BASE_SUBJECT + "email.send";

    private EventConstants() {
    }
}
