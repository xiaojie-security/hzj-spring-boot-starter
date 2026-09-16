package com.hzj.kuaidi100.provider.kuaidi100.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 快递100运行时业务配置。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Kuaidi100RuntimeConfig {

    /** 是否在未指定快递公司编码时启用智能单号判断。 */
    private Boolean intelligentJudgment;

    /**
     * 判断是否启用智能单号判断。
     *
     * @return 启用返回 true，否则返回 false
     */
    public boolean isIntelligentJudgmentEnabled() {
        return Boolean.TRUE.equals(intelligentJudgment);
    }
}
