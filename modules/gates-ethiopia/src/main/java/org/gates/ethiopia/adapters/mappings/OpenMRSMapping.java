package org.gates.ethiopia.adapters.mappings;

public class OpenMRSMapping {

    private String formName;
    private String xmlns;
    private MRSActivity[] activities;
    public String getFormName() {
        return formName;
    }
    public void setFormName(String formName) {
        this.formName = formName;
    }
    public MRSActivity[] getActivities() {
        return activities;
    }
    public void setActivities(MRSActivity[] activities) {
        this.activities = activities;
    }
    public String getXmlns() {
        return xmlns;
    }
    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    
}
