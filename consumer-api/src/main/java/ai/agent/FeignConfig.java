package ai.agent;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * 自定义 Feign 客户端的编码器配置
 * 技术要点
 * - 双编码器配置：同时支持 JSON 和表单数据传输
 * - @Primary 注解：指定默认编码器，避免 Bean 冲突
 * - SpringFormEncoder：专门处理 multiparty/form-date 类型的文件上传
 * - 依赖注入：表单编码依赖主编码器实现
 */
@Configuration
public class FeignConfig {

    @Bean
    @Primary  // 主要编码器 - 处理 JSON 数据
    public Encoder feignEncoder() {
        return new SpringEncoder(new ObjectFactory<HttpMessageConverters>() {
            @Override
            public HttpMessageConverters getObject() {
                return new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
            }
        });
    }

    @Bean // 表单编码器 - 处理文件上传等表单数据
    public Encoder feignFormEncoder(Encoder encoder) {
        return new SpringFormEncoder(encoder);
    }

}