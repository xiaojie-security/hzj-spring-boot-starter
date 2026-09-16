package com.hzj.wechat.provider.wechat.subscribe_message.entity;

import com.hzj.wechat.core.xcx.subscribe_message.enums.WechatXcxSubscribeMessageLang;
import com.hzj.wechat.core.xcx.subscribe_message.enums.WechatXcxSubscribeMessageMiniProgramState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信订阅消息动态配置。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatSubscribeMessageRuntimeConfig {

    /**
     * 发送订阅消息时跳转小程序的默认类型。
     */
    private WechatXcxSubscribeMessageMiniProgramState miniprogramState;

    /**
     * 发送订阅消息时进入小程序查看的默认语言类型。
     */
    private WechatXcxSubscribeMessageLang lang;
}
