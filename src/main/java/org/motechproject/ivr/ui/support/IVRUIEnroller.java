package org.motechproject.ivr.ui.support;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.ivr.ui.IVRUISettings;
import org.motechproject.ivr.ui.domain.EnrollmentRequest;
import org.motechproject.ivr.ui.domain.EnrollmentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IVRUIEnroller {
    
    private final IVRService ivrService;
    private final IVRUISettings settings;
    
    @Autowired
    public IVRUIEnroller(IVRService ivrService, IVRUISettings settings) {
        this.ivrService = ivrService;
        this.settings = settings;
    }

    /*
     * Enrolls a person in the test calls. The request contains the phone
     * number, pin, motech ID, and call start time.
     */
    public EnrollmentResponse enrollPerson(EnrollmentRequest request) {
        EnrollmentResponse response = new EnrollmentResponse();

        String phoneNum = request.getPhonenumber();
        if (phoneNum == null) {
            response.addError("Phone Number with digits: " + request.getPhonenumber() + " not found.");
            return response;
        }

        String pin = request.getPin();
        if (pin == null) {
            response.addError("Pin with digits: " + request.getPin() + " not found.");
            return response;
        }
        
        String motechID = request.getMotechID();
        if (motechID == null) {
            response.addError("Motech ID with digits: " + request.getMotechID() + " was not found.");
            return response;
        }

        String actualStartTime = request.getCallStartTime();
        response.setStartTime(actualStartTime);

        initiateCall(phoneNum, motechID);

        return response;
    }

    private void initiateCall(String phoneNum, String motechID) {
        CallRequest callRequest = new CallRequest(phoneNum, 120, settings.getVerboiceChannelName());

        Map<String, String> payload = callRequest.getPayload();
        
        // it's important that we store a motech id in the call request
        // payload. The verboice ivr service will copy all payload data to the
        // flow session so that we can retrieve it at a later time
        payload.put(CallRequestDataKeys.MOTECH_ID, motechID);

        // the callback_url is used once verboice starts a call to retrieve the
        // data for the call (e.g. TwiML)
        String callbackUrl = settings.getMotechUrl() + "/module/ivr-ui-test/ivr/start?motech_call_id=%s";
        try {
            payload.put(CallRequestDataKeys.CALLBACK_URL,
                    URLEncoder.encode(String.format(callbackUrl, callRequest.getCallId()), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }

        ivrService.initiateCall(callRequest);
    }

}
