package ai.agent.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 职位描述关键词提取服务类，负责从职位描述文本中提取关键词。
 * 通过调用Python脚本实现自然语言处理，提取职位相关的技能和要求关键词。
 */
@Service
public class JDKeywordExtractor {

    /** Python关键词提取脚本的路径 */
    @Value("${python.script.extract_keywords.path}")
    private String pythonScriptPath;

    /**
     * 调用Python脚本提取职位描述中的关键词。
     * @param jdText 职位描述文本
     * @return 提取的关键词字符串
     * @throws RuntimeException 当脚本执行失败时抛出异常
     */
    public String extractKeywords(String jdText) {
        try {
            // 将文本编码为UTF-8格式，确保中文字符正确处理
            byte[] jdTextBytes = jdText.getBytes(StandardCharsets.UTF_8);
            String jdTextEncoded = new String(jdTextBytes, StandardCharsets.UTF_8);

            // 创建进程构建器，配置Python脚本调用命令
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, jdText, jdTextEncoded);
            processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出

            // 启动Python脚本进程
            Process process = processBuilder.start();

            // 读取Python脚本的输出结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // 等待进程结束并检查执行结果
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // 脚本执行成功，返回提取的关键词
                return output.toString();
            } else {
                throw new RuntimeException("Python脚本执行失败，退出码: " + exitCode);
            }

        } catch (Exception e) {
            throw new RuntimeException("提取关键词时发生错误: " + e.getMessage(), e);
        }
    }
}












