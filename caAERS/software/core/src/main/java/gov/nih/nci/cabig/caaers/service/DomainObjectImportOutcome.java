/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service;

import gov.nih.nci.cabig.caaers.validation.ValidationErrors;
import gov.nih.nci.cabig.ctms.domain.MutableDomainObject;

import java.util.ArrayList;
import java.util.List;


/*
* A wrapper around a MutableDomain object
* This class shows the status of the imported domain object.
*/
public class DomainObjectImportOutcome<T extends MutableDomainObject> {
    private List<Message> messages = new ArrayList<Message>();

    private T importedDomainObject;
    private boolean isSavable = true;
    private String context;

    public void ifNullObject(Object domainObject, Severity severity, String message) {
        if (domainObject == null) {
            addErrorMessage(message, severity);
        }
    }

    public enum Severity {
        ERROR, WARNING
    }

    public void addWarning(String code, Object... replacements){
        addWarning(code, null, replacements);
    }
    public void addWarning(String code, String msg, Object... replacements){
        addErrorMessage(code, msg, Severity.WARNING, replacements);
    }
    public void addError(String code, Object... replacements){
        addError(code, null, replacements);
    }
    public void addError(String code, String msg, Object... replacements){
        addErrorMessage(code, msg, Severity.ERROR, replacements);
    }

    public void addErrorMessage(String msg, Severity severity, Object... replacements) {
        addErrorMessage(null, msg, severity, replacements);
    }

    public void addErrorMessage(String code, String msg, Severity severity, Object... replacements) {
        if (severity == Severity.ERROR) {
            isSavable = false;
        }
        Message m = new Message(msg, severity, replacements);
        m.setCode(code);
        messages.add(m);
    }

    public List<Message> getErrorMessages(){
        List<Message> errors = new ArrayList<Message>();
        for(Message m : getMessages()) {
            if(m.getSeverity() == Severity.ERROR) errors.add(m);
        }
        return errors;
    }
    public boolean hasErrors() {
        return !getErrorMessages().isEmpty();
    }

    public ValidationErrors getValidationErrors(){
        ValidationErrors errors = new ValidationErrors();
        for(Message msg : getErrorMessages()){
           errors.addValidationError(msg.getCode(), msg.getMessage(), msg.getReplacements()) ;
        }
        return errors;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return messages.toString();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public T getImportedDomainObject() {
        return importedDomainObject;
    }

    public void setImportedDomainObject(T importedDomainObject) {
        this.importedDomainObject = importedDomainObject;
    }

    public boolean isSavable() {
        return isSavable;
    }

    public void setSavable(boolean isSavable) {
        this.isSavable = isSavable;
    }

    public static class Message {
        private String message;
        private Object[] replacements;

        private Severity severity;

        private String code;

        public Message(String message, Severity severity, Object... replacements) {
            this.message = message;
            this.severity = severity;
            this.replacements = replacements;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Severity getSeverity() {
            return severity;
        }

        public void setSeverity(Severity severity) {
            this.severity = severity;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Object[] getReplacements() {
            return replacements;
        }

        public void setReplacements(Object[] replacements) {
            this.replacements = replacements;
        }

        @Override
        public String toString() {
            return code + " : " + message;
        }
    }



    public void ifNullOrEmptyList(List list, Severity severity, String message) {
        if (list.isEmpty()) {
            this.addErrorMessage(message, severity);
        }
    }

}
