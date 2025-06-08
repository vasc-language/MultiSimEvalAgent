package ai.agent.services;

import ai.agent.data.Candidates;
import ai.agent.exception.RestException;
import ai.agent.record.CandidateRecord;
import ai.agent.repository.CandidatesRepository;
import ai.agent.util.Dto2Record;
import ai.agent.util.ResultCode;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 候选人服务类
 * 负责处理候选人相关的业务逻辑，包括登录验证和候选人信息查询
 */
@Service
public class CandidatesService {
    // 候选人数据访问层，用于操作 candidates表
    private final CandidatesRepository interviewerRepository;

    public CandidatesService(CandidatesRepository interviewerRepository) {
        this.interviewerRepository = interviewerRepository;
    }

    public Candidates login(String name, String code) {
        // 根据姓名和状态码查询候选人信息
        Candidates interview = this.interviewerRepository.findByNameAndCode(name, code);
        if (interview == null) {
            throw new RestException(String.valueOf(ResultCode.FORBIDDEN.getCode()), "用户名或密码错误");
        }
        return interview;
    }

    /**
     * 获取所有候选人信息
     * @return 候选人纪录列表，包含所有候选人的基本信息
     */
    public List<CandidateRecord> getCandidates() {
        // 查询所有候选人，按ID降序排列（最新的在前面）
        List<Candidates> interviews = this.interviewerRepository.findAll(Sort.by("id").descending());
        // 将实体类对象转换为记录对象并返回
        return interviews.stream().map(Dto2Record::toCandidateRecord).toList();
    }
}
