package com.hzj.amap.core.webapi.domain;

import com.hzj.amap.core.webapi.enums.AMapStaticMapFormat;
import com.hzj.amap.core.webapi.enums.AMapStaticMapType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 静态地图请求参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapStaticMapRequest extends AMapWebApiRequest {

    /**
     * 地图中心点，格式为经度,纬度。
     */
    private String location;

    /**
     * 地图缩放级别。
     */
    private Integer zoom;

    /**
     * 图片尺寸，格式为宽*高。
     */
    private String size = "750*300";

    /**
     * 标注点集合。
     */
    private String markers;

    /**
     * 路径绘制参数。
     */
    private String paths;

    /**
     * 地图类型。
     */
    private AMapStaticMapType mapType;

    /**
     * 图片格式。
     */
    private AMapStaticMapFormat format = AMapStaticMapFormat.PNG;

    /**
     * 创建静态地图请求。
     */
    public AMapStaticMapRequest() {
        super("https://restapi.amap.com/v3/staticmap");
    }

    @Override
    public Map<String, String> toQueryParameters() {
        Map<String, String> parameters = createParameters();
        putIfNotBlank(parameters, "location", location);
        putIfNotBlank(parameters, "zoom", zoom);
        putIfNotBlank(parameters, "size", size);
        putIfNotBlank(parameters, "markers", markers);
        putIfNotBlank(parameters, "paths", paths);
        putIfNotBlank(parameters, "maptype", mapType == null ? null : mapType.getValue());
        putIfNotBlank(parameters, "format", format == null ? null : format.getValue());
        return parameters;
    }
}
