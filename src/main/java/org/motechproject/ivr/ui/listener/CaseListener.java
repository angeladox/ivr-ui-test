package org.motechproject.ivr.ui.listener;

import java.util.Map;

import org.joda.time.DateTime;
import org.motechproject.commcare.events.CaseEvent;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.ui.domain.EnrollmentRequest;
import org.motechproject.ivr.ui.mrs.MrsEntityFacade;
import org.motechproject.ivr.ui.support.IVRUIEnroller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MOTECH Listener to handle Case forwarding from Commcare. Currently, the ivr ui test
 * supports a single registration form. The only fields that are relevant for
 * the demo are pin and phone number. When the form is received,
 * this listener will create a new dumby patient within Couch
 * and attach as attributes to that patient, the pin and phone number entered on
 * the form. 
 */
@Component
public class CaseListener {

    private final IVRUIEnroller enroller;
    private final MrsEntityFacade mrsEntityFacade;

    @Autowired
    public CaseListener(IVRUIEnroller enroller, MrsEntityFacade mrsEntityFacade) {
        this.enroller = enroller;
        this.mrsEntityFacade = mrsEntityFacade;
    }

    @MotechListener(subjects = EventSubjects.CASE_EVENT)
    public void handleCase(MotechEvent event) {
        CaseEvent caseEvent = new CaseEvent(event);
        Map<String, String> caseValues = caseEvent.getFieldValues();
        String motechId = caseValues.get(CommcareConstants.PATIENT_NUMBER_CASE_ELEMENT);
        mrsEntityFacade.createDumbyPatient(motechId);
        enrollInCalls(caseValues, motechId);
    }

    private void enrollInCalls(Map<String, String> caseValues, String motechId) {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setMotechId(motechId);
        request.setPhonenumber(caseValues.get(CommcareConstants.PHONE_NUMBER_CASE_ELEMENT));
        request.setPin(caseValues.get(CommcareConstants.PIN_CASE_ELEMENT));

        DateTime dateTime = DateUtil.now().plusMinutes(2);
        request.setCallStartTime(String.format("%02d:%02d", dateTime.getHourOfDay(), dateTime.getMinuteOfHour()));

        enroller.enrollPatientWithId(request);
    }

}
