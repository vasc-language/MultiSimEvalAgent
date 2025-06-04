package ai.agent.exception;

import ai.agent.util.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice
public class RestExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionAdvice.class);

    @ResponseBody
    //@ExceptionHandler(value= Exception.class)
    public CommonResult<Map<String, Object>> restException(Exception e, HttpServletRequest request) {
        logger.error(e.getMessage());
        return CommonResult.failed(e.getMessage());
    }
}
