package ai.agent.data;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

/**
 * 简历向量实体（依赖 Resume 和 VectorType）
 * 简历文本 -> AI模型处理 -> 向量表示 -> ResumeVector存储 -> 相似度计算
 */
@Entity
@Table(name = "resume_vectors")
public class ResumeVector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(value = VectorType.class)
    @Column(name = "embedding", columnDefinition = "vector(50)")
    private float[] embedding;

    /**
     * 与 Resume 实体建立一对一关联
     * 使用 CascadeType.ALL 确保数据一致性
     * 通过 resume_id 建立数据库层面的引用完整性
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "resume_id", referencedColumnName = "id")
    private Resume resume;


    // 必须有无参构造函数
    public ResumeVector() {}

    public ResumeVector(float[] embedding, Resume resume) {
        this.embedding = embedding;
        this.resume = resume;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }
}
