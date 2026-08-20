package com.hzj.wechat.core.mobile.launch;

import com.hzj.wechat.core.mobile.launch.domain.WechatH5JsSdkSignatureRequest;
import com.hzj.wechat.core.mobile.launch.domain.WechatH5JsSdkSignatureResponse;
import com.hzj.wechat.core.mobile.launch.domain.WechatH5LaunchAppSceneIssueRequest;
import com.hzj.wechat.core.mobile.launch.domain.WechatH5LaunchAppSceneResolveResponse;
import com.hzj.wechat.core.mobile.launch.domain.WechatH5LaunchAppSceneResponse;

/**
 * 微信 H5 JS-SDK 签名与 Launch App 场景服务。
 */
public interface WechatH5LaunchAppService {

    /**
     * 生成微信 H5 JS-SDK 配置签名。
     *
     * @param request H5 页面签名请求
     * @return JS-SDK 配置签名
     */
    WechatH5JsSdkSignatureResponse signJsSdk(WechatH5JsSdkSignatureRequest request);

    /**
     * 签发 Launch App 短期场景码。
     *
     * @param request App 路由场景请求
     * @return 场景码、落地页地址和移动应用 AppID
     */
    WechatH5LaunchAppSceneResponse issueScene(WechatH5LaunchAppSceneIssueRequest request);

    /**
     * 解析并校验 Launch App 场景码。
     *
     * @param scene 场景码
     * @return 已校验的 App 路由场景
     */
    WechatH5LaunchAppSceneResolveResponse resolveScene(String scene);
}
