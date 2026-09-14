package com.hzj.wechat.provider.wechat.sec_check.entity;

import com.hzj.wechat.core.xcx.sec_check.enums.WechatXcxSecCheckScene;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信内容安全动态配置。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatSecCheckConfig {

    /**
     * 内容安全检测的默认场景，未传时使用「资料」。
     */
    private WechatXcxSecCheckScene scene = WechatXcxSecCheckScene.PROFILE;
}
