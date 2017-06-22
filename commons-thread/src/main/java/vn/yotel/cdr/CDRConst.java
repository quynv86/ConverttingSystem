package vn.yotel.cdr;

public interface CDRConst {
	public interface Actions{
		public String CONVERT = "CONVERT";
		public String GET = "GET";
	}
	public interface DataSources{
		public String HUAWEI_MSC = "1";
	}
	public interface Status{
		public int BEGIN = 2;
		public int SUCC = 1;
		public int ERR = 0;
		public String CV_SUCSS_KEY = "CONVERT_SUCCESS";
		public String CV_ERROR_KEY = "CONVERT_ERROR";
	}
	public interface SEQS{
		public String CDR_FILE_SEQ = "CDR_FILE_SEQ";
	}
}
