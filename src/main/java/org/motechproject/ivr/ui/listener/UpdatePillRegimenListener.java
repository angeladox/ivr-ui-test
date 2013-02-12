package org.motechproject.ivr.ui.listener;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.ui.events.Events;
import org.motechproject.ivr.ui.support.DecisionTreeSessionHandler;
import org.motechproject.ivr.ui.support.PillReminders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MOTECH Listener that updates the daily pill regimen dosage status
 */
@Component
public class UpdatePillRegimenListener {

    private PillReminders pillReminders;
    private DecisionTreeSessionHandler decisionTreeSessionHandler;

    @Autowired
    public UpdatePillRegimenListener(PillReminders pillReminders, DecisionTreeSessionHandler decisionTreeSessionHandler) {
        this.pillReminders = pillReminders;
        this.decisionTreeSessionHandler = decisionTreeSessionHandler;
    }

    @MotechListener(subjects = Events.PATIENT_TOOK_DOSAGE)
    public void handleDosageTaken(MotechEvent event) {
        String motechId = decisionTreeSessionHandler.getMotechIdForSessionWithId(event.getParameters()
                .get("flowSessionId").toString());

        pillReminders.setDosageStatusKnownForPatient(motechId);
    }
}
