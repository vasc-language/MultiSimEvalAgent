package ai.agent.controller;

import ai.agent.auth.Sessions;
import ai.agent.auth.Sign;
import ai.agent.data.Candidates;
import ai.agent.services.CandidatesService;
import ai.agent.util.CommonResult;
import ai.agent.vo.UserResponse;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final CandidatesService candidatesService;

    @Value("${auth.token.secret}")
    private String secret;

    public LoginController(CandidatesService interviewerService) {
        this.candidatesService = interviewerService;
    }

    /**
     * 登录
     * @param name 登录名
     * @param code 邀请code
     * @return 登录信息
     * @throws Exception
     */
    @PostMapping(value = "/login")
    public CommonResult<UserResponse> login(@RequestParam("name") String name,
                                            @RequestParam("code") String code) throws Exception {
        Candidates candidates = this.candidatesService.login(name,code);
        String token = Sessions.loginUser(candidates.getName(),
                true,
                secret);
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(candidates.getId());
        userResponse.setUserName(candidates.getName());
        userResponse.setToken(token);
        return CommonResult.success(userResponse);
    }

    /**
     * JWT认证
     * @param token token
     * @return 认证信息
     */
    @PostMapping(value = "/auth/verify-token")
    public CommonResult<String> verifyToken(@RequestHeader("token") String token) {
        DecodedJWT decodedJWT = null;
        try {
            decodedJWT = Sign.verifyToken(token, secret);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return CommonResult.unauthorized("Token验证失败");
        }
        String username = decodedJWT.getClaim("userName").asString();
        return CommonResult.success(username);
    }
}
