package ai.agent.record;

/**
 * 编程题纪录类
 */
public record ProgramRecord(String question, String input, String output, String code) {
    // 使用现代Java Record 语法定义的不可变数数据结构
    // 记录编程题的完整信息：题目、输入、期望输出、代码解答
}

record exampleRecord(String input, String output) {

}
