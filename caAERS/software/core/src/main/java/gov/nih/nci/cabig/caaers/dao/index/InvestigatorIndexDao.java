package gov.nih.nci.cabig.caaers.dao.index;

import gov.nih.nci.cabig.caaers.domain.index.InvestigatorIndex;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.transaction.annotation.Transactional;

public class InvestigatorIndexDao extends AbstractIndexDao {
    
    
    @Override
    @Transactional(readOnly = false)
    public int[] updateIndex(final List pIds , final String userName, final Integer roleCode){
    	String sql = "insert into investigator_index (login_id,investigator_id,role_code) "
            + "values (?,?,?)";
    	
        String dataBase = "";
    	if(this.getProperties().getProperty(DB_NAME) != null){
    		dataBase = getProperties().getProperty(DB_NAME);
    	}
    	if(dataBase.equals(ORACLE_DB))
    		sql = "insert into investigator_index (id,login_id,investigator_id,role_code) "
                + "values (seq_investigator_index_id.NEXTVAL,?,?,?)";
    	
    	
		BatchPreparedStatementSetter setter = null;
        setter = new BatchPreparedStatementSetter() {

            public int getBatchSize() {
                return pIds.size();
            }

            public void setValues(PreparedStatement ps, int index) throws SQLException {
            	Integer pId = (Integer) pIds.get(index);
            	ps.setString(1, userName);
                ps.setInt(2, pId);
                ps.setInt(3, roleCode);
            }


        };
        return this.getJdbcTemplate().batchUpdate(sql, setter);
    	
    }
	
    @Override
    @Transactional(readOnly = false)
    public void clearIndex(String userName) {
    	String sql = "delete from investigator_index where login_id = '"+userName+"'";
    	getJdbcTemplate().update(sql);

    }

}
