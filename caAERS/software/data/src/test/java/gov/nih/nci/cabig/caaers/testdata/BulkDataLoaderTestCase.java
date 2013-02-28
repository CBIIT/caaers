/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.testdata;

import edu.nwu.bioinformatics.commons.testing.DbTestCase;
import gov.nih.nci.cabig.caaers.CaaersDbNoSecurityTestCase;
import gov.nih.nci.cabig.caaers.testdata.generator.XMLGenerator;
import gov.nih.nci.cabig.caaers.testdata.generator.ae.AdverseEventXMLGenerator;
import gov.nih.nci.cabig.caaers.testdata.generator.investigator.InvestigatorXMLGenerator;
import gov.nih.nci.cabig.caaers.testdata.generator.participant.ParticipantXMLGenerator;
import gov.nih.nci.cabig.caaers.testdata.generator.researchstaff.ResearchStaffForMultipleSitesXMLGenerator;
import gov.nih.nci.cabig.caaers.testdata.generator.researchstaff.ResearchStaffXMLGenerator;
import gov.nih.nci.cabig.caaers.testdata.generator.study.StudyXMLGenerator;
import gov.nih.nci.cabig.caaers.testdata.loader.DataLoader;
import gov.nih.nci.cabig.caaers.testdata.loader.ae.AdverseEventLoader;
import gov.nih.nci.cabig.caaers.testdata.loader.investigator.InvestigatorLoader;
import gov.nih.nci.cabig.caaers.testdata.loader.participant.ParticipantLoader;
import gov.nih.nci.cabig.caaers.testdata.loader.researchstaff.ResearchStaffLoader;
import gov.nih.nci.cabig.caaers.testdata.loader.study.StudyLoader;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Has methods to generate and load data.
 * @author: Biju Joseph
 */
public class BulkDataLoaderTestCase extends CaaersDbNoSecurityTestCase{


    /**
     * Generate and load investigator
     */
    public void testLoadInvestigators(){

       try{
           TestDataFileUtils.deleteDirectory(TestDataFileUtils.getInvestigatorTestDataFolder());
           new GeneratorExecuter(new InvestigatorXMLGenerator(), "Investigator").execute();
           new DataLoadExecuter(new InvestigatorLoader(getDeployedApplicationContext()), "Investigator").execute();
       }catch(Exception e){
         e.printStackTrace();
       }
    }

    /**
     * Generate and load study
     */
    public void testLoadStudy(){
     try{
           TestDataFileUtils.deleteDirectory(TestDataFileUtils.getStudyTestDataFolder());
           new GeneratorExecuter(new StudyXMLGenerator(), "Study").execute();
           new DataLoadExecuter(new StudyLoader(getDeployedApplicationContext()), "Study").execute();
       }catch(Exception e){
         e.printStackTrace();
       }
    }

    /**
     * Generate and load research staff
     */
    public void testLoadResearchStaff(){
     try{
           TestDataFileUtils.deleteDirectory(TestDataFileUtils.getResearchStaffTestDataFolder());
           new GeneratorExecuter(new ResearchStaffXMLGenerator(), "Research staff").execute();
           new DataLoadExecuter(new ResearchStaffLoader(getDeployedApplicationContext()), "Research staff").execute();
       }catch(Exception e){
         e.printStackTrace();
       }
    }

    /**
     * Generate and load research staff
     */
    public void testLoadResearchStaffForMultipleSites(){
     try{
           TestDataFileUtils.deleteDirectory(TestDataFileUtils.getResearchStaffTestDataFolder());
           new GeneratorExecuter(new ResearchStaffForMultipleSitesXMLGenerator("researchstaff_template_for_multipleSites.xml"), "Research staff").execute();
           new DataLoadExecuter(new ResearchStaffLoader(getDeployedApplicationContext()), "Research staff").execute();
       }catch(Exception e){
         e.printStackTrace();
       }
    }

    /**
     * Generate and load subject
     */
    public void testLoadParticipantForFirstStudy(){
     try{
           TestDataFileUtils.deleteDirectory(TestDataFileUtils.getSubjectTestDataFolder());
           new GeneratorExecuter(new ParticipantXMLGenerator(), "Participant").execute();
           new DataLoadExecuter(new ParticipantLoader(getDeployedApplicationContext(), false), "Participant").execute();
       }catch(Exception e){
         e.printStackTrace();
       }
    }

    /**
     * Generate and load subject
     */
   public void testLoadParticipantForSubsequentStudy(){
    try{
          TestDataFileUtils.deleteDirectory(TestDataFileUtils.getSubjectTestDataFolder());
          new GeneratorExecuter(new ParticipantXMLGenerator(2,10), "Participant").execute();
          new DataLoadExecuter(new ParticipantLoader(getDeployedApplicationContext(), true), "Participant").execute();
      }catch(Exception e){
        e.printStackTrace();
      }
   }


    /**
     * Generate and load adverse events
     */
    public void testLoadAdverseEvents(){
     try{
           TestDataFileUtils.deleteDirectory(TestDataFileUtils.getAdverseEventTestDataFolder());
           new GeneratorExecuter(new AdverseEventXMLGenerator(), "Reporting Period").execute();
           new DataLoadExecuter(new AdverseEventLoader(getDeployedApplicationContext()), "Reporting Period").execute();
       }catch(Exception e){
         e.printStackTrace();
       }
    }

    public class GeneratorExecuter {
        XMLGenerator generator;
        String name;
        public GeneratorExecuter(XMLGenerator generator, String name){
            this.generator = generator;
            this.name = name;
        }
        public void execute() throws Exception{
            System.out.println("Data generation for " + name + " Started....");
            generator.generate();
            System.out.println("Data generation for " + name + " Finished....");
        }
    }

    public class DataLoadExecuter {
        DataLoader loader;
        String name;

        public DataLoadExecuter(DataLoader loader, String name){
            this.loader = loader;
            this.name = name;
        }
        public void execute() throws Exception{
            System.out.println("Data loading for " + name + " Started....");
            loader.load();
            System.out.println("Data loading for " + name + " Finished....");
        }
    }
}
