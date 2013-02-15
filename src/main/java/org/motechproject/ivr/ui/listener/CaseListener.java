package org.motechproject.ivr.ui.listener;

import java.util.Map;

import org.joda.time.DateTime;
import org.motechproject.commcare.events.CaseEvent;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.ui.domain.EnrollmentRequest;
import org.motechproject.ivr.ui.mrs.CouchPersonUtil;
import org.motechproject.ivr.ui.support.IVRUIEnroller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MOTECH Listener to handle Case forwarding from Commcare. Currently, the ivr ui test
 * supports a single registration form. The only fields that are relevant for
 * the demo are pin and phone number. When the form is received,
 * this listener will create a new dumby patient within Couch
 * and attaches as attributes the pin and phone number entered on
 * the form to that person.
 */
@Component
public class CaseListener {

    private final IVRUIEnroller enroller;
    private final CouchPersonUtil couchPersonUtil;

    @Autowired
    public CaseListener(IVRUIEnroller enroller, CouchPersonUtil couchPersonUtil) {
        this.enroller = enroller;
        this.couchPersonUtil = couchPersonUtil;
    }

    @MotechListener(subjects = EventSubjects.CASE_EVENT)
    public void handleCase(MotechEvent event) {
        CaseEvent caseEvent = new CaseEvent(event);
        Map<String, String> caseValues = caseEvent.getFieldValues();      
        CouchPerson person = couchPersonUtil.createAndSavePerson(caseValues.get(CommcareConstants.PHONE_NUMBER_CASE_ELEMENT), CommcareConstants.PIN_CASE_ELEMENT);
        enrollInCalls(person);
    }

    private void enrollInCalls(CouchPerson person) {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setPhoneNumber(couchPersonUtil.getAttribute(person, CommcareConstants.PHONE_NUMBER_CASE_ELEMENT).getValue());
        request.setPin(couchPersonUtil.getAttribute(person, CommcareConstants.PIN_CASE_ELEMENT).getValue());
        request.setMotechID(person.getId());
        DateTime dateTime = DateUtil.now().plusMinutes(2);
        request.setCallStartTime(String.format("%02d:%02d", dateTime.getHourOfDay(), dateTime.getMinuteOfHour()));
        enroller.enrollPerson(request);
    }   

}
