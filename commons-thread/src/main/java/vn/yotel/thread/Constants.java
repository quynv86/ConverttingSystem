package vn.yotel.thread;

public interface Constants {
	public interface ManageableThreadState {
		public byte NORMAL = 0;
		public byte INFO = 1;
		public byte WARN = 2;
		public byte ERROR = 3;
		public byte IDLE = 4;
	}
	public interface LOGLEVEL{
		public int DEFAUL =2;
		public int DEBUG = 1;
		public int INFOR =2;
		public int WARNING =3;
		public int ERROR = 4;
		public int FATAL = 5;
		
		public String DEBUG_ST = "DEBUG";
		public String INFOR_ST = "INFOR";
		public String WARNING_ST ="WARNING";
		public String ERROR_ST = "ERROR";
		public String FATAL_ST = "FATAL";
	}

}
