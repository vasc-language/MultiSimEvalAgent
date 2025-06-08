package ai.agent.repository;

import ai.agent.data.Candidates;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 候选人信息管理
 * 管理 Candidates 表，存储候选人基本信息
 */
public interface CandidatesRepository extends JpaRepository<Candidates, Long> {
    // 通过姓名和状态码验证候选人信息，用于登录阶段
    Candidates findByNameAndCode(String name, String code);
}

/**
 * 1. 面试流程管理：从候选人验证（CandidatesRepository）-> 题目获取（KnowledgeRepository）—> 简历管理（ResumeRepository）-> 智能匹配（ResumeVectorRepository）
 * 2. AI 能力支撑：ResumeVectorRepository 提供向量搜索能力，支持语义化的简历分析
 *               KnowledgeRepository 支持智能出题，根据不同技术栈随机生成题目
 * 3. 业务流程闭环：候选人登录 -> 笔试答题 -> 结果记录 -> 面试评论 -> 数据分析
 *                每个环节都有对应的 Repository 支持数据操作
 * 4. 服务层调用：这些 Repository 被 CandidatesService、ResumeService、ResumeVectorService 等服务类调用，为上层的 Controller 和 AI 助手数据支持
 */
