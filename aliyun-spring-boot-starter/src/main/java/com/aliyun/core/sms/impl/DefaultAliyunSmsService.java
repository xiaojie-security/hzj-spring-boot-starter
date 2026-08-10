package com.aliyun.core.sms.impl;

import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.core.sms.AliyunSmsService;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.provider.aliyun.sms.AliyunSmsConfigProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 阿里云短信服务默认实现。
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAliyunSmsService implements AliyunSmsService {

    private final AliyunSmsConfigProvider configProvider;
    private final com.aliyun.dysmsapi20170525.Client client;

    @Override
    public boolean sendSmsCode(String phoneNumber, String templateCode, Object templateParam) {
        return sendSmsCode(configProvider.getConfig().getSignName(), phoneNumber, templateCode, templateParam);
    }

    @Override
    public boolean sendSmsCode(String signName, String phoneNumber, String templateCode, Object templateParam) {
        if (StrUtil.isEmpty(phoneNumber)) {
            log.warn("DefaultAliyunSmsService.sendSmsCode 手机号不能为空");
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (!PhoneUtil.isPhone(phoneNumber)) {
            log.warn("DefaultAliyunSmsService.sendSmsCode 手机号格式不正确，手机号={}", phoneNumber);
            throw new IllegalArgumentException("手机号格式不正确");
        }
        CharSequence logPhone = PhoneUtil.hideBetween(phoneNumber);
        try {
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumber)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam(JSONUtil.toJsonStr(templateParam));
            com.aliyun.dysmsapi20170525.models.SendSmsResponseBody body = client.sendSms(request).getBody();
            log.info("DefaultAliyunSmsService.sendSmsCode phone={} 发送短信结果={}", logPhone, JSONUtil.toJsonStr(body));
            return Objects.equals(body.getCode(), "OK");
        } catch (Exception e) {
            log.error("DefaultAliyunSmsService.sendSmsCode phone={} 发送短信异常", logPhone, e);
            return false;
        }
    }
}
