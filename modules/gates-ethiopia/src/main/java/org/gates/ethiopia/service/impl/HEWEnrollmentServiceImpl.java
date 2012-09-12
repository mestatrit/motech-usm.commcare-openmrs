package org.gates.ethiopia.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gates.ethiopia.service.HEWEnrollmentService;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HEWEnrollmentServiceImpl implements HEWEnrollmentService {

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @Override
    public void enrollHEW(String hewId, String nameOfSchedule, LocalDate date, Time theTime,
            Map<String, String> metadata) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.setExternalId(hewId);
        enrollmentRequest.setScheduleName(nameOfSchedule);
        enrollmentRequest.setReferenceDate(date);
        enrollmentRequest.setReferenceTime(theTime);
        enrollmentRequest.setEnrollmentDate(date);
        enrollmentRequest.setEnrollmentTime(theTime);
        enrollmentRequest.setMetadata(metadata);

        scheduleTrackingService.enroll(enrollmentRequest);

        logger.info("New enrollment for " + " caseId: " + hewId + " for enroll time: " + date + " " + theTime);
    }

    @Override
    public boolean isEnrolled(String externalId, String scheduleName) {
        EnrollmentRecord record = scheduleTrackingService.getEnrollment(externalId, scheduleName);
        if (record != null) {
            return true;
        }
        return false;
    }

    @Override
    public void enrollAllHEWs(Map<String, DateTime> hews, String nameOfSchedule) {
        LocalDate date;
        Time time;

        for (Map.Entry<String, DateTime> hew : hews.entrySet()) {
            DateTime theTime = hew.getValue();

            date = DateUtil.newDate(theTime);
            time = new Time(theTime.getHourOfDay(), theTime.getMinuteOfHour());

            enrollHEW(hew.getKey(), nameOfSchedule, date, time, null);
        }
    }

    @Override
    public void unenrollHEW(String hewId, String nameOfSchedule) {
        List<String> schedules = new ArrayList<String>();
        schedules.add(nameOfSchedule);
        scheduleTrackingService.unenroll(hewId, schedules);
        logger.info("Unenrolled " + hewId + " from " + nameOfSchedule);
    }

    @Override
    public void unenrollAllHEWs(List<String> hews, String nameOfSchedule) {
        for (String hewId : hews) {
            unenrollHEW(hewId, nameOfSchedule);
        }
    }
}
