package com.hzj.wechat.core.mobile.share.domain;

import com.hzj.wechat.core.mobile.share.enums.WechatOpenSdkShareMessageType;
import lombok.Data;

/**
 * 微信 OpenSDK 分享签名请求。
 * <p>图片哈希字段必须与客户端最终传入 OpenSDK 的图片字节完全一致。</p>
 */
@Data
public class WechatOpenSdkShareSignatureRequest {

    /** 分享消息类型。 */
    private WechatOpenSdkShareMessageType messageType;

    /** 文字分享内容。 */
    private String text;

    /** 图片二进制数据的 SHA-256 小写十六进制值。 */
    private String imgDataHash;

    /** 消息标题。 */
    private String title;

    /** 消息描述。 */
    private String description;

    /** 缩略图二进制数据的 SHA-256 小写十六进制值。 */
    private String thumbDataHash;

    /** 视频链接。 */
    private String videoUrl;

    /** 低带宽视频链接。 */
    private String videoLowBandUrl;

    /** 网页链接。 */
    private String webpageUrl;

    /** 小程序原始 ID。 */
    private String userName;

    /** 小程序页面路径。 */
    private String path;

    /** 音频网页链接。 */
    private String musicUrl;

    /** 音频数据链接。 */
    private String musicDataUrl;

    /** 歌手名称。 */
    private String singerName;

    /** 歌曲时长，单位为毫秒。 */
    private Long duration;

    /** 高清专辑封面文件的 SHA-256 小写十六进制值。 */
    private String hdAlbumThumbFileHash;

    /** 专辑名称。 */
    private String albumName;

    /** 音乐流派。 */
    private String musicGenre;

    /** 发行时间 Unix 时间戳，单位为秒。 */
    private Long issueDate;

    /** 音乐唯一标识。 */
    private String identification;
}
