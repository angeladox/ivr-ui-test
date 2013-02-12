package org.motechproject.ivr.ui.listener;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.ui.events.Events;
import org.motechproject.ivr.ui.mrs.MrsEncounterCreator;
import org.motechproject.ivr.ui.support.DecisionTreeSessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MOTECH Listener that creates a new encounter for a patient, and adds an
 * observation for that encounter indicating the patient took their dosage
 */
@Component
public class UpdateMrsListener {

    private MrsEncounterCreator encounterUpdater;
    private DecisionTreeSessionHandler decisionTreeSessionHandler;

    @Autowired
    public UpdateMrsListener(MrsEncounterCreator encounterUpdater, DecisionTreeSessionHandler decisionTreeSessionHandler) {
        this.encounterUpdater = encounterUpdater;
        this.decisionTreeSessionHandler = decisionTreeSessionHandler;
    }

    @MotechListener(subjects = Events.PATIENT_TOOK_DOSAGE)
    public void handleMrsUpdate(MotechEvent event) {
        String motechId = decisionTreeSessionHandler.getMotechIdForSessionWithId(event.getParameters()
                .get("flowSessionId").toString());
        encounterUpdater.createPillTakenEncounterForPatient(motechId);
    }

}
