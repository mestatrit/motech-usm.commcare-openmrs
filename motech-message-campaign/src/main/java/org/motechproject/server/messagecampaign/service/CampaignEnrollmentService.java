package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CampaignEnrollmentService {

    @Autowired
    AllCampaignEnrollments allCampaignEnrollments;

    public void register(CampaignEnrollment enrollment) {
        allCampaignEnrollments.saveOrUpdate(enrollment);   
    }

    public void unregister(String externalId, String campaignName) {
        CampaignEnrollment enrollment = allCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName);
        allCampaignEnrollments.remove(enrollment);
    }

    public void unregister(CampaignEnrollment enrollment) {
        allCampaignEnrollments.remove(enrollment);
    }

    public CampaignEnrollment findByExternalIdAndCampaignName(String externalId, String campaignName) {
        return allCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName);
    }
}