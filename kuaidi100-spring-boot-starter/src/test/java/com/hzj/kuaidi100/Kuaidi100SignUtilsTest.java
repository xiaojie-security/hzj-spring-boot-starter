package com.hzj.kuaidi100;

import com.hzj.kuaidi100.utils.Kuaidi100SignUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 快递100签名工具测试。
 */
class Kuaidi100SignUtilsTest {

    /**
     * 验证 MD5 签名按大写十六进制输出。
     */
    @Test
    void shouldCreateUppercaseMd5Signature() {
        assertThat(Kuaidi100SignUtils.md5("paramkeycustomer"))
                .isEqualTo("785D5D0008316A620B0781B634E22C6B");
    }
}
