package ai.agent.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.Date;

/**
 * 候选人档案纪录类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CandidateRecord(@JsonPropertyDescription("序号") Long number, @JsonPropertyDescription("候选人姓名") String name,
                              @JsonPropertyDescription("简历") String cv, @JsonPropertyDescription("邮箱") String email,
                              @JsonPropertyDescription("出生日期") Date birth, @JsonPropertyDescription("状态") String status,
                              @JsonPropertyDescription("照片url") String pictureUrl) {
}
