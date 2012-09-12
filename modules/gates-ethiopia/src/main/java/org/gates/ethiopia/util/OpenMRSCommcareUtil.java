package org.gates.ethiopia.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gates.ethiopia.constants.FormMappingConstants;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSEncounter.MRSEncounterBuilder;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSCommcareUtil {

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @Autowired
    private MRSEncounterAdapter mrsEncounterAdapter;

    @Autowired
    private CommcareCaseService caseService;

    @Autowired
    private MRSFacilityAdapter mrsFacilityAdapter;

    @Autowired
    private MRSUserAdapter mrsUserAdapter;

    @Autowired
    private MRSPatientAdapter mrsPatientAdapter;

    public MRSPerson findProvider(String providerName) {

        MRSUser provider = mrsUserAdapter.getUserByUserName(providerName);

        if (provider == null) {
            return null;
        }

        return provider.getPerson();
    }

    public MRSFacility findFacility(String location) {
        List<MRSFacility> facilities = null;
        try {
            facilities = mrsFacilityAdapter.getFacilities(location);
        } catch (MRSException e) {
            return null;
        }
        if (facilities.size() == 0) {
            return null;
        } else if (facilities.size() > 1) {
            logger.info("Multiple facilities, returning facility with ID: " + facilities.get(0).getId());
        }

        return facilities.get(0);
    }

    public String getCaseId(FormValueElement formValueElement, String openMrsPatientIdentifier) {

        String caseId = formValueElement.getAttributes().get(FormMappingConstants.CASE_ID_ATTRIBUTE);

        CaseInfo caseInfo = caseService.getCaseByCaseId(caseId);

        return caseInfo.getFieldValues().get(openMrsPatientIdentifier);
    }

    public void addEncounter(MRSPatient patient, Set<MRSObservation> observations, String providerName,
            Date encounterDate, String facilityName, String encounterType) {

        MRSFacility facility = findFacility(facilityName);

        MRSPerson provider = findProvider(providerName);

        logger.info("Using provider: " + provider);

        MRSEncounterBuilder builder = new MRSEncounterBuilder();

        MRSEncounter mrsEncounter = builder.withProvider(provider).withFacility(facility).withDate(encounterDate).withPatient(patient).withEncounterType(encounterType).withObservations(observations).build();

        try {
            mrsEncounterAdapter.createEncounter(mrsEncounter);
            logger.info("Encounter saved");
        } catch (MRSException e) {
            logger.warn("Could not save encounter");
        }
    }

    public MRSPatient getPatientByMotechId(String motechId) {
        return mrsPatientAdapter.getPatientByMotechId(motechId);
    }

    public String retrieveId(Map<String, String> idScheme, FormValueElement element) {
        String idSchemeType = idScheme.get(FormMappingConstants.ID_SCHEME_TYPE);
        String idFieldName = idScheme.get(FormMappingConstants.ID_SCHEME_FIELD);

        String motechId = null;

        if (idSchemeType.equals(FormMappingConstants.DEFAULT_ID_SCHEME)) {
            motechId = element.getElementByName(idFieldName).getValue();
        } else if (idSchemeType.equals(FormMappingConstants.COMMCARE_ID_SCHEME)) {
            motechId = getCaseId(element.getElementByNameIncludeCase(FormMappingConstants.CASE_ELEMENT), idFieldName);
        }

        return motechId;
    }
}
