package org.motechproject.ivr.ui.support;

import org.motechproject.ivr.ui.domain.IVRUIResponse;

public interface IVRUITests {

    IVRUIResponse findIVRUITestByMotechId(String motechId);

    void deletePillReminder(String motechId);

    void setDosageStatusKnownForPatient(String motechId);

    boolean isPatientInPillRegimen(String motechId);

    String registerNewPatientIntoPillRegimen(String motechId, String dosageStartTime);

}
