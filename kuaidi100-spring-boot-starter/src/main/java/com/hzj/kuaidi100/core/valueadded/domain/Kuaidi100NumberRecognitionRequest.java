package com.hzj.kuaidi100.core.valueadded.domain;

import lombok.Data;

/**
 * 快递100智能单号识别请求。
 */
@Data
public class Kuaidi100NumberRecognitionRequest {

    /** 待识别快递单号。 */
    private String number;
}
