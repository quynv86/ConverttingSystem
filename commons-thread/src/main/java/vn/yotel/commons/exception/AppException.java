package vn.yotel.commons.exception;

public class AppException extends Exception {

    private static final long serialVersionUID = 1L;

    private String code;
    private String message;

    public AppException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIntCode() {
        try {
            return Integer.parseInt(this.code);
        } catch (Exception e) {
            return -1;
        }
    }
}
