package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转账参与方信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTransferParticipant {

    /**
     * 银行卡扩展信息。
     */
    private AliPayBankcardExtInfo bankcardExtInfo;

    /**
     * 参与方证件号。
     */
    private String certNo;

    /**
     * 参与方证件类型。
     */
    private String certType;

    /**
     * 参与方扩展信息。
     */
    private String extInfo;

    /**
     * 参与方唯一标识。
     */
    private String identity;

    /**
     * 参与方标识类型。
     */
    private String identityType;

    /**
     * 商户侧用户信息描述。
     */
    private String merchantUserInfo;

    /**
     * 参与方真实姓名。
     */
    private String name;
}
