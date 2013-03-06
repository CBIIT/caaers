/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.rule.notification;

import gov.nih.nci.cabig.caaers.web.rule.RuleInputCommand;

import java.rmi.RemoteException;

import org.springframework.web.multipart.MultipartFile;

public class ImportReportDefinitionCommand implements RuleInputCommand {

    private String folder;
    private String message;
    private String errorMessage;
    
    private boolean updated = false;

    private MultipartFile ruleSetFile1;

    public MultipartFile getRuleSetFile1() {
        return ruleSetFile1;
    }

    public void setRuleSetFile1(MultipartFile ruleSetFile1) {
        this.ruleSetFile1 = ruleSetFile1;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public ImportReportDefinitionCommand() throws RemoteException {
        // ruleSets = ruleAuthoringService.getAllRuleSets();
        // ruleSets.remove(0);//removing the default
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
