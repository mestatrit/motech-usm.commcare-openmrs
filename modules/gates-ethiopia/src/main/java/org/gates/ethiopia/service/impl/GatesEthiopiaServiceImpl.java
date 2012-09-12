package org.gates.ethiopia.service.impl;

import java.util.Properties;
import java.util.Random;
import org.gates.ethiopia.constants.EventConstants;
import org.gates.ethiopia.constants.MotechConstants;
import org.gates.ethiopia.service.GatesEthiopiaMailService;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class GatesEthiopiaServiceImpl implements GatesEthiopiaMailService {

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @Autowired
    private JavaMailSender mailService;

    @Autowired
    private MotechSchedulerService schedulerService;

    @Autowired
    @Qualifier(value = "emailSettings")
    private Properties emailSettings;

    @Override
    public void sendReminderMail(String externalId, String emailAddress) {
        logger.info("Sending mail for " + externalId + " to " + emailAddress);

        String[] woredaFacility = externalId.split("[.]");

        String woreda = woredaFacility[0];

        String facility = woredaFacility[1];

        String body = getMailBody(MotechConstants.REMINDER_BODY);
        String subject = getMailSubject(MotechConstants.REMINDER_SUBJECT);

        body = body.replace(MotechConstants.WOREDA_PLACEHOLDER, woreda).replace(MotechConstants.FACILITY_PLACEHOLDER,
                facility);
        subject = subject.replace(MotechConstants.WOREDA_PLACEHOLDER, woreda).replace(
                MotechConstants.FACILITY_PLACEHOLDER, facility);

        SimpleMailMessage emailReminder = buildEmail(emailAddress, subject, body);
        mailService.send(emailReminder);
    }

    private SimpleMailMessage buildEmail(String recipient, String subject, String body) {
        SimpleMailMessage reminderMessage = new SimpleMailMessage();
        reminderMessage.setTo(recipient);
        reminderMessage.setText(body);
        reminderMessage.setSubject(subject);

        return reminderMessage;

    }

    private String getMailBody(String mailBody) {
        return emailSettings.getProperty(mailBody);
    }

    private String getMailSubject(String mailSubject) {
        return emailSettings.getProperty(mailSubject);
    }

    @Override
    public void scheduleMail(String recipient, String emailAddress, String region) {
        Random randomMinutes = new Random();
        int minutesToAdd = 1 + randomMinutes.nextInt(MotechConstants.RANDOM_MINUTES);
        DateTime dateToSend = DateTime.now().plusMinutes(minutesToAdd);
        MotechEvent sendEmailEvent = new MotechEvent(EventConstants.EMAIL_DELIVERY + "." + recipient + "." + emailAddress + "." + dateToSend.toString());
        
        sendEmailEvent.getParameters().put(MotechConstants.RECIPIENT, recipient);
        sendEmailEvent.getParameters().put(MotechConstants.EMAIL_ADDRESS, emailAddress);
        sendEmailEvent.getParameters().put(MotechConstants.REGION, region);
        
        RunOnceSchedulableJob emailJob = new RunOnceSchedulableJob(sendEmailEvent, dateToSend.toDate());
        logger.info("Scheduling mail for " + recipient + " to " + emailAddress + " at " + dateToSend.toString());
        schedulerService.scheduleRunOnceJob(emailJob);
    }

}
