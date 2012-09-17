package org.gates.ethiopia.adapters.mappings;

import java.util.Map;

public class MRSActivity {

    private String type;
    private Map<String, String> idScheme;
    private Map<String, String> facilityScheme;
    private Map<String, String> providerScheme;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getIdScheme() {
        return idScheme;
    }

    public void setIdScheme(Map<String, String> idScheme) {
        this.idScheme = idScheme;
    }

    public Map<String, String> getFacilityScheme() {
        return facilityScheme;
    }

    public void setFacilityScheme(Map<String, String> facilityScheme) {
        this.facilityScheme = facilityScheme;
    }

    public Map<String, String> getProviderScheme() {
        return providerScheme;
    }

    public void setProviderScheme(Map<String, String> providerScheme) {
        this.providerScheme = providerScheme;
    }
}
