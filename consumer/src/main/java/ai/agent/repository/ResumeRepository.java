package ai.agent.repository;

import ai.agent.data.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 简历信息管理
 * 管理 Resume 表，存储候选人详细简历信息
 * 作为面试过程的核心数据载体，连接候选人信息与面试结果
 */
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    // 根据姓名查找简历，存储笔试成绩、面试评价结果等数据
    Resume findByName(String name);
}
