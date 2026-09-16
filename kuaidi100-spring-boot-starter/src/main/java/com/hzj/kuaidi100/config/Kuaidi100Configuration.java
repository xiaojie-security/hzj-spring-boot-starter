package com.hzj.kuaidi100.config;

import com.hzj.kuaidi100.client.Kuaidi100HttpClient;
import com.hzj.kuaidi100.core.order.Kuaidi100OrderImportService;
import com.hzj.kuaidi100.core.order.impl.DefaultKuaidi100OrderImportService;
import com.hzj.kuaidi100.core.print.Kuaidi100PrintService;
import com.hzj.kuaidi100.core.print.impl.DefaultKuaidi100PrintService;
import com.hzj.kuaidi100.core.query.Kuaidi100QueryService;
import com.hzj.kuaidi100.core.query.impl.DefaultKuaidi100QueryService;
import com.hzj.kuaidi100.core.valueadded.Kuaidi100ValueAddedService;
import com.hzj.kuaidi100.core.valueadded.impl.DefaultKuaidi100ValueAddedService;
import com.hzj.kuaidi100.core.waybill.Kuaidi100WaybillService;
import com.hzj.kuaidi100.core.waybill.impl.DefaultKuaidi100WaybillService;
import com.hzj.kuaidi100.provider.kuaidi100.Kuaidi100RuntimeConfigProvider;
import com.hzj.kuaidi100.provider.kuaidi100.Kuaidi100StaticConfigProvider;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 快递100自动装配配置。
 *
 * <p>仅当使用方提供快递100静态授权配置 Provider 时才装配业务服务；运行时配置 Provider 为可选依赖。</p>
 */
@AutoConfiguration
@ConditionalOnBean(Kuaidi100StaticConfigProvider.class)
public class Kuaidi100Configuration {

    /**
     * 注册快递100 HTTP客户端。
     *
     * @param staticConfigProvider 静态授权配置提供者
     * @param clientProvider 应用自定义 OkHttp客户端
     * @return 快递100 HTTP客户端
     */
    @Bean
    @ConditionalOnMissingBean(Kuaidi100HttpClient.class)
    public Kuaidi100HttpClient kuaidi100HttpClient(Kuaidi100StaticConfigProvider staticConfigProvider,
                                                   ObjectProvider<OkHttpClient> clientProvider) {
        OkHttpClient client = clientProvider.getIfAvailable(() -> new OkHttpClient.Builder().build());
        return new Kuaidi100HttpClient(staticConfigProvider, client);
    }

    /**
     * 注册查询服务。
     *
     * @param httpClient 快递100 HTTP客户端
     * @param runtimeProvider 运行时配置提供者
     * @return 查询服务
     */
    @Bean
    @ConditionalOnMissingBean(Kuaidi100QueryService.class)
    public Kuaidi100QueryService kuaidi100QueryService(
            Kuaidi100HttpClient httpClient,
            ObjectProvider<Kuaidi100RuntimeConfigProvider> runtimeProvider) {
        return new DefaultKuaidi100QueryService(httpClient, runtimeProvider.getIfAvailable());
    }

    /**
     * 注册电子面单服务。
     *
     * @param httpClient 快递100 HTTP客户端
     * @return 电子面单服务
     */
    @Bean
    @ConditionalOnMissingBean(Kuaidi100WaybillService.class)
    public Kuaidi100WaybillService kuaidi100WaybillService(Kuaidi100HttpClient httpClient) {
        return new DefaultKuaidi100WaybillService(httpClient);
    }

    /**
     * 注册打印服务。
     *
     * @param httpClient 快递100 HTTP客户端
     * @return 打印服务
     */
    @Bean
    @ConditionalOnMissingBean(Kuaidi100PrintService.class)
    public Kuaidi100PrintService kuaidi100PrintService(Kuaidi100HttpClient httpClient) {
        return new DefaultKuaidi100PrintService(httpClient);
    }

    /**
     * 注册订单导入服务。
     *
     * @param httpClient 快递100 HTTP客户端
     * @return 订单导入服务
     */
    @Bean
    @ConditionalOnMissingBean(Kuaidi100OrderImportService.class)
    public Kuaidi100OrderImportService kuaidi100OrderImportService(Kuaidi100HttpClient httpClient) {
        return new DefaultKuaidi100OrderImportService(httpClient);
    }

    /**
     * 注册增值服务。
     *
     * @param httpClient 快递100 HTTP客户端
     * @return 增值服务
     */
    @Bean
    @ConditionalOnMissingBean(Kuaidi100ValueAddedService.class)
    public Kuaidi100ValueAddedService kuaidi100ValueAddedService(Kuaidi100HttpClient httpClient) {
        return new DefaultKuaidi100ValueAddedService(httpClient);
    }
}
