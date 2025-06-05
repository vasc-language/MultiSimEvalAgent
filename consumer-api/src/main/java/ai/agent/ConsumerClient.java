package ai.agent;

import ai.agent.record.CandidateRecord;
import ai.agent.record.InterViewRecord;
import ai.agent.record.ProgramRecord;
import ai.agent.util.CommonResult;
import ai.agent.vo.CvRequest;
import ai.agent.vo.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = ConsumerConstant.SERVICE_NAME, url = "${consumer-service-endpoint}", configuration = FeignConfig.class)
public interface ConsumerClient {

    /**
     * 用户登录
     * @param name 用户名
     * @param code 验证码
     * @return 登录结果，包含用户信息
     */
    @PostMapping(value = "/login")
    CommonResult<UserResponse> login(@RequestParam("name") String name,
                                     @RequestParam("code") String code);

    /**
     * 验证用户令牌
     * @param token 用户令牌
     * @return 验证结果
     */
    @PostMapping(value = "/auth/verify-token")
    CommonResult<String> verifyToken(@RequestHeader("token") String token);

    /**
     * 获取面试记录列表
     * @return 面试记录列表
     */
    @GetMapping(value = "/frontend/interView")
    List<InterViewRecord> getInterView();

    /**
     * 获取候选人列表
     * @return 候选人记录列表
     */
    @GetMapping(value = "/frontend/candidates")
    List<CandidateRecord> getCandidates();

    /**
     * 发送邮件
     * @param name 收件人姓名
     */
    @PostMapping(value = "/frontend/sendMail")
    void sendMail(@RequestParam("name") String name);

    /**
     * 根据问题搜索面试记录
     * @param question 搜索关键词
     * @return 匹配的面试记录列表
     */
    @GetMapping(value = "/frontend/findInterView")
    List<InterViewRecord> findInterView(@RequestParam("question") String question);

    /**
     * 面对面聊天，支持语音交互
     * @param chatId 聊天会话ID
     * @param userName 用户名（可选）
     * @param audio 音频文件（可选）
     * @return 返回音频响应数据
     */
    @PostMapping(value="/interview/face2faceChat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/wav")
    ResponseEntity<byte[]> face2faceChat(@RequestParam("chatId") String chatId,
                                         @RequestParam(value ="userName", required = false) String userName,
                                         // 用于处理 HTTP 多部分请求（multipart/form-data） 的注解
                                         // 允许客户端通过表单上传音频文件（可选），并将文件内容封装为 MultipartFile 对象供后端处理
                                         @RequestPart(value = "audio", required = false) MultipartFile audio);

    /**
     * 获取欢迎语音文件
     * @return 欢迎语音的MP3数据
     */
    @GetMapping(value="/interview/welcomemp3", produces = "audio/mp3")
    ResponseEntity<byte[]> welcomemp3();

    /**
     * 制定面试方案
     * @param first 是否为首次制定方案
     * @param name 候选人姓名
     * @return 生成的面试方案
     */
    @GetMapping(value = "/interview/makeProgram")
    ResponseEntity<ProgramRecord> program(@RequestParam("first") Boolean first,
                                          @RequestParam("name") String name);

    /**
     * 上传简历文件
     * @param resumeFile 简历文件
     * @param jdText 职位描述文本
     * @return 上传结果
     */
    @PostMapping(value = "/resume/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    CommonResult<String> uploadResume(@RequestPart("resume") MultipartFile resumeFile,
                                      @RequestParam("jd") String jdText);

    /**
     * 创建简历
     * @param cvRequest 简历创建请求对象
     * @return 创建结果
     */
    @PostMapping(value = "/resume/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResult<String> createCv(@RequestBody CvRequest cvRequest);
}
