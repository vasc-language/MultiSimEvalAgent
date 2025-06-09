package ai.agent.assistant;


import ai.agent.advisor.LoggingAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * 简历重写AI助手
 */
@Service
public class ResumeRewriterAssistant {
    private final ChatClient chatClient;

    // 用户提示词模板资源：定义具体的任务指令和输入格式
    private Resource userTextAdvisors;

     /*
     * @param modelBuilder Spring AI模型构建器，用于配置底层LLM连接
     * @param vectorStore 向量数据库，存储简历优化相关的知识库
     * @param systemText 系统提示词资源，定义AI助手的基本角色和能力
     * @param userTextAdvisors 用户提示词模板，定义具体的简历重写任务格式
     * @throws IOException 资源文件读取异常
     */
    public ResumeRewriterAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore,
                                   @Value("classpath:prompt/resume-optimizer-system-prompt.st") Resource systemText,
                                   @Value("classpath:prompt/resume-optimizer-user-prompt.st") Resource userTextAdvisors) throws IOException {
        this.userTextAdvisors = userTextAdvisors;

        // 构建AI客户端：采用链式调用模式配置多层AI能力
        this.chatClient = modelBuilder
                // 第一层：系统级配置 - 定义AI助手的基础人格和能力边界
                .defaultSystem(s -> s.text(systemText))

                // 第二层：增强组件配置 - 添加专业领域能力
                .defaultAdvisors(
                        // RAG增强：结合向量检索提供上下文相关的专业知识
                        // 相似度阈值0.5：确保检索到的内容与查询足够相关
                        new QuestionAnswerAdvisor(vectorStore,
                                SearchRequest.builder().similarityThreshold(0.5).build(),
                                userTextAdvisors.getContentAsString(Charset.defaultCharset())),

                        // 日志增强：记录AI交互过程，支持问题诊断和性能优化
                        new LoggingAdvisor()
                )
                // 第三层：功能函数配置（当前被注释）
                // 支持AI调用Java方法，实现更复杂的业务逻辑集成
                //.defaultFunctions("changeTestResult","getInterviewDetails")
                .build();
    }

    /*
     * @param jdText 职位描述文本 - 提供目标职位的具体要求和期望
     * @param resumeText 原始简历内容 - 需要优化的基础简历文本
     * @param keyword 关键词列表 - 通过NLP提取的行业和技能关键词
     * @return 优化后的简历内容 - AI重写后的专业简历文本
     * @throws IOException 模板文件读取异常
     */
    public String rewirter(String jdText, String resumeText,
                           String keyword) throws IOException {
        // 第一阶段：数据准备 - 构建AI理解的结构化输入
        // 使用HashMap确保参数传递的类型安全和扩展性
        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("jd_text",jdText);        // JD原文：为AI提供目标匹配标准
        questionMap.put("resume_text",resumeText); // 简历原文：为AI提供优化基础
        questionMap.put("keyword",keyword);        // 关键词：为AI提供优化方向指导

        // 第二阶段：模板加载 - 获取用户提示词模板内容
        // 支持外部化配置，便于提示词的版本管理和A/B测试
        String userContent = userTextAdvisors.getContentAsString(Charset.defaultCharset());

        // 第三阶段：AI交互 - 构建并执行AI请求
        // 采用流式API设计，支持链式调用和参数化配置
        return this.chatClient.prompt()
                // 用户消息构建：结合模板和参数生成最终提示词
                .user(u -> u.text(userTextAdvisors).params(questionMap))
                // 同步调用：发送请求并等待AI响应
                .call()
                // 内容提取：从AI响应中提取纯文本结果
                .content();
    }
}

