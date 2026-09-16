package com.hzj.kuaidi100.core.print.domain;

import com.hzj.kuaidi100.core.common.Kuaidi100EnterpriseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 快递100云打印及自定义打印请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Kuaidi100PrintRequest extends Kuaidi100EnterpriseRequest {

    /** 附件打印时上传的文件。 */
    private File file;

    /** 发货单打印的纸张及模板设置。 */
    private Map<String, Object> settings = new LinkedHashMap<>();

    /**
     * 获取发货单打印设置副本。
     *
     * @return 打印设置副本
     */
    public Map<String, Object> copySettings() {
        return new LinkedHashMap<>(settings == null ? new LinkedHashMap<>() : settings);
    }
}
