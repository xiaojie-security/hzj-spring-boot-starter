package com.aliyun.core.pns;

import com.aliyun.core.pns.domain.AliyunPnsTemplateParam;

public interface AliyunPnsService {

    /**
     * 发送短信
     * @param schemeName 方案名称
     * @param phoneNumber 短信接收手机号
     * @param templateCode 短信模板
     * @return 短信发送结果
     */
    boolean sendSmsCode(String schemeName, String phoneNumber, String templateCode, AliyunPnsTemplateParam aliyunPnsTemplateParam);


    /**
     * 校验短信验证码
     * @param schemeName 方案名称
     * @param phoneNumber 短信接收手机号
     * @param verifyCode 短信验证码
     * @return 短信验证码校验结果
     */
    boolean checkSmsVerifyCode(String schemeName,String phoneNumber, String verifyCode);

    /**
     * 通过阿里云号码认证 token 获取手机号。
     * @param accessToken 访问令牌
     * @return 手机号
     */
    String getMobile(String accessToken);
}
