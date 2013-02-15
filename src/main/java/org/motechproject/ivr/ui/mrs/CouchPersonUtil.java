package org.motechproject.ivr.ui.mrs;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.motechproject.couch.mrs.impl.CouchPersonAdapter;
import org.motechproject.couch.mrs.model.CouchAttribute;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.mrs.domain.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Convenience class to create CouchMRS Person objects
 */
@Component
public class CouchPersonUtil {   
    
    private final CouchPersonAdapter couchPersonAdapter;

    @Autowired
    public CouchPersonUtil(CouchPersonAdapter couchPersonAdapter){
            this.couchPersonAdapter = couchPersonAdapter;
    }

    public CouchPerson createAndSavePerson(String phoneNum, String pin) {
        CouchPerson person = new CouchPerson(); 
        person.setId(UUID.randomUUID().toString());
        setAttribute(person, phoneNum, CouchMrsConstants.PHONE_NUMBER);
        setAttribute(person, pin, CouchMrsConstants.PERSON_PIN);
        couchPersonAdapter.addPerson(person);
        return person;
    }
    
    public CouchPerson getPersonByID(String motechID){
        ArrayList<CouchPerson> allPersons = (ArrayList<CouchPerson>) couchPersonAdapter.findAllPersons();
        for(CouchPerson person: allPersons){
            if (person.getId().matches(motechID)){
                return person;
            }
        }
        return null;
    }
    
    public CouchPerson getPersonByPhoneNumber(String phoneNum){
        ArrayList<CouchPerson> allPersons = (ArrayList<CouchPerson>) couchPersonAdapter.findAllPersons();
        for(CouchPerson person: allPersons){
            if (getAttribute(person, CouchMrsConstants.PHONE_NUMBER).getValue().matches(phoneNum)){
                return person;
            }
        }
        return null;
    }
    
    private void setAttribute(CouchPerson person, String attrValue, String attrName) {
        Iterator<Attribute> attrs = person.getAttributes().iterator();
        while (attrs.hasNext()) {
            Attribute attr = attrs.next();
            if (attrName.equalsIgnoreCase(attr.getName())) {
                attrs.remove();
                break;
            }
        }
        person.getAttributes().add(new CouchAttribute(attrName, attrValue));
    }
    
    public Attribute getAttribute(CouchPerson person, String attrName) {
        Iterator<Attribute> attrs = person.getAttributes().iterator();
        while (attrs.hasNext()) {
            Attribute attr = attrs.next();
            if (attrName.equalsIgnoreCase(attr.getName())) {
                return attr;
            }
        }
        return null;
    }
    
}
