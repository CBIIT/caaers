/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao.query;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class InvestigatorQuery extends AbstractQuery {

    private static String queryString = "SELECT distinct i from Investigator i";
    
    private static String FIRST_NAME = "firstName";
    private static String LAST_NAME = "lastName";
    private static String NCI_CODE = "nciIdentifier";
    private static String EMAIL_ADDRESS = "emailAddress";
    private static String LOGIN_ID = "loginId";
    private static String ORGANIZATION = "organization";
    private static String ORGANIZATIONS = "organizations";
    private static String USER_NAME = "userName";

    public InvestigatorQuery() {

        super(queryString);
        leftJoinFetch("i.siteInvestigatorsInternal si");
        orderBy("i.id");
    }
    
    
    public void excludeHavingId(Integer id){
    	if(id == null) return;
    	andWhere(" i.id != :iid");
    	setParameter("iid", id);
    }

    public void filterByName(final String name) {
        String[] searchFields = StringUtils.split(name);
        int i = 0;
        if (searchFields != null) {
            for (String searchField : searchFields) {
                String searchString = "%" + StringUtils.lowerCase(searchField) + "%";
                String firstNameKey = "FIRST_NAME_" + i;
                String lastNameKey = "LAST_NAME_" + i;
                String middleNameKey = "MIDDLE_NAME_" + i;
                andWhere(String.format("(lower(i.firstName) LIKE :%s OR lower(i.lastName) LIKE :%s OR lower(i.middleName) LIKE :%s)",
                        firstNameKey, lastNameKey, middleNameKey));
                setParameter(firstNameKey, searchString);
                setParameter(lastNameKey, searchString);
                setParameter(middleNameKey, searchString);
                i++;
            }
        }
    }

    public void filterByFirstName(final String firstName) {
        String searchString = "%" + firstName.toLowerCase() + "%";
        andWhere("lower(i.firstName) LIKE :" + FIRST_NAME);
        setParameter(FIRST_NAME, searchString);
    }

    public void filterByLastName(final String lastName) {
        String searchString = "%" + lastName.toLowerCase() + "%";
        andWhere("lower(i.lastName) LIKE :" + LAST_NAME);
        setParameter(LAST_NAME, searchString);
    }
    
    public void filterByEmailAddress(final String emailAddress) {
        String searchString = "%" + emailAddress.trim().toLowerCase() + "%";
        andWhere("lower(i.emailAddress) LIKE :" + EMAIL_ADDRESS);
        setParameter(EMAIL_ADDRESS, searchString);
    }

    public void filterByNciIdentifier(final String value) {
        String searchString = "%" + value.toLowerCase() + "%";
        andWhere("lower(i.nciIdentifier) LIKE :" + NCI_CODE);
        setParameter(NCI_CODE, searchString);
    }
    public void filterByNciIdentifierExactMatch(final String value) {
        String searchString = value.toLowerCase();
        andWhere("lower(i.nciIdentifier) LIKE :" + NCI_CODE);
        setParameter(NCI_CODE, searchString);
    }
    public void filterByLoginId(final String loginId) {
        join("i.caaersUser u");
        String searchString = "%" + loginId.trim().toLowerCase() + "%";
        andWhere(String.format("lower(u.loginName) LIKE :%s", LOGIN_ID));
        setParameter(LOGIN_ID, searchString);
    }
    
    public void filterByExactLoginId(final String... loginIds) {
         join("i.caaersUser u");
         if(loginIds.length > 1){
            andWhere("u.loginName in (:loginIds)");
            setParameterList("loginIds", Arrays.asList(loginIds));
         }else{
            String searchString = loginIds[0].trim().toLowerCase();
            andWhere(String.format("lower(u.loginName) = :%s", LOGIN_ID));
            setParameter(LOGIN_ID, searchString);
         }

    }
    
    public void filterByOrganization(final String organization) {
        String searchString = organization.trim();
        andWhere("si.organization.id =:" + ORGANIZATION);
        setParameter(ORGANIZATION, Integer.parseInt(searchString));
    }

    public void filterByUserName(final String value) {
    	join("i.caaersUser cu");
        String searchString = "%" + value.toLowerCase() + "%";
        andWhere("lower(cu.loginName) LIKE :" + USER_NAME);
        setParameter(USER_NAME, searchString);
    }


    public void excludeUsers(){
        andWhere("i.caaersUser is null");
    }


	public void filterByOrganizations(List list) {
        andWhere("si.organization.id in (:" + ORGANIZATIONS + ")");
        setParameterList(ORGANIZATIONS, list);
	}
}
