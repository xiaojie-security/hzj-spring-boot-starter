package com.hzj.wechat.provider.wechat.transfer;

import com.hzj.common.provider.StaticConfigProvider;
import com.hzj.wechat.provider.wechat.transfer.entity.WechatTransferConfig;

/**
 * 微信商家转账启动期静态配置提供者。
 * <p>
 * 配置包含商户身份、证书和 API 密钥，服务启动后不支持动态刷新。
 *
 * @author YourName
 * @version 1.0
 * @since 2026-07-15
 */
public interface WechatTransferConfigProvider extends StaticConfigProvider<WechatTransferConfig> {

}
