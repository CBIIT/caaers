package gov.nih.nci.cabig.caaers.domain.expeditedfields;

 
/**
 * The Class SectionNode.
 *
 * @author Rhett Sutphin
 */
class SectionNode extends PropertylessNode {
    
    /** The section. */
    private ExpeditedReportSection section;

    /**
     * Instantiates a new section node.
     *
     * @param section the section
     */
    public SectionNode(ExpeditedReportSection section) {
        this.section = section;
        // section.name() is legacy support. TODO: why not section.displayName?
        setDisplayNameCreator(new StaticDisplayNameCreator(section.name()));
    }

    /**
     * Gets the section.
     *
     * @return the section
     */
    public ExpeditedReportSection getSection() {
        return section;
    }
}
