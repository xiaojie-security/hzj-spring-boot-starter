package com.hzj.wechat.core.mobile.share;

import com.hzj.wechat.core.mobile.share.domain.WechatOpenSdkShareSignatureRequest;
import com.hzj.wechat.core.mobile.share.domain.WechatOpenSdkShareSignatureResponse;

/**
 * 微信 OpenSDK 分享签名服务。
 */
public interface WechatOpenSdkShareSignatureService {

    /**
     * 根据微信 OpenSDK 规则生成分享消息签名。
     *
     * @param request 分享签名请求
     * @return 分享签名响应
     */
    WechatOpenSdkShareSignatureResponse sign(WechatOpenSdkShareSignatureRequest request);

    /**
     * 计算二进制内容的 SHA-256 小写十六进制值。
     *
     * @param content 二进制内容
     * @return SHA-256 小写十六进制值
     */
    String calculateSha256(byte[] content);
}
