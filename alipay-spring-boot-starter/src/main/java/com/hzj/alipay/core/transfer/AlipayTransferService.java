package com.hzj.alipay.core.transfer;

import com.hzj.alipay.core.transfer.domain.AliPayBillDownloadUrlQueryParam;
import com.hzj.alipay.core.transfer.domain.AliPayBillDownloadUrlResult;
import com.hzj.alipay.core.transfer.domain.AliPayEreceiptApplyParam;
import com.hzj.alipay.core.transfer.domain.AliPayEreceiptApplyResult;
import com.hzj.alipay.core.transfer.domain.AliPayEreceiptQueryParam;
import com.hzj.alipay.core.transfer.domain.AliPayEreceiptQueryResult;
import com.hzj.alipay.core.transfer.domain.AliPayFundAccountQueryParam;
import com.hzj.alipay.core.transfer.domain.AliPayFundAccountQueryResult;
import com.hzj.alipay.core.transfer.domain.AliPayFundQuotaQueryParam;
import com.hzj.alipay.core.transfer.domain.AliPayFundQuotaQueryResult;
import com.hzj.alipay.core.transfer.domain.AliPayTransferParam;
import com.hzj.alipay.core.transfer.domain.AliPayTransferQueryParam;
import com.hzj.alipay.core.transfer.domain.AliPayTransferQueryResult;
import com.hzj.alipay.core.transfer.domain.AliPayTransferResult;

public interface AlipayTransferService {

    /**
     * 支付宝资金账户资产查询。
     *
     * @param queryParam 查询参数
     * @return 查询结果
     */
    AliPayFundAccountQueryResult accountQuery(AliPayFundAccountQueryParam queryParam);

    /**
     * 查询转账额度。
     *
     * @param queryParam 查询参数
     * @return 查询结果
     */
    AliPayFundQuotaQueryResult quotaQuery(AliPayFundQuotaQueryParam queryParam);

    /**
     * 申请电子回单。
     *
     * @param applyParam 申请参数
     * @return 申请结果
     */
    AliPayEreceiptApplyResult applyEreceipt(AliPayEreceiptApplyParam applyParam);

    /**
     * 查询电子回单状态。
     *
     * @param queryParam 查询参数
     * @return 查询结果
     */
    AliPayEreceiptQueryResult queryEreceipt(AliPayEreceiptQueryParam queryParam);

    /**
     * 发起单笔转账。
     *
     * @param transferParam 转账参数
     * @return 转账结果
     */
    AliPayTransferResult transfer(AliPayTransferParam transferParam);

    /**
     * 查询转账单据。
     *
     * @param queryParam 查询参数
     * @return 查询结果
     */
    AliPayTransferQueryResult query(AliPayTransferQueryParam queryParam);

    /**
     * 查询账单下载地址。
     *
     * @param queryParam 查询参数
     * @return 查询结果
     */
    AliPayBillDownloadUrlResult queryBillDownloadUrl(AliPayBillDownloadUrlQueryParam queryParam);
}
