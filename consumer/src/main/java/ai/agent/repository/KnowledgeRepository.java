package ai.agent.repository;

import ai.agent.data.Knowledge;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 面试题库管理
 * 管理 Knowledge 表，存储面试题目
 * 为笔试环节提供题目来源，支持按语言和类型分类的随机出题
 */
public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {
    // 根据变成语言和题目类型随机获取指定数量的面试题
    @Query(value = "SELECT * FROM Knowledge WHERE language = :language AND type = :type ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Knowledge> findRandomRecords(@Param("language") String language,
                                      @Param("type") String type,
                                      @Param("limit") int limit);
}
