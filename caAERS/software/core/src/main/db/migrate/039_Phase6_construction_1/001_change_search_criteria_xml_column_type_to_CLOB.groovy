/**
 * User: Janakiram_G
 * Date: 08/05/15
 */

class ChangeSearchCriteriaXmlColumnTypeToCLOB extends edu.northwestern.bioinformatics.bering.Migration {

    void up() {

        if (databaseMatches('oracle')){

            execute("ALTER TABLE searches ADD TEMP VARCHAR2(4000);");
            execute("UPDATE searches SET TEMP = CRITERIA_XML;");
            execute("COMMIT;");
            execute("ALTER TABLE searches MODIFY CRITERIA_XML NULL;");
            execute("UPDATE searches SET CRITERIA_XML = null;");
            execute("COMMIT;");
            execute("ALTER TABLE searches MODIFY CRITERIA_XML LONG;");
            execute("ALTER TABLE searches MODIFY CRITERIA_XML CLOB;");
            execute("UPDATE searches SET CRITERIA_XML = TEMP;");
            execute("ALTER TABLE searches MODIFY CRITERIA_XML NOT NULL;");
            execute("COMMIT;");
            execute("ALTER TABLE searches DROP column TEMP;");

        }

    }

    void down() {

    }
}
