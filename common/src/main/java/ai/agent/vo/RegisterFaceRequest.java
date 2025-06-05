package ai.agent.vo;

/**
 * 身份认证模块
 * 人脸注册请求类
 */
public class RegisterFaceRequest {
    private String image; // Base64 编码的图片数据
    private String userId;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
