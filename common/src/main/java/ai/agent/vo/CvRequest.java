package ai.agent.vo;

import ai.agent.record.EducationRecord;
import ai.agent.record.ProjectExperienceRecord;
import ai.agent.record.WorkExperienceRecord;

import java.util.List;

/**
 * 简历分析模块
 * 简历信息请求类
 */
public class CvRequest {
    private String name;
    private String sex;
    private String birthDate;
    private String email;
    private String phone;
    private String jd;
    // 教育信息
    private List<EducationRecord> educationRecords;
    // 工作信息
    private List<WorkExperienceRecord> workExperienceRecords;
    // 项目信息
    private List<ProjectExperienceRecord> projectExperienceRecords;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJd() {
        return jd;
    }

    public void setJd(String jd) {
        this.jd = jd;
    }

    public List<EducationRecord> getEducationRecords() {
        return educationRecords;
    }

    public void setEducationRecords(List<EducationRecord> educationRecords) {
        this.educationRecords = educationRecords;
    }

    public List<WorkExperienceRecord> getWorkExperienceRecords() {
        return workExperienceRecords;
    }

    public void setWorkExperienceRecords(List<WorkExperienceRecord> workExperienceRecords) {
        this.workExperienceRecords = workExperienceRecords;
    }

    public List<ProjectExperienceRecord> getProjectExperienceRecords() {
        return projectExperienceRecords;
    }

    public void setProjectExperienceRecords(List<ProjectExperienceRecord> projectExperienceRecords) {
        this.projectExperienceRecords = projectExperienceRecords;
    }
}
