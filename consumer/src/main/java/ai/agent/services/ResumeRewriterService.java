package ai.agent.services;

import ai.agent.assistant.ResumeRewriterAssistant;
import ai.agent.record.EducationRecord;
import ai.agent.record.ProjectExperienceRecord;
import ai.agent.record.WorkExperienceRecord;
import ai.agent.util.MarkdownToPdfConverter;
import ai.agent.vo.CvRequest;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简历重写服务类
 *
 * 实现原理：
 * 1. 智能简历优化：基于岗位描述(JD)自动优化简历内容，提高求职匹配度
 * 2. 多模态处理：支持文件上传解析和表单数据创建两种简历输入方式
 * 3. AI驱动重写：利用自然语言处理技术，结合JD关键词进行简历内容重写
 * 4. 自动化流程：从文档解析、关键词提取、内容重写到PDF生成的完整自动化流程
 *
 * 核心技术栈：
 * - Apache Tika：用于多格式文档解析（PDF、Word等）
 * - Python集成：调用Python脚本进行NLP关键词提取
 * - Spring AI：集成AI模型进行简历内容重写
 * - Markdown转PDF：支持样式丰富的简历输出
 *
 * 业务价值：
 * - 提高简历与职位的匹配度
 * - 自动化简历优化流程
 * - 支持批量简历处理
 * - 提供专业的简历格式输出
 */
@Service
public class ResumeRewriterService {
    private static final Logger logger = LoggerFactory.getLogger(ResumeRewriterService.class);

    // Python脚本路径配置：用于调用外部Python程序进行文本处理
    @Value("${python.script.extract_text.path}")
    private String pythonScriptPath;
    @Value("${python.script.html_to_pdf.path}")
    private String html2PdfPath;

    // 简历模板资源：预定义的简历格式模板，支持动态内容填充
    @Value("classpath:templates/resume.txt")
    private Resource resume;
    @Value("classpath:templates/education.txt")
    private Resource education;
    @Value("classpath:templates/work.txt")
    private Resource work;
    @Value("classpath:templates/project.txt")
    private Resource project;

    // 依赖注入的核心服务
    private final JDKeywordExtractor jdKeywordExtractor;      // JD关键词提取服务
    private final ResumeRewriterAssistant resumeRewriterAssistant;  // AI简历重写助手

    public ResumeRewriterService(JDKeywordExtractor jdKeywordExtractor, ResumeRewriterAssistant resumeRewriterAssistant) {
        this.jdKeywordExtractor = jdKeywordExtractor;
        this.resumeRewriterAssistant = resumeRewriterAssistant;
    }

    // PDF文件存储路径常量
    private static final String PDF_STORAGE_PATH = "external/static/pdf/";

    /**
     * 主要业务方法：处理上传的简历文件并进行AI重写
     *
     * 业务流程：
     * 1. 文档解析阶段：使用Apache Tika解析多格式简历文件（PDF、Word、TXT等）
     * 2. 关键词提取阶段：调用Python NLP脚本从JD中提取核心技能关键词
     * 3. AI重写阶段：结合原简历内容、JD要求和关键词，使用AI模型重写简历
     * 4. PDF生成阶段：将重写后的Markdown格式简历转换为专业PDF格式
     *
     * 技术实现：
     * - TikaDocumentReader：跨平台文档解析，支持50+种文件格式
     * - ProcessBuilder：安全的外部程序调用，支持Python脚本集成
     * - Spring AI：集成OpenAI/Azure OpenAI等大语言模型
     * - Markdown渲染：支持富文本格式和自定义CSS样式
     *
     * @param resumeFile 简历文件
     * @param jdText 招聘要求
     * @return 简历文件名
     * @throws IOException
     * @throws InterruptedException
     */
    public String processResume(MultipartFile resumeFile, String jdText) throws IOException, InterruptedException {
        // 阶段1：文档解析 - 使用Apache Tika进行智能文档解析
        // 优势：支持多种格式、自动识别编码、保留文档结构
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new InputStreamResource(resumeFile.getInputStream()));

        // 将文本内容划分成更小的块
        List<Document> documents = tikaDocumentReader.get();
        String resumeText = documents.get(0).getText();

        // 阶段2：关键词提取 - 调用Python NLP脚本进行语义分析
        // 使用spaCy中文模型进行词性标注、命名实体识别、关键词提取
        String jdKeywords = jdKeywordExtractor.extractKeywords(jdText);
        logger.info("提取的JD关键词: " + jdKeywords);

        // 阶段3：AI智能重写 - 基于大语言模型的简历内容优化
        // 结合原简历、JD要求、关键词三重信息进行上下文感知的内容重写
        String rewrittenResume = this.resumeRewriterAssistant.rewirter(jdText, resumeText, jdKeywords);

        String originalFilename = resumeFile.getOriginalFilename();
        // 阶段4：PDF生成 - 生成专业格式的简历文档
        String output = this.makePdf(originalFilename,rewrittenResume);

        return output;
    }

    /**
     * 替代业务方法：基于表单数据创建并重写简历
     *
     * 适用场景：
     * - 用户手动输入简历信息
     * - 批量简历生成
     * - 简历模板化创建
     *
     * 与processResume的区别：
     * - 输入源：表单数据 vs 文件上传
     * - 解析方式：模板填充 vs 文档解析
     * - 适用性：结构化数据 vs 非结构化文档
     *
     * @param cvRequest 简历信息
     * @return 简历文件名
     */
    public String createResume(CvRequest cvRequest) throws IOException, InterruptedException {
        // 使用模板系统生成结构化简历文本
        String resumeText = this.makeCvTxt(cvRequest);

        // 后续流程与processResume相同：关键词提取 -> AI重写 -> PDF生成
        String jdKeywords = jdKeywordExtractor.extractKeywords(cvRequest.getJd());
        logger.info("提取的JD关键词: " + jdKeywords);

        String rewrittenResume = this.resumeRewriterAssistant.rewirter(cvRequest.getJd(), resumeText, jdKeywords);

        String originalFilename = cvRequest.getName();
        String output = this.makePdf(originalFilename,rewrittenResume);

        return output;
    }

    /**
     * 备用文本提取方法：直接调用Python脚本处理PDF
     *
     * 使用场景：
     * - 当Tika解析失败时的降级方案
     * - 需要更精确的PDF文本提取时
     * - 处理特殊格式或加密PDF时
     *
     * 技术原理：
     * - 使用PyMuPDF(fitz)库进行PDF文本提取
     * - ProcessBuilder确保进程安全和资源管理
     * - 临时文件机制避免内存溢出
     *
     * @param resumeFile
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private String extractTextFromPDF(MultipartFile resumeFile) throws IOException, InterruptedException {
        // 创建临时文件：避免直接操作上传流，确保文件完整性
        File tempFile = File.createTempFile("resume", ".pdf");
        resumeFile.transferTo(tempFile);

        // 调用Python脚本：使用ProcessBuilder确保进程隔离和安全性
        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, tempFile.getAbsolutePath());
        processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出
        Process process = processBuilder.start();

        // 读取Python脚本的输出：使用流式处理避免大文件内存问题
        // process.getInputStream() 获取 Python脚本进程的标准输出流
        // new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8) 将字节流转换成字符流，指定 UTF-8 编码确保中文字符正确处理
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        // 将所有行 用换行符连接成一个完整的字符串
        String resumeText = reader.lines().collect(Collectors.joining("\n"));

        // 清理临时文件：防止磁盘空间泄露
        tempFile.delete();

        return resumeText;
    }

    /**
     * PDF生成核心方法：将Markdown格式简历转换为专业PDF文档
     *
     * 实现流程：
     * 1. 时间戳生成：确保文件名唯一性，避免覆盖
     * 2. Markdown转HTML：保留格式和结构信息
     * 3. CSS样式注入：添加专业的排版样式
     * 4. HTML转PDF：调用Python脚本进行最终转换
     *
     * 样式特性：
     * - 中文字体支持：宋体SimSun确保中文显示效果
     * - 专业配色：标题使用深蓝色系，体现专业性
     * - 响应式布局：适配不同PDF阅读器和打印需求
     *
     * @param originalFilename
     * @param rewrittenResume
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private String makePdf(String originalFilename,String rewrittenResume) throws IOException, InterruptedException {
        // 生成唯一时间戳：确保并发安全和文件唯一性
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(dtf);

        String resumePdfName = FilenameUtils.removeExtension(originalFilename) + "_" + timestamp + ".pdf";
        String outputPath = PDF_STORAGE_PATH + resumePdfName; // 生成的PDF文件路径

        // Markdown到HTML的转换：保留文档结构和格式
        String html = MarkdownToPdfConverter.markdownToHtml(rewrittenResume);

        // CSS样式注入：创建专业的简历视觉效果
        // 包含字体设置、颜色主题、布局样式等
        html = "<html><head><meta charset=\\\"UTF-8\\\"/><style>" +
                "body { font-family: 宋体, SimSun;} " +
                "h1 { color: #333366;} " +
                "h2 { color: #666699;border-bottom: 1px solid #ddd;padding-bottom: 5px; margin-bottom: 20px;  } " +
                "ul { list-style-type: square; } " +
                "li { font-size: 18px;}" +
                "</style></head><body>" +
                html +
                "</body></html>";

        // 调用Python脚本进行HTML到PDF的转换
        MarkdownToPdfConverter.html2Pdf(html, outputPath, html2PdfPath);

        return "pdf/" + resumePdfName;
    }

    /**
     * 模板化简历生成方法：基于预定义模板和用户数据生成简历文本
     *
     * 模板系统设计：
     * 1. 主模板(resume.txt)：定义简历整体结构和基本信息占位符
     * 2. 子模板(education.txt, work.txt, project.txt)：定义各部分的详细格式
     * 3. 占位符替换：使用{key}格式进行动态内容填充
     * 4. 循环处理：支持多个教育经历、工作经历、项目经历的批量生成
     *
     * 技术优势：
     * - 模板与逻辑分离：便于维护和定制
     * - 动态内容填充：支持个性化简历生成
     * - 结构化处理：确保简历格式的一致性
     * - 可扩展设计：易于添加新的简历模块
     *
     * @param cvReqeust 简历信息
     * @return 简历文本
     */
    private String makeCvTxt(CvRequest cvReqeust) throws IOException {
        // 加载主简历模板并进行基本信息填充
        String cv = resume.getContentAsString(Charset.defaultCharset());
        cv = cv
                .replace("{name}", cvReqeust.getName())
                .replace("{sex}", cvReqeust.getSex())
                .replace("{birthDate}", cvReqeust.getBirthDate())
                .replace("{email}", cvReqeust.getEmail())
                .replace("{phone}", cvReqeust.getPhone());

        // 使用StringBuilder进行高效的字符串拼接
        StringBuilder sb = new StringBuilder();

        // 教育经历模块：支持多个教育背景的批量处理
        List<EducationRecord> educationRecords = cvReqeust.getEducationRecords();
        if (educationRecords != null) {
            educationRecords.forEach(educationRecord -> {
                try {
                    // 为每个教育经历应用模板并填充数据
                    String txt = education.getContentAsString(Charset.defaultCharset());
                    txt = txt
                            .replace("{begin}", educationRecord.getBegin())
                            .replace("{end}", educationRecord.getEnd())
                            .replace("{university}", educationRecord.getUniversity())
                            .replace("{major}", educationRecord.getMajor())
                            .replace("{degree}", educationRecord.getDegree());
                    sb.append(txt);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
        cv = cv.replace("{education}",sb.toString());
        sb.setLength(0); // 清空StringBuilder以重用

        // 工作经历模块：支持多段工作经验的结构化处理
        List<WorkExperienceRecord> workExperienceRecords = cvReqeust.getWorkExperienceRecords();
        if (workExperienceRecords != null) {
            workExperienceRecords.forEach(workExperienceRecord -> {
                try {
                    String contentAsString = work.getContentAsString(Charset.defaultCharset());
                    contentAsString = contentAsString
                            .replace("{begin}", workExperienceRecord.getBegin())
                            .replace("{end}", workExperienceRecord.getEnd())
                            .replace("{company}", workExperienceRecord.getCompany())
                            .replace("{workContent}", workExperienceRecord.getWorkContent());
                    sb.append(contentAsString);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
        cv = cv.replace("{work}",sb.toString());
        sb.setLength(0);

        // 项目经历模块：展示技术能力和项目成果
        List<ProjectExperienceRecord> projectExperienceRecords = cvReqeust.getProjectExperienceRecords();
        if (projectExperienceRecords != null) {
            projectExperienceRecords.forEach(projectExperienceRecord -> {
                try {
                    String contentAsString = project.getContentAsString(Charset.defaultCharset());
                    contentAsString = contentAsString
                            .replace("{begin}", projectExperienceRecord.getBegin())
                            .replace("{end}", projectExperienceRecord.getEnd())
                            .replace("{name}", projectExperienceRecord.getName())
                            .replace("{content}", projectExperienceRecord.getContent())
                            .replace("{skills}", projectExperienceRecord.getSkills());
                    sb.append(contentAsString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        cv = cv.replace("{project}",sb.toString());
        return cv;
    }
}
