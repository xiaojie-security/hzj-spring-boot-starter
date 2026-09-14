package com.hzj.wechat.core.xcx.ad.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 广告位尺寸。
 */
@Data
@NoArgsConstructor
public class WechatXcxAdUnitSize {

    /**
     * 高度。
     */
    private Integer height;

    /**
     * 宽度。
     */
    private Integer width;
}
