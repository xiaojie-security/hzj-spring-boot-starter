package com.hzj.kuaidi100.core.valueadded.domain;

import com.hzj.kuaidi100.core.common.Kuaidi100EnterpriseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;

/**
 * 快递100电子云签请求。
 *
 * <p>文件上传接口使用 file 字段，回单注册接口只使用父类的 param 参数。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Kuaidi100ElectronicCloudSignRequest extends Kuaidi100EnterpriseRequest {

    /** 待上传回单文件。 */
    private File file;
}
