package com.hzj.kuaidi100;

import com.hzj.kuaidi100.config.Kuaidi100Configuration;
import com.hzj.kuaidi100.core.order.Kuaidi100OrderImportService;
import com.hzj.kuaidi100.core.print.Kuaidi100PrintService;
import com.hzj.kuaidi100.core.query.Kuaidi100QueryService;
import com.hzj.kuaidi100.core.valueadded.Kuaidi100ValueAddedService;
import com.hzj.kuaidi100.core.waybill.Kuaidi100WaybillService;
import com.hzj.kuaidi100.provider.kuaidi100.Kuaidi100StaticConfigProvider;
import com.hzj.kuaidi100.provider.kuaidi100.entity.Kuaidi100StaticConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 快递100自动装配测试。
 */
class Kuaidi100ConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(Kuaidi100Configuration.class);

    /**
     * 未提供静态授权配置时不装配服务。
     */
    @Test
    void shouldNotCreateServicesWithoutStaticProvider() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(Kuaidi100QueryService.class));
    }

    /**
     * 提供静态授权配置时装配所有业务域服务。
     */
    @Test
    void shouldCreateAllServicesWithStaticProvider() {
        contextRunner.withBean(Kuaidi100StaticConfigProvider.class, () -> () ->
                        new Kuaidi100StaticConfig("key", "customer", "secret", "userId"))
                .run(context -> {
                    assertThat(context).hasSingleBean(Kuaidi100QueryService.class);
                    assertThat(context).hasSingleBean(Kuaidi100WaybillService.class);
                    assertThat(context).hasSingleBean(Kuaidi100PrintService.class);
                    assertThat(context).hasSingleBean(Kuaidi100OrderImportService.class);
                    assertThat(context).hasSingleBean(Kuaidi100ValueAddedService.class);
                });
    }
}
