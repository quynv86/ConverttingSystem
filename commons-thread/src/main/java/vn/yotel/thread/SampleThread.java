package vn.yotel.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.yotel.commons.exception.AppException;

public class SampleThread extends ManageableThread {

	Logger LOG = LoggerFactory.getLogger(getClass());
	private String mstrSQL;

	@Override
	protected boolean processSession() throws AppException {
		// TODO Auto-generated method stub
		for (int i = 0; i < 100; i++) {
			DataSource ds = null;
			Connection conn = null;
			PreparedStatement stmt = null;

			try {
				ds = (DataSource) getBean("dataSource");
				conn = ds.getConnection();
				stmt = conn.prepareStatement("select 1 from dual");
				ResultSet res = stmt.executeQuery();
				//System.out.println("Dung lai 30 s");
				stmt.close();
				Thread.currentThread().sleep(10);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					stmt.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	protected void loadParameters() throws AppException {
		try {
			mstrSQL = this.getParamAsString("SQL");
		} catch (Exception exLoadParams) {
			throw new AppException("LOAD", exLoadParams.getMessage());
		} finally {
			super.loadParameters();
		}
	}

	@Override
	protected void validateParameters() throws AppException {
	}
}
