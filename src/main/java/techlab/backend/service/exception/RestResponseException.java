package techlab.backend.service.exception;

public class RestResponseException extends RuntimeException {

    private final int code;

    public RestResponseException(String message, int code) {
        super(message);
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
