package vn.yotel.commons.util;

import com.google.gson.annotations.Expose;

public class RestMessage {
	
	@Expose private String status;
	@Expose private String message;
	@Expose private Object data;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public static class RestMessageBuilder {
		public static RestMessage SUCCESS() {
			return SUCCESS("Success");
		}
		public static RestMessage SUCCESS(String message) {
			RestMessage msg = new RestMessage();
			msg.setStatus("0");
			msg.setMessage(message);
			msg.setData(null);
			return msg;
		}
		
		public static RestMessage FAIL(String string, String message) {
			RestMessage msg = new RestMessage();
			msg.setStatus(string);
			msg.setMessage(message);
			msg.setData(null);
			return msg;
		}
	}
}
