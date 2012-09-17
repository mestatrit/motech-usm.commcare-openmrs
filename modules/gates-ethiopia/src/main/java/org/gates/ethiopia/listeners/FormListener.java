package org.gates.ethiopia.listeners;

import java.util.List;
import java.util.Map;
import org.gates.ethiopia.adapters.impl.AllFormsAdapter;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.commcare.service.CommcareFormService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormListener {

    @Autowired
    private CommcareFormService formService;

    @Autowired
    private AllFormsAdapter formsAdapter;

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @MotechListener(subjects = EventSubjects.FORM_STUB_EVENT)
    public void handleFormEvent(MotechEvent event) {

        Map<String, Object> parameters = event.getParameters();

        String receivedOn = (String) parameters.get(EventDataKeys.RECEIVED_ON);
        String formId = (String) parameters.get(EventDataKeys.FORM_ID);
        List<String> caseIds = (List<String>) parameters.get(EventDataKeys.CASE_IDS);

        logger.info("Received form: " + formId);

        CommcareForm form = null;

        if (formId != null && formId.trim().length() > 0) {
            form = formService.retrieveForm(formId);
        } else {
            logger.info("Form Id was null");
        }

        FormValueElement rootElement = null;

        if (form != null) {
            rootElement = form.getForm();
        }

        if (rootElement != null) {
            formsAdapter.adaptForm(form);
        } else {
            logger.info("Unable to adapt form");
        }
    }
}
