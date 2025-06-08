package ai.agent.data;

/**
 * 候选人状态枚举类
 * 设计思路
 * 使用 code 存储到数据库中，节省存储空间
 * 使用 value 提供人类可读的描述
 * 重写 toString() 方法返回中文描述
 */
public enum CandidateStatus {
    WAIT(1, "等待"),
    NOTIFY(2, "通知"),
    FINISH(3, "完成");

    private Integer code; // Key
    private String value; // value

    CandidateStatus(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
