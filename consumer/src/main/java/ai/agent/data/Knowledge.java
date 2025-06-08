package ai.agent.data;

import jakarta.persistence.*;

/**
 * 知识库实体
 * 面试题库管理
 * 支持多语言和分类
 * 为AI面试官提供题目来源
 */
@Entity
@Table(name = "knowledge")
public class Knowledge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 语言
    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "question", nullable = false)
    private String question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
