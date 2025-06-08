package ai.agent.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 向量嵌入服务类，负责文本的向量化处理和向量存储。
 * 基于 Spring AI框架，提供文本到向量的转换和向量数据库操作功能。
 */
@Service
public class EmbeddingService {
    /** Spring AI向量存储，用于保存和检索向量数据 */
    private final VectorStore vectorStore;
    /** Spring AI嵌入模型，用于生成文本向量 */
    private final EmbeddingModel embeddingModel;

    /**
     * 构造函数，注入向量存储和嵌入模型。
     * @param vectorStore 向量存储服务
     * @param embeddingModel 嵌入模型服务
     */
    public EmbeddingService(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    /**
     * 将文本转换为向量表示。
     * @param text 输入文本
     * @return 文本的向量表示（浮点数组）
     */
    public float[] generateEmbedding(String text) {
        return embeddingModel.embed(text);
    }

    /**
     * 将文档转换为向量表示。
     * @param document 输入文档
     * @return 文档文本的向量表示（浮点数组）
     */
    public float[] generateEmbedding(Document document) {
        return embeddingModel.embed(document.getText());
    }

    /**
     * 将文本向量化并保存到向量数据库。
     * @param text 要保存的文本内容
     */
    public void saveEmbedding(String text) {
        // 将文本包装为文档对象
        List<Document> documents = List.of(new Document(text));
        // 保存到向量存储
        vectorStore.add(documents);
    }
}
