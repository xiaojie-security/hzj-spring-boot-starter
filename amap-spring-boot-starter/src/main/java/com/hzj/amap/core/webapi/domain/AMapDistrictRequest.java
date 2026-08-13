package com.hzj.amap.core.webapi.domain;

import com.hzj.amap.core.webapi.enums.AMapDistrictDepth;
import com.hzj.amap.core.webapi.enums.AMapExtensions;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 行政区域查询请求参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapDistrictRequest extends AMapWebApiRequest {

    /**
     * 行政区名称或区域编码。
     */
    private String keywords;

    /**
     * 子级行政区深度。
     */
    private AMapDistrictDepth subdistrict = AMapDistrictDepth.ONE;

    /**
     * 行政区边界坐标格式。
     */
    private AMapExtensions extensions = AMapExtensions.BASE;

    /**
     * 返回结果页码。
     */
    private Integer page = 1;

    /**
     * 每页记录数。
     */
    private Integer offset = 20;

    /**
     * 创建行政区域查询请求。
     */
    public AMapDistrictRequest() {
        super("https://restapi.amap.com/v3/config/district");
    }

    @Override
    public Map<String, String> toQueryParameters() {
        Map<String, String> parameters = createParameters();
        putIfNotBlank(parameters, "keywords", keywords);
        putIfNotBlank(parameters, "subdistrict", subdistrict == null ? null : subdistrict.getValue());
        putIfNotBlank(parameters, "extensions", extensions == null ? null : extensions.getValue());
        putIfNotBlank(parameters, "page", page);
        putIfNotBlank(parameters, "offset", offset);
        return parameters;
    }
}
