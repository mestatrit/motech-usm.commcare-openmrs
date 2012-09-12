package org.gates.ethiopia.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gates.ethiopia.constants.CommcareConstants;
import org.gates.ethiopia.constants.EventConstants;
import org.gates.ethiopia.constants.MotechConstants;
import org.gates.ethiopia.scheduling.RegistrationPoller;
import org.gates.ethiopia.service.HEWEnrollmentService;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HEWEnrollController {

    @Autowired
    private EventRelay eventRelay;

    @Autowired
    private HEWEnrollmentService hewEnrollmentService;

    @Autowired
    private CommcareCaseService caseService;

    @Autowired
    private RegistrationPoller poller;

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @RequestMapping("/enrollments/unenroll")
    public ModelAndView unenroll(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("redirect:/gates-ethiopia/enrollments/view");

        String caseId = request.getParameter("caseId");

        hewEnrollmentService.unenrollHEW(caseId, MotechConstants.SCHEDULE_NAME);

        request.getSession().setAttribute("result", "success");

        raiseAccessEvent(EventConstants.UNENROLL_ATTEMPT, caseId, request);

        return mav;
    }

//    @RequestMapping("/enrollments/enroll")
//    public ModelAndView enroll(HttpServletRequest request, HttpServletResponse response) {
//        ModelAndView mav = new ModelAndView("redirect:/gates-ethiopia/enrollments/view");
//
//        String caseId = request.getParameter("caseId");
//
//        if (caseId != null && caseId.trim().length() > 0) {
//            int day = Integer.parseInt(request.getParameter("day"));
//            int hour = Integer.parseInt(request.getParameter("hour"));
//            int minute = Integer.parseInt(request.getParameter("minute"));
//            String timezone = request.getParameter("timezone");
//
//            DateTime now = DateTime.now();
//            
//            Map<String, String> metadata = new HashMap<String, String>();
//            
//            if (timezone.equals("PDT")) {
//                hour = hour + 3;
//                metadata.put("timezone", timezone);
//            } else if (timezone.equals("EDT")) {
//                metadata.put("timezone", timezone);
//            }
//
//            DateTime enrollTime;
//            
//            enrollTime = now.withHourOfDay(hour).withMinuteOfHour(minute).withDayOfWeek(day);

//            if (now.dayOfWeek().get() > day) {
//                enrollTime = now.withHourOfDay(hour).withMinuteOfHour(minute).withDayOfWeek(day);
//            } else {
//                enrollTime = now.withHourOfDay(hour).withMinuteOfHour(minute).withDayOfWeek(day).minusWeeks(1);
//            }
//            
//
//
//            LocalDate date = enrollTime.toLocalDate();
//
//            Time theTime = DateUtil.time(enrollTime);
//
//            hewEnrollmentService.enrollHEW(caseId, MotechConstants.SCHEDULE_NAME, date, theTime, metadata);
//
//            request.getSession().setAttribute("result", "enrolled");
//        }
//
//        raiseAccessEvent(EventConstants.ENROLL_ATTEMPT, caseId, request);
//
//        return mav;
//    }

    @RequestMapping("/enrollments/enrollall")
    public ModelAndView enrollAll(HttpServletRequest request, HttpServletResponse response) {
        poller.pollCommCareForRegistrations(null);
        return null;
    }

    @RequestMapping("/enrollments/unenrollall")
    public ModelAndView unenrollAll(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("admin");

        List<CaseInfo> hewList = new ArrayList<CaseInfo>();

        try {
            hewList = caseService.getAllCasesByType(CommcareConstants.CASE_TYPE);
        } catch (Exception e) {
            logger.warn("Problem with getting all cases by type from CommCare");
        }

        List<String> hews = new ArrayList<String>();

        for (CaseInfo hew : hewList) {
            hews.add(hew.getCaseId());

        }

        hewEnrollmentService.unenrollAllHEWs(hews, MotechConstants.SCHEDULE_NAME);

        return mav;
    }

    @RequestMapping("/enrollments/enrollallevenifcompleted")
    public ModelAndView enrollAllEvenIfCompleted(HttpServletRequest request, HttpServletResponse response) {
        // TODO
        return null;
    }

    private void raiseAccessEvent(String accessAttempt, String caseId, HttpServletRequest request) {

        MotechEvent accessEvent = new MotechEvent(accessAttempt);

        String remoteAddress = request.getRemoteAddr();

        Map<String, Object> parameters = accessEvent.getParameters();

        parameters.put("IP", remoteAddress);
        parameters.put("caseId", caseId);

        eventRelay.sendEventMessage(accessEvent);
    }
}
