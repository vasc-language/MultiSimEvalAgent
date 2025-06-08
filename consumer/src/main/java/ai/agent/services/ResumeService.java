package ai.agent.services;

import ai.agent.audio.services.AudioService;
import ai.agent.data.Resume;
import ai.agent.repository.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 简历服务类 - 处理简历相关的业务逻辑
 * 主要功能：建立上传解析、面试结果管理、面试状态更新
 */
@Service
public class ResumeService {
    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);

    private final AudioService audioService; // 音频服务，用于生成语音反馈
    private final ResumeRepository resumeRepository; // 建立数据访问层

    public ResumeService(AudioService audioService, ResumeRepository resumeRepository) {
        this.audioService = audioService;
        this.resumeRepository = resumeRepository;
    }

    /**
     * 保存简历文件 - 解析上传的简历文件并提取文本内容
     */
    public void saveResume(Long id, MultipartFile file) throws IOException {
        // 使用 Tika 解析文本内容
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
        List<Document> documents = tikaDocumentReader.get();
        if (!documents.isEmpty()) {
            Resume resume = resumeRepository.getReferenceById(id);
            resume.setRawText(documents.get(0).getText()); // 保存解析后的文本
            resumeRepository.save(resume);
        }
    }
}
