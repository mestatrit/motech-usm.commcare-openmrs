package org.gates.ethiopia.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.gates.ethiopia.constants.MotechConstants;
import org.gates.ethiopia.scheduling.EmailRedeliveryJob;
import org.gates.ethiopia.scheduling.RegistrationPoller;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class StartController {

    @Autowired
    private RegistrationPoller poller;

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");
    
    @Autowired
    private EmailRedeliveryJob deliveryJob;

    @RequestMapping("/addschedules/1B")
    public String addSchedules(HttpServletRequest request, HttpServletResponse response) {

        InputStream is = getClass().getClassLoader().getResourceAsStream(MotechConstants.MAPPING_FILE_NAME);

        StringWriter writer = new StringWriter();

        try {
            IOUtils.copy(is, writer, "UTF-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        scheduleTrackingService.add(writer.toString());

        is = getClass().getClassLoader().getResourceAsStream(MotechConstants.DEMO_MAPPING_FILE_NAME);

        writer = new StringWriter();

        try {
            IOUtils.copy(is, writer, "UTF-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        scheduleTrackingService.add(writer.toString());
        
        return null;
    }

    @RequestMapping("/start/1B")
    public String startPhase1B(HttpServletRequest request, HttpServletResponse response) {

        poller.initiateJob();

        return null;
    }
    
    @RequestMapping("/start/redelivery")
    public String startRedeliveryPolling(HttpServletRequest request, HttpServletResponse response) {

        deliveryJob.initiateJob();

        return null;
    }

    @RequestMapping("/stop/1B")
    public String endPhase1B(HttpServletRequest request, HttpServletResponse response) {

        return null;
    }

    @RequestMapping("/clear/1B")
    public String clearPhase1B(HttpServletRequest request, HttpServletResponse response) {

        List<String> scheduleNames = new ArrayList<String>();

        scheduleNames.add(MotechConstants.SCHEDULE_NAME);

        EnrollmentsQuery query = new EnrollmentsQuery().havingSchedule(MotechConstants.SCHEDULE_NAME);

        List<EnrollmentRecord> records = scheduleTrackingService.search(query);

        for (EnrollmentRecord record : records) {
            logger.info("Unenrolling : " + record.getExternalId());
            scheduleTrackingService.unenroll(record.getExternalId(), scheduleNames);
        }

        return null;
    }

    @RequestMapping("/status/1B")
    public ModelAndView statusPhase1B(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("enrollments");

        EnrollmentsQuery query = new EnrollmentsQuery().havingSchedule(MotechConstants.SCHEDULE_NAME).havingState(
                EnrollmentStatus.ACTIVE);

        List<EnrollmentRecord> enrollmentList = scheduleTrackingService.search(query);

        mav.addObject("activeenrollments", enrollmentList);

        EnrollmentsQuery query2 = new EnrollmentsQuery().havingSchedule(MotechConstants.SCHEDULE_NAME).havingState(
                EnrollmentStatus.COMPLETED);

        List<EnrollmentRecord> completedEnrollmentList = scheduleTrackingService.search(query2);

        mav.addObject("completedenrollments", completedEnrollmentList);

        EnrollmentsQuery query3 = new EnrollmentsQuery().havingSchedule(MotechConstants.SCHEDULE_NAME).havingState(
                EnrollmentStatus.DEFAULTED);

        List<EnrollmentRecord> defaultedEnrollmentList = scheduleTrackingService.search(query3);

        mav.addObject("defaultedenrollments", defaultedEnrollmentList);

        EnrollmentsQuery query4 = new EnrollmentsQuery().havingSchedule(MotechConstants.SCHEDULE_NAME).havingState(
                EnrollmentStatus.UNENROLLED);

        List<EnrollmentRecord> unenrolledEnrollmentList = scheduleTrackingService.search(query4);

        mav.addObject("unenrolledenrollments", unenrolledEnrollmentList);

        return mav;
    }

}
