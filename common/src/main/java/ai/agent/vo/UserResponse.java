package ai.agent.vo;

/**
 * 身份认证模块
 * 用户响应类
 */
public class UserResponse {
    public Long userId;
    public String userName;
    public String token;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
