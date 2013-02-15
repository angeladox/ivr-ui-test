package org.motechproject.ivr.ui.domain;

public class EnrollmentRequest {

    private String pin;
    private String phonenumber;
    private String motechID;

    private String callStartTime;

    public String getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(String callStartTime) {
        this.callStartTime = callStartTime;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhoneNumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
    
    public String getMotechID() {
        return motechID;
    }

    public void setMotechID(String motechID) {
        this.motechID = motechID;
    }


}
