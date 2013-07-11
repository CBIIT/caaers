/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.domain.AnatomicSite;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the Data access related operations for the AnatomicSite domain object.
 * 
 * @author kulasekaran
 * 
 */
@Transactional(readOnly = true)
public class AnatomicSiteDao extends CaaersDao<AnatomicSite> {

    private static final List<String> SUBSTRING_MATCH_PROPERTIES = Arrays.asList("name");
    private static final List<String> EXACT_MATCH_PROPERTIES = Collections.emptyList();
    private static final List<Object> EXTRA_PARAMS = Collections.emptyList();

    /**
     * Get the Class representation of the domain object that this DAO is representing.
     * 
     * @return Class representation of the domain object that this DAO is representing.
     */
    @Transactional(readOnly = true, propagation= Propagation.NOT_SUPPORTED)
    public Class<AnatomicSite> domainClass() {
        return AnatomicSite.class;
    }

    /**
     * Get the list of Anatomic sites matching the name fragments.
     * 
     * @param subnames
     *                the name fragments to search on.
     * @return List of matching Anatomic sites.
     */

    public List<AnatomicSite> getBySubnames(String[] subnames) {
        return findBySubname(subnames, null, EXTRA_PARAMS, SUBSTRING_MATCH_PROPERTIES, EXACT_MATCH_PROPERTIES);
    }

    public AnatomicSite findByName(String name){
        List<AnatomicSite> list =  findBySubname(new String[]{name}, null, Arrays.asList("name"));
        if(list != null && !list.isEmpty()) return list.get(0);
        return null;
    }

    public AnatomicSite findByNameAndCategory(String name, String category){
        List<AnatomicSite> list =  findBySubname(new String[]{name, category}, null, Arrays.asList("name", "category"));
        if(list != null && !list.isEmpty()) return list.get(0);
        return null;
    }

    /**
     * Get the list of all anatomic sites.
     * 
     * @return return the list of anatomic sites.
     */
    public List<AnatomicSite> getAll() {
        return findAll("o.name");
    }

}
