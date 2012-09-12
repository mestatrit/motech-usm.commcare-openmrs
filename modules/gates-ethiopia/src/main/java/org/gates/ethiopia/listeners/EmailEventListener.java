package org.gates.ethiopia.listeners;

import org.gates.ethiopia.constants.EventConstants;
import org.gates.ethiopia.constants.MotechConstants;
import org.gates.ethiopia.service.GatesEthiopiaMailService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

@Component
public class EmailEventListener {

    @Autowired
    private GatesEthiopiaMailService mailService;

    @Autowired
    private EventRelay eventRelay;

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @MotechListener(subjects = EventConstants.EMAIL_DELIVERY + ".*")
    public void sendEmail(MotechEvent event) {
        String email = (String) event.getParameters().get(MotechConstants.EMAIL_ADDRESS);
        String recipient = (String) event.getParameters().get(MotechConstants.RECIPIENT);
        String region = (String) event.getParameters().get(MotechConstants.REGION);
        try {
            mailService.sendReminderMail(recipient, email);
        } catch (MailException e) {
            logger.info("ERROR SENDING EMAIL FOR " + recipient + " TO: " + region + " DUE TO: " + e.getMessage());
            MotechEvent emailEvent = new MotechEvent(EventConstants.EMAIL_DELIVERY_FAILURE);
            event.getParameters().put(MotechConstants.RECIPIENT, recipient);
            event.getParameters().put(MotechConstants.EMAIL_ADDRESS, email);
            event.getParameters().put(MotechConstants.REGION, region);
            eventRelay.sendEventMessage(emailEvent);
        }
    }

}
