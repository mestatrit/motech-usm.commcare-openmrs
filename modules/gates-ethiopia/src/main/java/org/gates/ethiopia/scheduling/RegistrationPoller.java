package org.gates.ethiopia.scheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gates.ethiopia.constants.CommcareConstants;
import org.gates.ethiopia.constants.EventConstants;
import org.gates.ethiopia.constants.MotechConstants;
import org.gates.ethiopia.service.HEWEnrollmentService;
import org.joda.time.LocalDate;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationPoller {

    @Autowired
    private MotechSchedulerService schedulerService;

    @Autowired
    private CommcareCaseService caseService;

    @Autowired
    private HEWEnrollmentService enrollmentService;

    private static final String CRON_EXPRESSION = "0 */1 * * *  ? ";

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    public void initiateJob() {

        logger.info("Starting registration polling job with cron schedule " + CRON_EXPRESSION);

        MotechEvent pollRegistrationsEvent = new MotechEvent(EventConstants.POLL_EVENT_SUBJECT);

        CronSchedulableJob pollingJob = new CronSchedulableJob(pollRegistrationsEvent, CRON_EXPRESSION);

        schedulerService.safeScheduleJob(pollingJob);

    }

    public void stopPollingJob() {
        // TODO
    }

    @MotechListener(subjects = EventConstants.POLL_EVENT_SUBJECT)
    public void pollCommCareForRegistrations(MotechEvent event) {

        logger.debug("Polling CommCare... ");

        List<CaseInfo> hewList = new ArrayList<CaseInfo>();

        try {
            hewList = caseService.getAllCasesByType(CommcareConstants.CASE_TYPE);
        } catch (Exception e) {
            logger.warn("Problem with getting all cases by type from CommCare");
        }

        logger.info("Number of current HEWs: " + hewList.size());

        // enrollHEWs(hewList);
        enrollFacilities(hewList);
    }

    private void enrollFacilities(List<CaseInfo> hewList) {

        Map<String, List<String>> woredaFacilityReportingDayMap = new HashMap<String, List<String>>();

        for (CaseInfo hew : hewList) {
            String woreda = hew.getFieldValues().get(CommcareConstants.WOREDA).trim().toLowerCase();
            String facility = hew.getFieldValues().get(CommcareConstants.FACILITY_NAME).trim().toLowerCase();
            String facilityWoredaKey = woreda + "." + facility;
            if (woredaFacilityReportingDayMap.get(facilityWoredaKey) == null) {
                List<String> list = new ArrayList<String>();
                list.add(hew.getFieldValues().get(CommcareConstants.HEW_NAME));
                woredaFacilityReportingDayMap.put(facilityWoredaKey, list);
            } else {
                woredaFacilityReportingDayMap.get(facilityWoredaKey).add(
                        hew.getFieldValues().get(CommcareConstants.HEW_NAME));

            }
        }

        for (Map.Entry<String, List<String>> entry : woredaFacilityReportingDayMap.entrySet()) {

            LocalDate localDate = new LocalDate(GenerateDateTimeUtil.nextTime());
            

            if (!enrollmentService.isEnrolled(entry.getKey(), MotechConstants.SCHEDULE_NAME)) {
                enrollmentService.enrollHEW(entry.getKey(), MotechConstants.SCHEDULE_NAME, localDate,
                        new Time(MotechConstants.HOUR_DUE, MotechConstants.MINUTE_DUE), null);
            }
        }

        List<String> list = new ArrayList<String>(woredaFacilityReportingDayMap.keySet());

        logger.info("Number of woreda-facility pairs: " + list.size());

//        Collections.sort(list);
//
//        File fileToLogTo = new File("hewInfo");
//
//        for (String value : list) {
//            try (FileWriter fileStream = new FileWriter(fileToLogTo, true);
//                    BufferedWriter fileWriter = new BufferedWriter(fileStream)) {
//                fileWriter.write((value));
//                fileWriter.newLine();
//                List<String> hewListWoredaFacility = woredaFacilityReportingDayMap.get(value);
//                for (String hew : hewListWoredaFacility) {
//                    fileWriter.write("\t" + hew);
//                    fileWriter.newLine();
//                }
//                fileWriter.flush();
//            } catch (IOException e) {
//                logger.warn("Error when trying to log to file " + fileToLogTo + ": " + e.getMessage());
//            }
//        }

    }
//     private void enrollHEWs(List<CaseInfo> hewList) {
//     for (CaseInfo hew : hewList) {
//     String hewId = hew.getCaseId();
//    
//     EnrollmentsQuery query = new
//     EnrollmentsQuery().havingSchedule(MotechConstants.SCHEDULE_NAME).havingExternalId(hewId);
//    
//     List<EnrollmentRecord> records = scheduleTrackingService.search(query);
//    
//     if (records.size() == 0) {
//    
//     DateTime enrollTime = GenerateDateTimeUtil.nextTime();
//    
//     LocalDate date = enrollTime.toLocalDate();
//    
//     Time theTime = DateUtil.time(enrollTime);
//    
//     EnrollmentRequest hewEnrollmentRequest = new EnrollmentRequest();
//     hewEnrollmentRequest.setExternalId(hewId);
//     hewEnrollmentRequest.setScheduleName(MotechConstants.SCHEDULE_NAME);
//     hewEnrollmentRequest.setReferenceDate(date);
//     hewEnrollmentRequest.setReferenceTime(theTime);
//     logger.info("New enrollment for " +
//     hew.getFieldValues().get(CommcareConstants.HEW_NAME) + " caseId: " +
//     hewId
//     + " for enroll time: " + date + " " + theTime);
//    
//     scheduleTrackingService.enroll(hewEnrollmentRequest);
//     }
//     }
//     }
}
