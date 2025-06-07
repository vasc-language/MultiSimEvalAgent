package ai.agent.util;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Markdown 转换器
 */
public class MarkdownToPdfConverter {
    /**
     * 将Markdown转换为HTML
     */
    public static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Document document = parser.parse(markdown);
        return renderer.render(document);
    }

    /**
     * iText 库方案 - 适用 Java 环境下高性能 PDF 生成
     * 将 HTML 转换为 PDF
     */
    public static void htmlToPdf(String html, String outputPdfPath) {
        try (FileOutputStream os = new FileOutputStream(outputPdfPath)) {
            ConverterProperties converterProperties = new ConverterProperties();
            FontProvider fontProvider = new FontProvider();

            InputStream fontStream = MarkdownToPdfConverter.class.getClassLoader().getResourceAsStream("fonts/NotoSerifSC-VariableFont_wght.ttf");
            if (fontStream == null) {
                throw new RuntimeException("字体文件未找到！");
            }

            // 将输入流转换为字节数组
            byte[] fontBytes = fontStream.readAllBytes();
            System.out.println("Font byte array length: " + fontBytes.length);
            FontProgram fontProgram = FontProgramFactory.createFont(fontBytes, true);

            fontProvider.addFont(fontProgram);
            converterProperties.setFontProvider(fontProvider);

            // 设置编码
            converterProperties.setCharset(StandardCharsets.UTF_8.name());

            HtmlConverter.convertToPdf(html, os, converterProperties);
            System.out.println("HTML 已成功转换为 PDF: " + outputPdfPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Python 脚本方案 - 适用于需要更丰富HTML/CSS支持的场景
     * @param html HTML 文件
     * @param outputPath 文件输出路径
     * @param script python 脚本的路径
     */
    public static void html2Pdf(String html, String outputPath, String script) throws IOException, InterruptedException {
        // 调用 extract_text.py 提取文本
        ProcessBuilder processBuilder = new ProcessBuilder("python", script, html, outputPath);
        processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出
        Process process = processBuilder.start();

        // 读取输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // 等待进程结束
        int exitCode = process.waitFor();
        System.out.println("Python script executed with exit code: " + exitCode);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== MarkdownToPdfConverter 测试程序 ===\n");

        // 测试用的Markdown内容
        String output = """
                ## 个人信息
                - **姓名:** 张三 \s
                - **教育背景:** 浙江大学，2004.9~2008.7，计算机科学学士 \s
                                
                ---
                                
                ## 工作经历
                                
                ### 阿里巴巴（2018.9~至今）
                #### 职位: 高级软件工程师
                                
                - **背景与挑战:** 在阿里巴巴期间，负责参与阿里千问大模型的性能调优工作，面临模型推理效率低、资源利用率不高的问题。
                - **任务:** 我的任务是通过优化算法和框架结构，提升模型推理速度，并降低计算资源消耗。
                - **行动:**\s
                  - 快速学习并掌握Langchain框架，结合Python实现模型调优。
                  - 引入分布式计算技术，优化模型训练和推理流程。
                  - 与团队成员密切协作，设计并实施多轮测试验证方案。
                - **成果:**\s
                  - 模型推理时间缩短了**30%**，资源使用率提升了**25%**。
                  - 成功支持了多个客户的定制化需求，客户满意度达到**95%**。
                                
                ---
                                
                ## 技能清单
                - **编程语言:** JAVA, Python \s
                - **框架与工具:** Spring Boot, Spring Cloud, RabbitMQ, Langchain \s
                - **数据库:** Oracle, MySQL \s
                - **大数据技术:** Hadoop生态圈相关技术（如HDFS、MapReduce等） \s
                """;

        // 将Markdown转换为HTML
        System.out.println("1. 正在将Markdown转换为HTML...");
        String markdownHtml = markdownToHtml(output);
        
        // 测试用的完整HTML内容（带样式）
        String styledHtml = "<html><head><meta charset=\"UTF-8\" /><style>" +
                "body { font-family: 'Microsoft YaHei', '微软雅黑', SimSun, '宋体', serif; " +
                "       background: #f8f9fa; margin: 20px; line-height: 1.6; color: #333; } " +
                "h1 { color: #2c3e50; text-align: center; border-bottom: 3px solid #3498db; padding-bottom: 10px; } " +
                "h2 { color: #34495e; border-bottom: 2px solid #ecf0f1; padding-bottom: 8px; margin-top: 25px; } " +
                "h3 { color: #7f8c8d; margin-bottom: 10px; } " +
                "h4 { color: #95a5a6; font-style: italic; } " +
                "li { font-size: 14px; margin-bottom: 5px; } " +
                "ul { padding-left: 20px; } " +
                "strong { color: #e74c3c; } " +
                "hr { border: none; height: 2px; background: linear-gradient(to right, #3498db, #2ecc71); margin: 20px 0; } " +
                ".container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 20px rgba(0,0,0,0.1); }" +
                "</style></head><body><div class='container'>" +
                "<h1>张三 - 个人简历</h1>" +
                markdownHtml +
                "</div></body></html>";

        // 简单HTML测试内容
        String simpleHtml = "<html><head><meta charset=\"UTF-8\" /><style>" +
                "body { font-family: '微软雅黑', SimSun; background: #f0f0f0; padding: 20px; } " +
                "h1 { color: #333366; text-align: center; } " +
                "h2 { color: #666699; border-bottom: 1px solid #ddd; padding-bottom: 5px; } " +
                "li { font-size: 16px; margin-bottom: 3px; }" +
                "</style></head><body>" +
                "<h1>简历测试文档</h1>" +
                "<h2>联系方式</h2>" +
                "<ul><li>电话: 123-456-7890</li><li>邮箱: zhangsan@example.com</li></ul>" +
                "<h2>工作经历</h2>" +
                "<h3>公司A - 软件工程师</h3>" +
                "<ul><li><strong>时间</strong>: 2020年1月 - 至今</li>" +
                "<li><strong>职责</strong>: 负责开发Java后端服务，优化系统性能。</li>" +
                "<li><strong>成就</strong>: 通过优化数据库查询，将系统响应时间减少了30%。</li></ul>" +
                "<h2>教育背景</h2>" +
                "<h3>某某大学 - 计算机科学与技术</h3>" +
                "<ul><li><strong>时间</strong>: 2016年9月 - 2020年6月</li>" +
                "<li><strong>学位</strong>: 本科</li></ul>" +
                "</body></html>";

        // 测试1: 使用iText库的htmlToPdf方法
        System.out.println("\n2. 测试htmlToPdf方法（使用iText库）...");
        try {
            String outputPath1 = "test_itext_resume.pdf";
            htmlToPdf(styledHtml, outputPath1);
            System.out.println("✓ 使用iText的PDF转换成功，输出文件: " + outputPath1);
        } catch (Exception e) {
            System.err.println("✗ iText PDF转换失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 测试2: 使用简单HTML内容测试iText
        System.out.println("\n3. 测试htmlToPdf方法（简单HTML内容）...");
        try {
            String outputPath2 = "test_itext_simple.pdf";
            htmlToPdf(simpleHtml, outputPath2);
            System.out.println("✓ 使用iText的简单HTML转换成功，输出文件: " + outputPath2);
        } catch (Exception e) {
            System.err.println("✗ iText简单HTML转换失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 测试3: 使用Python脚本的html2Pdf方法
        System.out.println("\n4. 测试html2Pdf方法（调用Python脚本）...");
        try {
            String pythonScript = "scripts/html2pdf.py";
            String outputPath3 = "test_python_resume.pdf";
            html2Pdf(styledHtml, outputPath3, pythonScript);
            System.out.println("✓ 使用Python脚本的PDF转换调用完成，输出文件: " + outputPath3);
        } catch (Exception e) {
            System.err.println("✗ Python脚本PDF转换失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 测试4: 使用Python脚本转换简单HTML
        System.out.println("\n5. 测试html2Pdf方法（Python + 简单HTML）...");
        try {
            String pythonScript = "scripts/html2pdf.py";
            String outputPath4 = "test_python_simple.pdf";
            html2Pdf(simpleHtml, outputPath4, pythonScript);
            System.out.println("✓ 使用Python脚本的简单HTML转换调用完成，输出文件: " + outputPath4);
        } catch (Exception e) {
            System.err.println("✗ Python脚本简单HTML转换失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== 测试完成 ===");
        System.out.println("请检查生成的PDF文件:");
        System.out.println("- test_itext_resume.pdf (iText + 完整简历)");
        System.out.println("- test_itext_simple.pdf (iText + 简单HTML)"); 
        System.out.println("- test_python_resume.pdf (Python + 完整简历)");
        System.out.println("- test_python_simple.pdf (Python + 简单HTML)");
    }
}
