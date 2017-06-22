package vn.yotel.admin.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;




import vn.yotel.admin.dao.SeqGeneratorDao;

@Repository("SeqGeneratorDaoImpl")
public class SeqGeneratorDaoImpl implements SeqGeneratorDao {

	@Autowired
	DataSource dataSource;
	
	@Override
	public String nextVal(String seqName) {
		String SQL = String.format("select %s.nextval from dual", seqName);
		Connection conn =null;
		PreparedStatement stmt =null;
		ResultSet res = null;
		String retVal = "";
		try{
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(SQL);
			res = stmt.executeQuery();
			if(res.next()){
				retVal = res.getString(1);
			}
			
		}catch(Exception ex){
			
		}finally{
			try{
				if(res!=null){
					res.close();
				}
				if(stmt !=null ){stmt.close();}
				if(conn !=null) {conn.close();}
			}catch(Exception finalE){
				
			}
		}
		return retVal;
	}

}
