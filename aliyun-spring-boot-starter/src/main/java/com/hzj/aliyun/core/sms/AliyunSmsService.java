package com.hzj.aliyun.core.sms;

/**
 * 阿里云短信服务
 */
public interface AliyunSmsService {

    /**
     * 发送短信
     * @param phoneNumber 手机号
     * @param templateCode 模板编号
     * @param templateParam 模板参数
     * @return 是否发送成功
     */
    boolean sendSmsCode(String phoneNumber, String templateCode, Object templateParam);

    /**
     * 发送短信
     * @param signName 签名
     * @param phoneNumber 手机号
     * @param templateCode 短信模板 Code
     * @param templateParam 短信模板变量对应的实际值，JSON 字符串。
     * @return 验证码
     */
    boolean sendSmsCode(String signName, String phoneNumber, String templateCode, Object templateParam);

}
