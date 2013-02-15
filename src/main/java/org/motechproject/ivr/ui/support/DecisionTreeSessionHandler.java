package org.motechproject.ivr.ui.support;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.ivr.ui.mrs.CouchMrsConstants;
import org.motechproject.ivr.ui.mrs.CouchPersonUtil;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.domain.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecisionTreeSessionHandler {

    private final CouchPersonUtil couchPersonUtil;
    private final FlowSessionService flowSessionService;

    @Autowired
    public DecisionTreeSessionHandler(CouchPersonUtil couchPersonUtil, FlowSessionService flowSessionService) {
        this.couchPersonUtil = couchPersonUtil;
        this.flowSessionService = flowSessionService;
    }

    public boolean updateFlowSessionIdToVerboiceId(String oldSessionId, String newSessionId) {
        FlowSession session = flowSessionService.getSession(oldSessionId);
        if (session == null) {
            return false;
        }
        flowSessionService.updateSessionId(oldSessionId, newSessionId);
        return true;
    }

    public boolean digitsMatchPin(String sessionId, String digits) {
        String motechID = getMotechIdForSessionWithId(sessionId);
        if (couchPersonUtil.getPersonByID(motechID) != null) {
            CouchPerson person = couchPersonUtil.getPersonByID(motechID);
            String pin = readPinAttributeValue(person);
            if (StringUtils.isNotBlank(digits) && digits.equals(pin)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private String readPinAttributeValue(CouchPerson person) {
        List<Attribute> attrs = person.getAttributes();
        String pin = null;
        for (Attribute attr : attrs) {
            if (CouchMrsConstants.PERSON_PIN.equals(attr.getName())) {
                pin = attr.getValue();
            }
        }
        return pin;
    }

    public String getMotechIdForSessionWithId(String sessionId) {
        FlowSession session = flowSessionService.getSession(sessionId);
        return session.get(CallRequestDataKeys.MOTECH_ID);
    }

}
