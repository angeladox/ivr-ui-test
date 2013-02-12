package org.motechproject.ivr.ui.domain;

import org.motechproject.mrs.domain.Patient;

public class MrsPatientSearchResult {

    private String motechId;

 
    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public static MrsPatientSearchResult fromMrsPatient(Patient findPatientByMotechId) {
        MrsPatientSearchResult result = new MrsPatientSearchResult();
        if (findPatientByMotechId != null) {
            result.motechId = findPatientByMotechId.getMotechId();
        }
        return result;
    }

}
