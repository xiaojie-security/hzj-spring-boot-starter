package com.hzj.alipay.config;

import com.hzj.alipay.core.transfer.AlipayTransferService;
import com.hzj.alipay.core.transfer.impl.DefaultAlipayTransferService;
import com.hzj.alipay.provider.AlipayConfigProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "alipay.transfer", name = "enable", havingValue = "true", matchIfMissing = true)
public class AlipayTransferConfiguration {


    /**
     * 装配支付宝转账服务。
     *
     * @return 转账服务
     */
    @Bean
    @ConditionalOnMissingBean(AlipayTransferService.class)
    public AlipayTransferService alipayTransferService(AlipayConfigProvider provider) {
        return new DefaultAlipayTransferService(provider);
    }
}
