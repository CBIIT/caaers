/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.domain.*;

import java.util.ArrayList;
import java.util.List;

/*
 * @author Ion C. Olaru
 * 
 */
public class AgentCommand {

    private Agent agent;

    private Term terminology;
    private Ctc ctcVersion;
    private MeddraVersion meddraVersion;

    private List<AgentSpecificTerm> agentSpecificTerms;

    public void save() {
        
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public List<AgentSpecificTerm> getAgentSpecificTerms() {
        return agentSpecificTerms;
    }

    public void setAgentSpecificTerms(List<AgentSpecificTerm> agentSpecificTerms) {
        this.agentSpecificTerms = agentSpecificTerms;
    }

    public Ctc getCtcVersion() {
        return ctcVersion;
    }

    public void setCtcVersion(Ctc ctcVersion) {
        this.ctcVersion = ctcVersion;
    }

    public MeddraVersion getMeddraVersion() {
        return meddraVersion;
    }

    public void setMeddraVersion(MeddraVersion meddraVersion) {
        this.meddraVersion = meddraVersion;
    }

    public Term getTerminology() {
        return terminology;
    }

    public void setTerminology(Term terminology) {
        this.terminology = terminology;
    }
}
