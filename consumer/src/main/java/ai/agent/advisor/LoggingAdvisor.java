package ai.agent.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

public class LoggingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAdvisor.class);

    @Override
    public String getName() {
        return "LoggingAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        logger.info("\nRequest: " + advisedRequest);
        AdvisedResponse response = chain.nextAroundCall(advisedRequest);
        logger.info("\nResponse: " + response);
        return response;

    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        logger.info("\nRequest: " + advisedRequest);
        Flux<AdvisedResponse> responses = chain.nextAroundStream(advisedRequest);
        // MessageAggregator() 创建一个消息聚合器
        // aggregateAdvisedResponse(response, callback) 将流式对象 AdvisedResponse 聚合成单个相应 callback 就是一个 lambda 回调函数
        // 当聚合完成时，回调函数就会被触发，记录最终的响应内容到日志中
        return new MessageAggregator().aggregateAdvisedResponse(responses, aggregatedAdvisedResponse -> {
            logger.info("\nResponse: " + aggregatedAdvisedResponse);
        });
    }

}
