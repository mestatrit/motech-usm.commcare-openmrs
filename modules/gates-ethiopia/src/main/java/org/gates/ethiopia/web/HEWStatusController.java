package org.gates.ethiopia.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gates.ethiopia.constants.CommcareConstants;
import org.gates.ethiopia.constants.EventConstants;
import org.gates.ethiopia.constants.MotechConstants;
import org.gates.ethiopia.scheduling.GenerateDateTimeUtil;
import org.joda.time.LocalDate;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.motechproject.eventlogging.service.EventQueryService;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HEWStatusController {

    @Autowired
    private EventQueryService<CouchEventLog> queryService;

    @Autowired
    private CommcareCaseService caseService;

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    
    @Autowired
    private EventRelay eventRelay;

    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @RequestMapping("/enrollments/view")
    public ModelAndView checkSchedules(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("lastsubmission");

        Map<String, String> late = new HashMap<String, String>();

        List<CaseInfo> hewList = caseService.getAllCasesByType(CommcareConstants.CASE_TYPE);

        for (CaseInfo hew : hewList) {
            List<CouchEventLog> logs = queryService.getAllEventsBySubjectAndParameter(EventConstants.LATE_EVENT,
                    MotechConstants.EXTERNAL_ID, hew.getCaseId());
            late.put(hew.getCaseId(), new Integer(logs.size()).toString());
        }

        mav.addObject("hews", hewList);

        mav.addObject("late", late);

        EnrollmentsQuery query = new EnrollmentsQuery().havingSchedule(MotechConstants.SCHEDULE_NAME).havingState(
                EnrollmentStatus.ACTIVE);

        List<EnrollmentRecord> enrollmentList = scheduleTrackingService.search(query);

        Map<String, String> hewMap = new HashMap<String, String>();

        for (EnrollmentRecord enrollmentRecord : enrollmentList) {
            hewMap.put(enrollmentRecord.getExternalId(), "Yes");
        }

        mav.addObject("activeenrollments", hewMap);

        String result = (String) request.getSession().getAttribute("result");

        if (result != null) {
            if ("success".equals(result)) {
                mav.addObject("success", "HEW unenrolled");
            } else {
                mav.addObject("success", "HEW enrolled");
            }
        }

        return mav;
    }

    @RequestMapping("/enrollments/admin")
    public ModelAndView admin(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("admin");

        return mav;
    }

    @RequestMapping("/regions") 
    public ModelAndView regions(HttpServletRequest request, HttpServletResponse response) {

        logger.info("Printing regions...");

        Set<String> regions = new HashSet<String>();

        List<CaseInfo> hewList = caseService.getAllCasesByType(CommcareConstants.CASE_TYPE);

        logger.info("# of HEWs: " + hewList.size());

        for (CaseInfo hew : hewList) {
            regions.add(hew.getFieldValues().get("region").trim().toLowerCase());
        }

        for (String region : regions) {
            logger.info("Region: " + region);
        }

        ModelAndView mav = new ModelAndView("admin");

        return mav;
    }

    @RequestMapping("/enrollments/checkemails")
    public ModelAndView emails(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("admin");

        List<CaseInfo> hewList = caseService.getAllCasesByType(CommcareConstants.CASE_TYPE);


        Map<String, List<String>> woredaFacilityReportingDayMap = new HashMap<String, List<String>>();

        for (CaseInfo hew : hewList) {
            String woreda = hew.getFieldValues().get(CommcareConstants.WOREDA).trim();
            String facility = hew.getFieldValues().get(CommcareConstants.FACILITY_NAME).trim();
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

        List<String> list = new ArrayList<String>(woredaFacilityReportingDayMap.keySet());

        logger.info("Number of woreda-facility pairs: " + list.size());

        for (String woredaFacility : list) {
            MotechEvent hewEvent = new MotechEvent(EventSubjects.MILESTONE_ALERT);
            hewEvent.getParameters().put(EventDataKeys.SCHEDULE_NAME, MotechConstants.SCHEDULE_NAME);
            hewEvent.getParameters().put(EventDataKeys.EXTERNAL_ID, woredaFacility);
            eventRelay.sendEventMessage(hewEvent);
        }

        return mav;
    }
}
