package ai.agent.vo;

/**
 * 对话交互模块
 * 聊天请求类
 */
public class ChatRequest {
    private String chatId;
    private String userMessage;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
}
