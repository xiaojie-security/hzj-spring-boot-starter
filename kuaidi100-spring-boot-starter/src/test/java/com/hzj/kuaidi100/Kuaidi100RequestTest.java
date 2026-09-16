package com.hzj.kuaidi100;

import com.hzj.kuaidi100.core.query.domain.Kuaidi100QueryRequest;
import com.hzj.kuaidi100.core.query.domain.Kuaidi100SubscribeRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 快递100请求参数测试。
 */
class Kuaidi100RequestTest {

    /**
     * 智能判断开启时不提交快递公司编码。
     */
    @Test
    void shouldEnableIntelligentJudgmentForQuery() {
        Kuaidi100QueryRequest request = new Kuaidi100QueryRequest();
        request.setNum("YT123456789");

        Map<String, Object> parameters = request.toParameters(true);

        assertThat(parameters).doesNotContainKey("com");
        assertThat(parameters).containsEntry("num", "YT123456789");
    }

    /**
     * 订阅智能判断使用官方要求的 autoCom 参数。
     */
    @Test
    void shouldPutAutoComIntoSubscriptionParameters() {
        Kuaidi100SubscribeRequest request = new Kuaidi100SubscribeRequest();
        request.setNumber("YT123456789");
        request.setCallbackUrl("https://example.com/kuaidi");

        Map<String, Object> parameters = request.toParameters("key", true);

        assertThat(parameters).doesNotContainKey("company");
        assertThat(parameters).containsEntry("key", "key");
        @SuppressWarnings("unchecked")
        Map<String, Object> subscriptionParameters =
                (Map<String, Object>) parameters.get("parameters");
        assertThat(subscriptionParameters).containsEntry("autoCom", "1");
    }
}
