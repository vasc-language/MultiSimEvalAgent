package ai.agent.audio.services;

import com.alibaba.dashscope.audio.tts.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.tts.SpeechSynthesizer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 音频服务类，负责将文本转换为语音文件。
 * 使用阿里云DashScope的语音合成服务，将面试评价等文本内容转换为MP3音频文件。
 */
@RequiredArgsConstructor
@Service
public class AudioService {

    /** 阿里云DashScope API密钥 */
    @Value("${spring.ai.dash-scope.audio.api-key}")
    private String apiKey;

    /** 语音合成模型名称 */
    @Value("${spring.ai.dash-scope.audio.options.model}")
    private String model;

    /**
     * 将文本转换为语音文件
     * @param title 音频文件标题，用于生成文件名
     * @param question 要转换的文本内容
     * @return 生成的音频文件的绝对路径
     */
    public String getSpeech(String title,String question) {
        // 创建语音合成器
        SpeechSynthesizer synthesizer = new SpeechSynthesizer();

        // 构建语音合成参数
        SpeechSynthesisParam param =
                SpeechSynthesisParam.builder()
                        .apiKey(apiKey)
                        .model(model)
                        .text(question)
                        .sampleRate(48000)  // 设置采样率为48kHz
                        .build();

        // 生成音频文件路径
        String name = "audio/" + title + ".mp3";
        File file = new File(name);

        // 如果文件已存在，先删除
        if (file.exists()) {
            file.delete();
        }

        // 调用语音合成API，获取音频数据
        ByteBuffer audio = synthesizer.call(param);

        // 将音频数据写入文件
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(audio.array());
            System.out.println("synthesis done!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 返回音频文件的绝对路径
        return file.getAbsolutePath();
    }
}
