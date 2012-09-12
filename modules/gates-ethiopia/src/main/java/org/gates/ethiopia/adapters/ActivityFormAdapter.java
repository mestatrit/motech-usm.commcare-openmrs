package org.gates.ethiopia.adapters;

import org.gates.ethiopia.adapters.mappings.MRSActivity;
import org.motechproject.commcare.domain.CommcareForm;

public interface ActivityFormAdapter {

    void adaptForm(CommcareForm form, MRSActivity activity);

}
