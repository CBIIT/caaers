/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.domain.ConverterOrganization;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class OrganizationConverterDao extends CaaersDao<ConverterOrganization>{

	@Override
	@Transactional(readOnly = true, propagation= Propagation.NOT_SUPPORTED)
	public Class<ConverterOrganization> domainClass() {
		return ConverterOrganization.class;
	}
	
    @Transactional(readOnly = false)
	public void save(ConverterOrganization org){
		getHibernateTemplate().saveOrUpdate(org);
	}
	
    @SuppressWarnings("unchecked")
	public ConverterOrganization getByName(final String name) {
        List<ConverterOrganization> results = getHibernateTemplate().find("from ConverterOrganization where name= ?",
                        name);
        return results.size() > 0 ? results.get(0) : null;
    }
}
