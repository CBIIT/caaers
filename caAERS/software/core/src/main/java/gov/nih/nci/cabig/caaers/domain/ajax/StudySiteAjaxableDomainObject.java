package gov.nih.nci.cabig.caaers.domain.ajax;



/**
 *
 */
public class StudySiteAjaxableDomainObject extends AbstractAjaxableDomainObject {


    private String name;
    private String nciInstituteCode;
    private Integer studyId;
    private String type;
    private String primaryId;
    private String studyShortTitle;
    private String status;
    private String studyPhase;


    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public String getNciInstituteCode() {
		return nciInstituteCode;
	}

	public void setNciInstituteCode(String nciInstituteCode) {
		this.nciInstituteCode = nciInstituteCode;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getDisplayName(){
    	StringBuffer displayNameBuffer = new StringBuffer();
    	displayNameBuffer.append(name);
    	if(nciInstituteCode != null && !nciInstituteCode.equals("")){
    		displayNameBuffer.append("(");
    		displayNameBuffer.append(nciInstituteCode);
    		displayNameBuffer.append(")");
    	}
    	return displayNameBuffer.toString();
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    public String getStudyShortTitle() {
        return studyShortTitle;
    }

    public void setStudyShortTitle(String studyShortTitle) {
        this.studyShortTitle = studyShortTitle;
    }

    public String getStudyPhase() {
        return studyPhase;
    }

    public void setStudyPhase(String studyPhase) {
        this.studyPhase = studyPhase;
    }

    public boolean equals(Object arg0) {
        if (arg0 == null) {
            return false;
        }

        if (!(arg0 instanceof StudySiteAjaxableDomainObject)) {
            return false;
        }

        StudySiteAjaxableDomainObject other = (StudySiteAjaxableDomainObject) arg0;

        if (this.getNciInstituteCode().equals(other.getNciInstituteCode())) {
            return true;
        }

        return false;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}