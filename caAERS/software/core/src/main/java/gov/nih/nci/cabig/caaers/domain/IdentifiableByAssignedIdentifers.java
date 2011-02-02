package gov.nih.nci.cabig.caaers.domain;

import java.util.List;

 
/**
 * The Interface IdentifiableByAssignedIdentifers.
 *
 * @author Rhett Sutphin
 */
public interface IdentifiableByAssignedIdentifers {
    
    /**
     * Gets the identifiers.
     *
     * @return the identifiers
     */
    List<Identifier> getIdentifiers();

    /**
     * Sets the identifiers.
     *
     * @param identifiers the new identifiers
     */
    void setIdentifiers(List<Identifier> identifiers);

    /**
     * Gets the primary identifier.
     *
     * @return the primary identifier
     */
    Identifier getPrimaryIdentifier();

    /**
     * Gets the secondary identifiers.
     *
     * @return the secondary identifiers
     */
    List<Identifier> getSecondaryIdentifiers();
}
