package ai.agent.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 会话管理层
 */
public class Sessions {
    public static final long SHORT_SESSION = TimeUnit.HOURS.toMillis(12);
    public static final long LONG_SESSION = TimeUnit.HOURS.toMillis(30 * 24);

    /**
     * 用户登录
     * 调用 Sign.getToken() 生成 JWT 令牌
     * @param rememberMe 根据“记住我”选项选择会话时长
     */
    public static String loginUser(String name, boolean rememberMe, String secret) {
        long duration;
        int maxAge;

        if (rememberMe) {
            // "Remember me"
            duration = LONG_SESSION;
        } else {
            duration = SHORT_SESSION;
        }
        maxAge = (int) (duration / 1000);

        return Sign.getToken(name, secret);
    }

    /**
     * 从请求中获取令牌
     * @param request 从 Cookie 中提取名为 "ai-interview" 的令牌
     */
    public static String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return null;
        Cookie tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> AuthConstant.COOKIE_NAME.equals(cookie.getName()))
                .findAny().orElse(null);
        if (tokenCookie == null) return null;
        return tokenCookie.getValue();
    }

    /**
     * 用户登出
     * 通过设置空值和过期时间为 0 来清除 Cookie
     */
    public static void logout(String externalApex, HttpServletResponse response) {
        Cookie cookie = new Cookie(AuthConstant.COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setDomain(externalApex);
        response.addCookie(cookie);
    }
}
