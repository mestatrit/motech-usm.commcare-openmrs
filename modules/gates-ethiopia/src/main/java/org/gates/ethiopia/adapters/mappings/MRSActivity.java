package org.gates.ethiopia.adapters.mappings;

import java.util.Map;

public class MRSActivity {

    private String type;
    private Map<String, String> idScheme;

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
}
