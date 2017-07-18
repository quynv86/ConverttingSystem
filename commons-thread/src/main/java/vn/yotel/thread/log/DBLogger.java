package vn.yotel.thread.log;

import java.sql.Connection;




import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vn.yotel.thread.ManageableThread;


@Component("DBLogger")
public class DBLogger implements ThreadLogger {

	org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(getClass());
	@Autowired
	DataSource dataSource;
	@Override
	public void log(ManageableThread thread, int level, String message) {
		String SQL = "insert into threads_log(thread_id,log_level,log_date,log_content) "
				+" values(?, ?, current_timestamp,?)";
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(SQL);
			int idx =1;
			stmt.setString(idx++, thread.getId());
			stmt.setString(idx++, String.valueOf(level));
			stmt.setString(idx++, message);
			stmt.execute();
			conn.commit();
		}catch(Exception ex){
			try {
				conn.rollback();
			} catch (SQLException e) {
			}
			LOG.error("Error when write thread log, detail: {}",ex.getMessage());
		}
		finally{
			try{
				if(stmt!=null){
					stmt.close();
				}
				if(conn!=null){
					conn.close();
				}
			}catch(SQLException exFiException){
				
			}
		}
	}

}
