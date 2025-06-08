package ai.agent.services;

import ai.agent.data.Resume;
import ai.agent.data.ResumeVector;
import ai.agent.record.InterViewRecord;
import ai.agent.repository.ResumeVectorRepository;
import ai.agent.util.Dto2Record;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简历向量服务类，负责简历的向量化处理和相似度搜索。
 * 通过将简历文本转换为向量，实现基于语义相似度的智能简历匹配功能。
 */
@Service
public class ResumeVectorService {
    /** 向量嵌入服务，用于生成文本向量 */
    private final EmbeddingService embeddingService;
    /** 简历向量数据访问对象 */
    private final ResumeVectorRepository resumeVectorRepository;

    /**
     * 构造函数，注入依赖服务。
     * @param embeddingService 向量嵌入服务
     * @param resumeVectorRepository 简历向量数据访问对象
     */
    public ResumeVectorService(EmbeddingService embeddingService, ResumeVectorRepository resumeVectorRepository) {
        this.embeddingService = embeddingService;
        this.resumeVectorRepository = resumeVectorRepository;
    }

    /**
     * 根据问题查找相似的面试者简历。
     * @param question 查询问题
     * @return 相似度最高的面试者信息列表
     */
    public List<InterViewRecord> findInterViewsByQuestion(String question) {
        // 将问题转换为向量
        float[] embedding = this.embeddingService.generateEmbedding(question);
        System.out.println("Embedding length: "+ embedding.length);
        System.out.println("Embedding values: "+ Arrays.toString(embedding));

        // 查找最相似的简历向量
        List<ResumeVector> nearest = this.resumeVectorRepository.findNearest(embedding);

        // 提取简历实体
        List<Resume> resume = nearest.stream().map(resumeVector -> resumeVector.getResume()).collect(Collectors.toList());

        // 转换为面试记录格式并返回
        return resume.stream().map(Dto2Record::toInterViewDetails).toList();
    }

    /**
     * 根据简历ID查找对应的简历向量。
     * @param resumeId 简历ID
     * @return 简历向量实体
     */
    public ResumeVector findResumeVectorByResumeId(Long resumeId) {
        return this.resumeVectorRepository.findByResumeId(resumeId);
    }

    /**
     * 保存或更新简历的向量表示。
     * @param resume 简历实体
     */
    public void saveResumeVector(Resume resume) {
        // 查找是否已存在该简历的向量
        ResumeVector resumeVector = this.findResumeVectorByResumeId(resume.getId());

        // 生成简历文本的向量表示
        float[] embedding = embeddingService.generateEmbedding(resume.getRawText());

        if (resumeVector != null) {
            // 更新已存在的向量
            resumeVector.setEmbedding(embedding);
        } else {
            // 创建新的简历向量
            resumeVector = new ResumeVector(embedding, resume);
        }

        // 保存向量到数据库
        this.resumeVectorRepository.save(resumeVector);
    }
}
