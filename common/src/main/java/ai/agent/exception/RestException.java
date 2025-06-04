package ai.agent.exception;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 姚东名
 * Date: 2025-06-04
 * Time: 22:20
 */
public class RestException extends RuntimeException {
    private static final long serialVersionUID = -8148263925714955642L; // 唯一标识符
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public RestException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
