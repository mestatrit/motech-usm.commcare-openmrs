package org.gates.ethiopia.service;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;

public interface HEWEnrollmentService {

    void enrollHEW(String hewId, String nameOfSchedule, LocalDate date, Time theTime, Map<String, String> metadata);

    void enrollAllHEWs(Map<String, DateTime> hews, String nameOfSchedule);

    void unenrollHEW(String hewId, String nameOfSchedule);

    void unenrollAllHEWs(List<String> hews, String nameOfSchedule);

    boolean isEnrolled(String externalId, String scheduleName);
}
