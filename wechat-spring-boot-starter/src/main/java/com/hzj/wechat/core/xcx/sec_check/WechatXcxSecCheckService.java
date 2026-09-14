package com.hzj.wechat.core.xcx.sec_check;

import com.hzj.wechat.core.xcx.sec_check.domain.WechatXcxSecCheckMediaRequest;
import com.hzj.wechat.core.xcx.sec_check.domain.WechatXcxSecCheckMediaResponse;
import com.hzj.wechat.core.xcx.sec_check.domain.WechatXcxSecCheckMsgRequest;
import com.hzj.wechat.core.xcx.sec_check.domain.WechatXcxSecCheckMsgResponse;
import com.hzj.wechat.core.xcx.sec_check.enums.WechatXcxSecCheckMediaType;

/**
 * 微信小程序内容安全服务。
 *
 * <p>封装「文本内容安全识别 msgSecCheck」与「多媒体内容安全识别 mediaCheckAsync」两个接口。
 * 其中多媒体检测为异步接口，同步响应只返回 {@code trace_id}，检测结果由微信推送。</p>
 */
public interface WechatXcxSecCheckService {

    /**
     * 文本内容安全识别。
     * 检查一段文本是否含有违法违规内容，单次检测文本上限 2500 字。
     *
     * @param request 文本内容安全识别请求参数
     * @return 检测结果，包含综合结果 result 与详细结果 detail
     */
    WechatXcxSecCheckMsgResponse msgSecCheck(WechatXcxSecCheckMsgRequest request);

    /**
     * 文本内容安全识别。
     * 场景值未设置时使用配置提供者的默认值，仍为空时使用「资料」。
     *
     * @param content 需检测的文本内容
     * @param openid  用户的 openid
     * @return 检测结果
     */
    WechatXcxSecCheckMsgResponse msgSecCheck(String content, String openid);

    /**
     * 多媒体内容安全识别。
     * 异步校验图片或音频是否含有违法违规内容，单个文件不超过 10M。
     *
     * @param request 多媒体内容安全识别请求参数
     * @return 检测结果，包含用于匹配异步推送结果的 trace_id
     */
    WechatXcxSecCheckMediaResponse mediaCheckAsync(WechatXcxSecCheckMediaRequest request);

    /**
     * 多媒体内容安全识别。
     * 场景值未设置时使用配置提供者的默认值，仍为空时使用「资料」。
     *
     * @param mediaUrl  要检测的图片或音频的 url
     * @param mediaType 多媒体类型
     * @param openid    用户的 openid
     * @return 检测结果
     */
    WechatXcxSecCheckMediaResponse mediaCheckAsync(String mediaUrl,
                                                   WechatXcxSecCheckMediaType mediaType,
                                                   String openid);
}
