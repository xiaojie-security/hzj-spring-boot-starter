package com.hzj.amap.core.webapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德输入提示响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapInputTipsResponse extends AMapWebApiResponse {

    /** 输入提示列表。 */
    private List<AMapInputTip> tips;

    /**
     * 输入提示信息。
     */
    @Data
    public static class AMapInputTip {
        /** 兴趣点标识。 */
        private String id;
        /** 兴趣点名称。 */
        private String name;
        /** 兴趣点类型编码。 */
        private String typecode;
        /** 行政区编码。 */
        private String adcode;
        /** 地址。 */
        private String address;
        /** 坐标。 */
        private String location;
        /** 所属区域。 */
        private String district;
    }
}
