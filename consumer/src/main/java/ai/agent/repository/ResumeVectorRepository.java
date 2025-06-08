package ai.agent.repository;

import ai.agent.data.ResumeVector;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 简历向量化存储与相似度搜索
 * 管理 ResumeVector 表，存储简历的向量化表示
 */
public interface ResumeVectorRepository  extends JpaRepository<ResumeVector, Long> {
    /**
     * 基于向量相似度查找最近的简历
     */
    @Query(value = "SELECT * FROM resume_vectors where embedding <=> CAST(:embedding AS vector) < 1 ORDER BY embedding <=> CAST(:embedding AS vector) LIMIT 5",
            nativeQuery = true)
    List<ResumeVector> findNearest(@Param("embedding") float[] embedding);

    /**
     * 根据简历ID查找对象的向量
     */
    @Query(value = "SELECT * FROM resume_vectors where resume_id = :resumeId",nativeQuery = true)
    ResumeVector findByResumeId(@Param("resumeId") Long resumeId);
}
