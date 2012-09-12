package org.gates.ethiopia.scheduling;

import java.util.List;
import org.gates.ethiopia.constants.EventConstants;
import org.gates.ethiopia.constants.MotechConstants;
import org.gates.ethiopia.service.GatesEthiopiaMailService;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.motechproject.eventlogging.service.EventQueryService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailRedeliveryJob {
    
    private static final int HOURS_AGO = 3;
    
    private static final String CRON_EXPRESSION = "0 0 */3 * *  ? ";

    @Autowired
    private MotechSchedulerService schedulerService;
    
    @Autowired
    private EventQueryService<CouchEventLog> eventQueryService;
    
    @Autowired
    private GatesEthiopiaMailService mailService;
    
    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");
    
    public void initiateJob() {

        logger.info("Starting email redelivery polling job with cron schedule " + CRON_EXPRESSION);

        MotechEvent pollRegistrationsEvent = new MotechEvent(EventConstants.REDELIVERY_POLL);

        CronSchedulableJob pollingJob = new CronSchedulableJob(pollRegistrationsEvent, CRON_EXPRESSION);

        schedulerService.safeScheduleJob(pollingJob);

    }
    
    @MotechListener(subjects = EventConstants.REDELIVERY_POLL)
    public void pollCommCareForRegistrations(MotechEvent event) {
        List<CouchEventLog> deliveryFailureEvents = eventQueryService.getAllEventsBySubject(EventConstants.EMAIL_DELIVERY_FAILURE);
        logger.info("Total delivery failures to date: " + deliveryFailureEvents.size());
        for (CouchEventLog log : deliveryFailureEvents) {
            if (log.getTimeStamp().isAfter(DateTime.now().minusMinutes(HOURS_AGO))) {
                String recipient = (String) log.getParameters().get(MotechConstants.RECIPIENT);
                String emailAddress = (String) log.getParameters().get(MotechConstants.EMAIL_ADDRESS);
                String region = (String) log.getParameters().get(MotechConstants.REGION);
                mailService.scheduleMail(recipient, emailAddress, region);
            }
        }
    }
}
