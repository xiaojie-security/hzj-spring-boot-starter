package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账单下载地址查询参数。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayBillDownloadUrlQueryParam {

    /**
     * 账单时间。
     */
    private String billDate;

    /**
     * 账单类型。
     */
    private String billType;

    /**
     * 二级商户 smid。
     */
    private String smid;
}
