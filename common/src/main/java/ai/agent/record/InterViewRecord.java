package ai.agent.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * 完整面试纪录类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InterViewRecord(@JsonPropertyDescription("序号") String number, @JsonPropertyDescription("姓名") String name,
                              @JsonPropertyDescription("分数") String score, @JsonPropertyDescription("状态") String interviewStatus,
                              @JsonPropertyDescription("笔试评语") String evaluate, @JsonPropertyDescription("邮箱") String email,
                              @JsonPropertyDescription("音频评语") String mp3path, @JsonPropertyDescription("面试评语") String interviewEvaluate) {
}
