package org.gates.ethiopia.listeners;

import java.util.List;
import java.util.Properties;
import org.gates.ethiopia.constants.CommcareConstants;
import org.gates.ethiopia.constants.EventConstants;
import org.gates.ethiopia.constants.MotechConstants;
import org.gates.ethiopia.service.GatesEthiopiaMailService;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LateFormListener {

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @Autowired
    private GatesEthiopiaMailService mailService;

    @Autowired
    private CommcareCaseService caseService;

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    private EventRelay eventRelay;

    @Autowired
    @Qualifier(value = "regionEmailConfiguration")
    private Properties regionEmailConfiguration;

    @MotechListener(subjects = EventSubjects.MILESTONE_ALERT)
    public void handleWoredaFacilityLateOnForm(MotechEvent event) {
        MilestoneEvent milestoneEvent = new MilestoneEvent(event);

         logger.info("Alert for schedule: " +
         milestoneEvent.getScheduleName());

        if (milestoneEvent.getScheduleName().equals(MotechConstants.SCHEDULE_NAME)) {
            String woredaFacilityId = milestoneEvent.getExternalId();
            List<CaseInfo> hews = caseService.getAllCasesByType(CommcareConstants.CASE_TYPE);
            String[] woredaFacility = woredaFacilityId.split("[.]");

            String region = null;
            if ((region = (formSubmittedForWoredaFacilityWithinLastWeek(woredaFacility[0], woredaFacility[1], hews))) != null) {
                sendEmailAlert(woredaFacilityId, region);
                logger.info("**** Form was NOT submitted for " + milestoneEvent.getExternalId());
            } else {
                logger.info("&&&& Form was submitted for " + milestoneEvent.getExternalId());
            }

            LocalDate localDate = DateUtil.today();
            Time time = DateUtil.time(DateTime.now());

             scheduleTrackingService.fulfillCurrentMilestone(woredaFacilityId,
             MotechConstants.SCHEDULE_NAME, localDate,
             time);

        }
    }

    // Demo handling
    // @MotechListener(subjects = EventSubjects.MILESTONE_ALERT)
    // public void handleHEWLateOnForm(MotechEvent event) throws
    // InterruptedException {
    //
    // MilestoneEvent milestoneEvent = new MilestoneEvent(event);
    //
    // if
    // (milestoneEvent.getScheduleName().equals(MotechConstants.SCHEDULE_NAME))
    // {
    //
    // String hewId = milestoneEvent.getExternalId();
    //
    // logger.info("Milestone alert for: " + hewId + " in window: " +
    // milestoneEvent.getWindowName());
    //
    // CaseInfo hewCase = null;
    //
    // try {
    // hewCase = caseService.getCaseByCaseId(hewId);
    // } catch (Exception e) {
    // logger.warn("ERROR: " + e.getMessage() +
    // " when accessing CommCare's cases API");
    // MotechEvent exceptionEvent = new
    // MotechEvent(EventConstants.EXCEPTION_EVENT);
    // exceptionEvent.getParameters().put("message", e.getMessage());
    // eventRelay.sendEventMessage(exceptionEvent);
    // return;
    // }
    //
    // Map<String, String> fieldValues = null;
    //
    // if (hewCase != null) {
    // fieldValues = hewCase.getFieldValues();
    // }
    //
    // String lastSubmittedOn = null;
    // String region = "eastern";
    //
    // if (fieldValues != null) {
    // lastSubmittedOn =
    // hewCase.getFieldValues().get(CommcareConstants.LAST_SUBMITTED);
    // region =
    // hewCase.getFieldValues().get(CommcareConstants.REGION).toLowerCase();
    // }
    //
    // DateTime lastDateSubmitted = null;
    //
    // if (lastSubmittedOn != null) {
    // lastDateSubmitted = DateTime.parse(lastSubmittedOn);
    // logger.info("HEW " + fieldValues.get(CommcareConstants.HEW_NAME) +
    // " + caseId: " + hewId
    // + " last submitted on " + lastDateSubmitted);
    // } else {
    // logger.info("HEW " + fieldValues.get(CommcareConstants.HEW_NAME) +
    // " + caseId: " + hewId
    // + " has never submitted.");
    // }
    //
    // if (milestoneEvent.getWindowName().equals(MotechConstants.LATE_WINDOW)) {
    //
    // DateTime timePrior =
    // DateTime.now().minusMinutes(CommcareConstants.QUICK_MINUTES);
    // logger.info(timePrior.toString());
    //
    // switch (region) {
    // case "eastern":
    // case "eastern2":
    // timePrior = timePrior.minusHours(4);
    // break;
    // case "central":
    // case "central2":
    // timePrior = timePrior.minusHours(6);
    // break;
    // case "mountain":
    // case "mountain2":
    // timePrior = timePrior.minusHours(6);
    // break;
    // case "western":
    // case "western2":
    // timePrior = timePrior.minusHours(7);
    // break;
    // default:
    // break;
    // }
    //
    // logger.info(timePrior.toString());
    //
    // LocalDate localDate = DateUtil.today();
    //
    // Time time = DateUtil.time(DateTime.now());
    //
    // scheduleTrackingService.fulfillCurrentMilestone(hewId,
    // MotechConstants.SCHEDULE_NAME, localDate, time);
    //
    // if (lastDateSubmitted == null || lastDateSubmitted.isBefore(timePrior)) {
    // MotechEvent lateEvent = new MotechEvent(EventConstants.LATE_EVENT);
    // lateEvent.getParameters().put(CommcareConstants.HEW_NAME,
    // fieldValues.get(CommcareConstants.HEW_NAME));
    // lateEvent.getParameters().put(CommcareConstants.EXTERNAL_ID,
    // milestoneEvent.getExternalId());
    // eventRelay.sendEventMessage(lateEvent);
    // sendEmailAlert(fieldValues.get(CommcareConstants.HEW_NAME),
    // fieldValues.get(CommcareConstants.REGION));
    // } else {
    // logger.info("HEW: " + fieldValues.get(CommcareConstants.HEW_NAME)
    // + " was on time, having last submitted on " + lastDateSubmitted);
    // }
    // }
    // }
    // }

    private void sendEmailAlert(String hewName, String region) {

        String emailAddress = regionEmailConfiguration.getProperty(region.trim().toLowerCase());

        if (emailAddress != null) {
            mailService.scheduleMail(hewName, emailAddress, region);
        } else {
            logger.info("Unable to schedule e-mail for " + hewName + " for region: " + region);
        }
    }

    private String formSubmittedForWoredaFacilityWithinLastWeek(String woreda, String facility, List<CaseInfo> hews) {

        String region = null;

        for (CaseInfo hew : hews) {
            String hewWoreda = hew.getFieldValues().get(CommcareConstants.WOREDA).trim().toLowerCase();
            String hewFacility = hew.getFieldValues().get(CommcareConstants.FACILITY_NAME).trim().toLowerCase();
            hew.getFieldValues().get(CommcareConstants.LAST_SUBMITTED);

            if (woreda.equals(hewWoreda) && facility.equals(hewFacility)) {
                region = hew.getFieldValues().get(CommcareConstants.REGION);
                int dayDueBy = MotechConstants.DAY_DUE;
                String lastSubmittedString = hew.getFieldValues().get(CommcareConstants.LAST_SUBMITTED);
                logger.info("HEW " + hew.getFieldValues().get(CommcareConstants.HEW_NAME) + " for " + woreda + " : "
                        + facility + " - last submitted a form at: " + lastSubmittedString);
                if (lastSubmittedString != null) {
                    DateTime lastSubmitted = DateTime.parse(lastSubmittedString);
                    if (checkLate(lastSubmitted, dayDueBy, MotechConstants.SECONDS_IN_DAY
                            * MotechConstants.NUM_DAYS_TO_CHECK)) {
                        return null;
                    }
                }
            }
        }

        if (region == null) {
            region = "default";
        }
        MotechEvent lateEvent = new MotechEvent(EventConstants.LATE_EVENT);
        lateEvent.getParameters().put(CommcareConstants.WOREDA, woreda);
        lateEvent.getParameters().put(CommcareConstants.FACILITY_NAME, facility);
        eventRelay.sendEventMessage(lateEvent);
        return region;
    }

    private boolean checkLate(DateTime lastSubmitted, int dayDueBy, int secondsDueAgo) {
        DateTime fixedDate = lastSubmitted.withYear(MotechConstants.YEAR);
        DateTime secondsAgo = DateTime.now().minusSeconds(secondsDueAgo);

        if (!fixedDate.isBefore(secondsAgo)) {
            return true;
        }

        return false;
    }
}
