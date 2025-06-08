package ai.agent.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 将 code -> value (status)
 * 候选人状态转换器
 * 设计思路
 * 实现枚举与数据库整型的自动转换
 * autoApply = true 自动应用到所有相关字段
 * 提供双向转换：枚举(value) <-> 数据库整型(code)
 */
@Converter(autoApply = true)
public class CandidateStatusConverter implements AttributeConverter<CandidateStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CandidateStatus candidateStatus) {
        if (candidateStatus == null) {
            return null;
        }
        return candidateStatus.getCode();
    }

    @Override
    public CandidateStatus convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }

        for (CandidateStatus status : CandidateStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException();
    }
}
