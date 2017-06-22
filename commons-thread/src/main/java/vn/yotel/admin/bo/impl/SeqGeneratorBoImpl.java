package vn.yotel.admin.bo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import vn.yotel.admin.bo.SeqGeneratorBo;
import vn.yotel.admin.dao.SeqGeneratorDao;

@Service("SeqGeneratorBoImpl")
public class SeqGeneratorBoImpl  implements SeqGeneratorBo{

	@Autowired
	@Qualifier("SeqGeneratorDaoImpl")
	private SeqGeneratorDao seqDao;
	@Override
	public String nextVal(String seqName) {
		return seqDao.nextVal(seqName);
	}

}
