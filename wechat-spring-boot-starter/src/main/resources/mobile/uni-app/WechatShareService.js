/**
 * 微信分享场景。
 */
export const WechatShareScene = Object.freeze({
  /** 分享到微信聊天。 */
  SESSION: 'WXSceneSession',
  /** 分享到微信朋友圈。 */
  TIMELINE: 'WXSceneTimeline',
  /** 分享到微信收藏。 */
  FAVORITE: 'WXSceneFavorite'
})

/**
 * 微信小程序版本。
 */
export const WechatMiniProgramType = Object.freeze({
  /** 正式版。 */
  RELEASE: 0,
  /** 测试版。 */
  TEST: 1,
  /** 体验版。 */
  PREVIEW: 2
})

/**
 * uni-app 微信分享服务。
 * <p>仅适用于 App 端，使用前需要在 manifest.json 中配置 uni-share 微信 AppID。</p>
 */
export default class WechatShareService {
  /** 微信分享服务提供商。 */
  static get PROVIDER() {
    return 'weixin'
  }

  /** 图文分享类型。 */
  static get WEBPAGE_TYPE() {
    return 0
  }

  /** 文字分享类型。 */
  static get TEXT_TYPE() {
    return 1
  }

  /** 图片分享类型。 */
  static get IMAGE_TYPE() {
    return 2
  }

  /** 音乐分享类型。 */
  static get MUSIC_TYPE() {
    return 3
  }

  /** 视频分享类型。 */
  static get VIDEO_TYPE() {
    return 4
  }

  /** 小程序分享类型。 */
  static get MINI_PROGRAM_TYPE() {
    return 5
  }

  /**
   * 查询当前 App 是否已配置微信分享服务。
   *
   * @returns {Promise<boolean>} 是否可使用微信分享。
   */
  static async isAvailable() {
    if (typeof uni === 'undefined' || typeof uni.getProvider !== 'function') {
      return false
    }
    return new Promise((resolve) => {
      uni.getProvider({
        service: 'share',
        success: (result) => {
          resolve(Array.isArray(result.provider) && result.provider.includes(this.PROVIDER))
        },
        fail: () => resolve(false)
      })
    })
  }

  /**
   * 分享文字到微信。
   *
   * @param {Object} options 分享参数。
   * @param {String} options.summary 文字内容。
   * @param {String} [options.title] 文字标题。
   * @param {String} [options.scene] 微信分享场景。
   * @returns {Promise<Object>} uni.share 成功回调结果。
   */
  static shareText(options) {
    this.requireText(options?.summary, 'summary')
    return this.share({
      ...options,
      type: this.TEXT_TYPE
    })
  }

  /**
   * 分享图片到微信。
   *
   * @param {Object} options 分享参数。
   * @param {String} options.imageUrl 图片本地路径或网络地址。
   * @param {String} [options.title] 图片标题。
   * @param {String} [options.scene] 微信分享场景。
   * @returns {Promise<Object>} uni.share 成功回调结果。
   */
  static shareImage(options) {
    this.requireText(options?.imageUrl, 'imageUrl')
    return this.share({
      ...options,
      type: this.IMAGE_TYPE
    })
  }

  /**
   * 分享网页图文到微信。
   *
   * @param {Object} options 分享参数。
   * @param {String} options.title 分享标题。
   * @param {String} options.summary 分享摘要。
   * @param {String} options.href 网页链接。
   * @param {String} options.imageUrl 缩略图本地路径或网络地址。
   * @param {String} [options.scene] 微信分享场景。
   * @returns {Promise<Object>} uni.share 成功回调结果。
   */
  static shareWebpage(options) {
    this.requireText(options?.href, 'href')
    this.requireText(options?.imageUrl, 'imageUrl')
    return this.share({
      ...options,
      type: this.WEBPAGE_TYPE
    })
  }

  /**
   * 分享音乐到微信。
   *
   * @param {Object} options 分享参数。
   * @param {String} options.title 音乐标题。
   * @param {String} options.summary 音乐摘要。
   * @param {String} options.mediaUrl 音乐媒体地址。
   * @param {String} [options.href] 音乐详情页链接。
   * @param {String} [options.imageUrl] 缩略图本地路径或网络地址。
   * @param {String} [options.scene] 微信分享场景。
   * @returns {Promise<Object>} uni.share 成功回调结果。
   */
  static shareMusic(options) {
    this.requireText(options?.mediaUrl, 'mediaUrl')
    return this.share({
      ...options,
      type: this.MUSIC_TYPE
    })
  }

  /**
   * 分享视频到微信。
   *
   * @param {Object} options 分享参数。
   * @param {String} options.title 视频标题。
   * @param {String} options.summary 视频摘要。
   * @param {String} options.mediaUrl 视频媒体地址。
   * @param {String} [options.href] 视频详情页链接。
   * @param {String} [options.imageUrl] 缩略图本地路径或网络地址。
   * @param {String} [options.scene] 微信分享场景。
   * @returns {Promise<Object>} uni.share 成功回调结果。
   */
  static shareVideo(options) {
    this.requireText(options?.mediaUrl, 'mediaUrl')
    return this.share({
      ...options,
      type: this.VIDEO_TYPE
    })
  }

  /**
   * 分享小程序卡片到微信聊天。
   * <p>微信仅支持将小程序卡片分享到聊天界面。</p>
   *
   * @param {Object} options 分享参数。
   * @param {String} options.title 小程序卡片标题。
   * @param {String} options.imageUrl 小程序卡片缩略图。
   * @param {Object} options.miniProgram 小程序信息。
   * @param {String} options.miniProgram.id 小程序原始 ID。
   * @param {String} [options.miniProgram.path] 小程序页面路径。
   * @param {Number} [options.miniProgram.type] 小程序版本。
   * @param {String} [options.miniProgram.webUrl] 低版本兼容网页链接。
   * @returns {Promise<Object>} uni.share 成功回调结果。
   */
  static shareMiniProgram(options) {
    this.requireText(options?.imageUrl, 'imageUrl')
    this.requireText(options?.miniProgram?.id, 'miniProgram.id')
    return this.share({
      ...options,
      scene: WechatShareScene.SESSION,
      type: this.MINI_PROGRAM_TYPE,
      miniProgram: {
        ...options.miniProgram,
        type: options.miniProgram.type ?? WechatMiniProgramType.RELEASE
      }
    })
  }

  /**
   * 拉起微信客服会话。
   *
   * @param {Object} options 客服会话参数。
   * @param {String} options.corpid 客服企业 ID。
   * @param {String} options.customerUrl 客服页面路径。
   * @returns {Promise<Object>} uni.share 成功回调结果。
   */
  static openCustomerServiceChat(options) {
    this.requireText(options?.corpid, 'corpid')
    this.requireText(options?.customerUrl, 'customerUrl')
    return this.share({
      ...options,
      openCustomerServiceChat: true
    })
  }

  /**
   * 调用 uni-app 微信分享底层接口。
   *
   * @param {Object} options uni.share 参数。
   * @returns {Promise<Object>} uni.share 成功回调结果。
   */
  static share(options) {
    if (typeof uni === 'undefined' || typeof uni.share !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持 uni.share'))
    }
    const { success, fail, complete, ...shareOptions } = options || {}
    const scene = shareOptions.scene || WechatShareScene.SESSION
    return new Promise((resolve, reject) => {
      uni.share({
        ...shareOptions,
        provider: this.PROVIDER,
        scene,
        success: (result) => {
          success?.(result)
          resolve(result)
        },
        fail: (error) => {
          fail?.(error)
          reject(error)
        },
        complete: (result) => {
          complete?.(result)
        }
      })
    })
  }

  /**
   * 校验必填字符串参数。
   *
   * @param {String} value 参数值。
   * @param {String} name 参数名称。
   */
  static requireText(value, name) {
    if (typeof value !== 'string' || value.trim().length === 0) {
      throw new Error(`微信分享参数不能为空: ${name}`)
    }
  }
}
