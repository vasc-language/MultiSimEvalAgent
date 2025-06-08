package ai.agent.util;

import ai.agent.data.Candidates;
import ai.agent.data.Resume;
import ai.agent.record.CandidateRecord;
import ai.agent.record.InterViewRecord;

/**
 * 数据传输对象（DTO）到记录（Record）的转换工具类
 */
public class Dto2Record {
    /**
     * 将 Resume 实体转换为 InterViewRecord 记录
     * Resume -> InterViewRecord 包含面试相关信息（姓名、分数、状态、评语、音频路径等）
     */
    public static InterViewRecord toInterViewDetails(Resume resume){
        return new InterViewRecord(
                resume.getId().toString(),
                resume.getName(),
                resume.getScore(),
                resume.getInterViewStatus().toString(),
                resume.getEvaluate(),
                resume.getEmail(),
                resume.getMp3Path(),
                resume.getInterviewEvaluate()
        );
    }

    /**
     * 将 Candidates 实体转换为 CandidateRecord 记录
     * 包含候选人基本信息（姓名、简历、邮箱、出生日期、状态、照片等）
     */
    public static CandidateRecord toCandidateRecord(Candidates candidates) {
        return new CandidateRecord(
                candidates.getId(),
                candidates.getName(),
                candidates.getCv(),
                candidates.getEmail(),
                candidates.getBirth(),
                candidates.getStatus().toString(),
                candidates.getPictureUrl());
    }
}
