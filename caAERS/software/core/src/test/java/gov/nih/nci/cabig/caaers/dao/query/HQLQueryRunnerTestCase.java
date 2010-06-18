package gov.nih.nci.cabig.caaers.dao.query;

import gov.nih.nci.cabig.caaers.CaaersDbTestCase;
import gov.nih.nci.cabig.caaers.accesscontrol.query.impl.CaaersStudyIdFetcherImpl;
import gov.nih.nci.cabig.caaers.dao.ResearchStaffDao;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.security.CaaersSecurityFacadeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author: Biju Joseph
 */
public class HQLQueryRunnerTestCase extends CaaersDbTestCase {

    CaaersStudyIdFetcherImpl fetcher;
    long st;
    long en;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fetcher = (CaaersStudyIdFetcherImpl) getDeployedApplicationContext().getBean("studyIdFetcher");
        System.out.println("begining test.......\n");
        st = System.currentTimeMillis();

    }

    @Override
    protected void tearDown() throws Exception {

        en = System.currentTimeMillis();

        System.out.println("time " +(en-st));
        super.tearDown();
    }

    public void testStudyIDFetcher1(){


        StringBuilder hql = new StringBuilder("select so.study.id from StudyOrganization so where so.organization.id in (:orgIdSet)");
        HQLQuery query = new HQLQuery(hql.toString());
        query.getParameterMap().put("orgIdSet", orgIds());

        List<Integer> resultList = (List<Integer>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());
        System.out.println(new HashSet(resultList));
    }


    public void testStudyIDFetcher2(){


        //find all the studies where he is active
        StringBuilder hql = new StringBuilder("select so.study.id from StudyPersonnel sp ")
                .append("join sp.studyOrganization so ")
                .append("join sp.siteResearchStaff srs ")
                .append("join srs.researchStaff rs ")
                .append("where rs.loginId = :loginId ")
                .append("and sp.startDate<= :stDate ")
                .append("and (sp.endDate is null or sp.endDate >= :enDate ) " )
                .append("and sp.retiredIndicator <> true");

        Date d = new Date();
        HQLQuery query = new HQLQuery(hql.toString());
        query.getParameterMap().put("loginId", "aj1");
        query.getParameterMap().put("stDate", d);
        query.getParameterMap().put("enDate", d);
        System.out.println("before executing query");
        List<Integer> resultList = (List<Integer>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());
        System.out.println(new HashSet(resultList));
    }



    public void testParticipantIDFetcher1(){


        StringBuilder hql = new StringBuilder("select so.id from StudyOrganization so where " +
                " so.study in ( select sp.study from StudyFundingSponsor sp where sp.organization.id in (:orgIdSet) ) ");
        HQLQuery query = new HQLQuery(hql.toString());
        query.getParameterMap().put("orgIdSet",orgIds(1,5000));
        st = System.currentTimeMillis();
        List<Integer> resultList = (List<Integer>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());
        System.out.println(new HashSet(resultList));
    }



    public void testParticipantIDFetcher1_0(){


        StringBuilder hql = new StringBuilder("select distinct a.participant.id from  StudyOrganization so ,StudyParticipantAssignment a " +
                " join a.studySite ss where ss.study = so.study" +
                " and so.organization.id in (:orgIdSet)  ");

        HQLQuery query = new HQLQuery(hql.toString());
        query.getParameterMap().put("orgIdSet", Arrays.asList(new Integer[]{6}));

        List<Integer> resultList = (List<Integer>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());
        System.out.println(new HashSet(resultList));
    }



    public void testParticipantIDFetcher1_1(){


        StringBuilder hql = new StringBuilder("select distinct a.participant.id from  StudyOrganization so ,StudyParticipantAssignment a " +
                " join a.studySite ss  " +
                " join so.studyPersonnelsInternal sp " +
                " join sp.siteResearchStaff srs " +
                " join srs.researchStaff rs " +
                " where ss.study = so.study ") 
                .append(" and rs.loginId = :loginId ")
                .append(" and sp.startDate<= :stDate ")
                .append(" and (sp.endDate is null or sp.endDate >= :enDate ) " )
                .append(" and sp.retiredIndicator <> true");

        Date d = new Date();
        HQLQuery query = new HQLQuery(hql.toString());
       // query.getParameterMap().put("orgIdSet", Arrays.asList(new Integer[]{6}));
        query.getParameterMap().put("loginId", "aj1");
        query.getParameterMap().put("stDate", d);
        query.getParameterMap().put("enDate", d);

        List<Integer> resultList = (List<Integer>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());
        System.out.println(new HashSet(resultList));
        
    }

    public void testReportingPeriodIDFetcher_0(){
           StringBuilder hql = new StringBuilder("select distinct rp.id from  StudyOrganization so ,StudyParticipantAssignment a ")
                    .append(" join a.reportingPeriods rp ")
                    .append(" join a.studySite ss where ss.study = so.study ")
                    .append(" and ( ss.organization.id in (:orgIdSet) " )
                    .append(" or ( so.organization.id in (:orgIdSet) and so.class in ('SFS', 'SCC') )")
                    .append(")");
        HQLQuery query = new HQLQuery(hql.toString());
        query.getParameterMap().put("orgIdSet", Arrays.asList(new Integer[]{6}));
        //query.getParameterMap().put("loginId", "sponsor");
        st = System.currentTimeMillis();
        List<Integer> resultList = (List<Integer>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());
        System.out.println(new HashSet(resultList));

    }

    public void testOrganizationIDFetcher1_0(){


        StringBuilder hql = new StringBuilder("select distinct so.organization.id from  StudyOrganization so ,StudyPersonnel sp " +
                " join sp.studyOrganization ss  " +
                " join sp.siteResearchStaff srs " +
                " join srs.researchStaff rs " +
                " where ss.study = so.study ")
                .append(" and rs.loginId = :loginId ")
                .append(" and sp.startDate<= :stDate ")
                .append(" and (sp.endDate is null or sp.endDate >= :enDate ) " )
                .append(" and sp.retiredIndicator <> true");

        Date d = new Date();
        HQLQuery query = new HQLQuery(hql.toString());
       // query.getParameterMap().put("orgIdSet", Arrays.asList(new Integer[]{6}));
        query.getParameterMap().put("loginId", "sponsor");
        query.getParameterMap().put("stDate", d);
        query.getParameterMap().put("enDate", d);
        st = System.currentTimeMillis();
        List<Integer> resultList = (List<Integer>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());
        System.out.println(new HashSet(resultList));

    }


    public void testStudyQuery_1(){
        StringBuilder hql = new StringBuilder("select s from Study s , StudyIndex si")
                .append(" where si.loginId = :loginId")
                .append(" and si.study = s");
        HQLQuery query = new HQLQuery(hql.toString());
        query.getParameterMap().put("loginId", "2");

        st = System.currentTimeMillis();        
        List<Integer> resultList = (List<Integer>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());
        System.out.println(new HashSet(resultList));
    }



    public void testStudyQuery_2(){
        StringBuilder hql = new StringBuilder("select s from StudyIndex si join si.study s")
                .append(" where si.loginId = :loginId")
                .append(" and si.study = s");
        HQLQuery query = new HQLQuery(hql.toString());
        query.getParameterMap().put("loginId", "2");

        st = System.currentTimeMillis();
        List<Integer> resultList = (List<Integer>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());
        System.out.println(new HashSet(resultList));
    }


    public void testStudyAjaxableQuery(){

        //select study.id,study.shortTitle,identifier.value,identifier.primaryIndicator,study.phaseCode,study.status,sponsor.organization,study.externalId from Study study join StudyFundingSponsor sponsor left join study.identifiers as identifier


         StringBuffer hql = new StringBuffer("select study.id,study.shortTitle,identifier.value,")
            .append("identifier.primaryIndicator,study.phaseCode,study.status,")
            .append("study.externalId")
            .append(" from Study study");
           hql.append(" join study.studyOrganizations sponsor");
        
        hql.append(" left join study.identifiers as identifier ");
        hql.append(" where sponsor.class in ('SFS') " );

        HQLQuery query = new HQLQuery(hql.toString());

        st = System.currentTimeMillis();
        List<Object[]> resultList = (List<Object[]>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());

        for(int i = 0; i < resultList.size(); i++){
            System.out.println(Arrays.asList(resultList.get(i)));
        }
    }

    
    public void testIdentifiersFetchForInv(){
    	
    	Date d = new Date();
    	
    	StringBuilder hql = new StringBuilder("select distinct ids.value from StudyInvestigator sti "); 
					  hql.append("join sti.studyOrganization so ");
					  hql.append("join so.study s ");
					  hql.append("join s.identifiers ids ");
					  hql.append("join sti.siteInvestigator si ");
					  hql.append("join si.investigator i ");
					  hql.append("where i.loginId = :loginId and  ");
					  hql.append("sti.startDate<=:stDate and  ");
					  hql.append("( sti.endDate is null or sti.endDate >= :enDate ) and ");
					  hql.append("sti.retiredIndicator <> true and ");
					  hql.append("ids.type = 'Coordinating Center Identifier'");
    	
    	HQLQuery query = new HQLQuery(hql.toString());
        query.getParameterMap().put("loginId", "p.pater@web.de");
        query.getParameterMap().put("stDate", d);
        query.getParameterMap().put("enDate", d);
        st = System.currentTimeMillis();
        List<String> resultList = (List<String>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());
        System.out.println(new HashSet(resultList));
    	
    }
    
    public void testIdentifiersFetchForRs(){
    	
    	Date d = new Date();
    	
        StringBuilder hql = new StringBuilder("select distinct ids.value from StudyPersonnel sp ")
        .append("join sp.studyOrganization so ")
        .append("join so.study s ")
        .append("join s.identifiers ids ")
        .append("join sp.siteResearchStaff srs ")
        .append("join srs.researchStaff rs ")
        .append("where rs.loginId = :loginId ")
        .append("and sp.startDate<= :stDate ")
        .append("and (sp.endDate is null or sp.endDate >= :enDate ) " )
        .append("and sp.retiredIndicator <> true and ")
        .append("ids.type = 'Coordinating Center Identifier'");
    	
    	HQLQuery query = new HQLQuery(hql.toString());
        query.getParameterMap().put("loginId", "sponsor");
        query.getParameterMap().put("stDate", d);
        query.getParameterMap().put("enDate", d);
        st = System.currentTimeMillis();
        List<String> resultList = (List<String>) fetcher.search(query);
        System.out.println("result Count : " + resultList.size());
        System.out.println(new HashSet(resultList));
    	
    }
    
    
    public void testProvisionRs(){
    	CaaersSecurityFacadeImpl securityFacade = (CaaersSecurityFacadeImpl)getDeployedApplicationContext().getBean("caaersSecurityFacade");
    	ResearchStaffDao researchStaffDao = (ResearchStaffDao)getDeployedApplicationContext().getBean("researchStaffDao");
    	ResearchStaff rs = researchStaffDao.getByLoginId("whosbaum@yahoo.com");
    	securityFacade.provisionUser(rs);
    }
    

    private List<Integer> orgIds(){
       return orgIds(1, 5000);
    }

    private List<Integer> orgIds(int st, int en){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(int i = st; i < en; i++){
            ids.add(i);
        }
        return ids;
    }
}
