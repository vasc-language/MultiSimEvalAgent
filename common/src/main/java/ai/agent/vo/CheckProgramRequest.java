package ai.agent.vo;

/**
 * 编程测试模块
 * 编程题检查请求类
 */
public class CheckProgramRequest {
    private String question; // 编程题目描述
    private String input; // 测试用例的输入数据
    private String output; // 期望的输出结果
    private String code; // 面试者提交的代码解答
}
