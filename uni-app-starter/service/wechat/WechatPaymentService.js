/**
 * 微信虚拟支付类型。
 */
export const WechatVirtualPaymentMode = Object.freeze({
  /** 短剧代币充值。 */
  SHORT_SERIES_COIN: 'short_series_coin',
  /** 短剧道具直购。 */
  SHORT_SERIES_GOODS: 'short_series_goods'
})

/**
 * uni-app 微信支付服务。
 * <p>普通支付支持 App 和微信小程序；虚拟支付仅支持微信小程序；商家转账确认收款需要安装
 * uni-requestMerchantTransfer 扩展插件。</p>
 */
export default class WechatPaymentService {
  /** App 端微信支付服务提供商。 */
  static get PROVIDER() {
    return 'wxpay'
  }

  /** 微信虚拟支付人民币币种。 */
  static get VIRTUAL_PAYMENT_CURRENCY() {
    return 'CNY'
  }

  /** 微信 App 支付固定 package 值。 */
  static get APP_PAYMENT_PACKAGE() {
    return 'Sign=WXPay'
  }

  /**
   * 发起微信 App 支付。
   * <p>支付参数必须由服务端根据 App 预支付订单签名后提供。支付回调成功仅表示客户端已完成支付，
   * 订单最终状态仍应以服务端支付回调或主动查单结果为准。</p>
   *
   * @param {Object} options 支付参数。
   * @param {String} options.appid 微信开放平台移动应用 AppID。
   * @param {String} options.noncestr 随机字符串。
   * @param {String} options.partnerid 微信支付商户号。
   * @param {String} options.prepayid 服务端预支付订单号。
   * @param {String|Number} options.timestamp 秒级时间戳。
   * @param {String} options.sign 服务端生成的支付签名。
   * @param {String} [options.package] 固定值 Sign=WXPay。
   * @param {Function} [options.success] 成功回调。
   * @param {Function} [options.fail] 失败回调。
   * @param {Function} [options.complete] 完成回调。
   * @returns {Promise<Object>} uni.requestPayment 成功回调结果。
   */
  static payApp(options) {
    this.requireObject(options, 'options')
    const {
      appid,
      noncestr,
      partnerid,
      prepayid,
      timestamp,
      sign,
      package: packageValue = this.APP_PAYMENT_PACKAGE,
      success,
      fail,
      complete
    } = options
    this.requireText(appid, 'appid')
    this.requireText(noncestr, 'noncestr')
    this.requireText(partnerid, 'partnerid')
    this.requireText(prepayid, 'prepayid')
    this.requireText(sign, 'sign')
    this.requireTimestamp(timestamp, 'timestamp')
    this.requireText(packageValue, 'package')
    return this.requestPayment({
      orderInfo: {
        appid,
        noncestr,
        package: packageValue,
        partnerid,
        prepayid,
        timestamp,
        sign
      },
      success,
      fail,
      complete
    })
  }

  /**
   * 发起微信小程序支付。
   *
   * @param {Object} options 支付参数。
   * @param {String} options.timeStamp 秒级时间戳。
   * @param {String} options.nonceStr 随机字符串。
   * @param {String} options.package 预支付标识，格式为 prepay_id=xxx。
   * @param {String} options.signType 签名算法，例如 RSA。
   * @param {String} options.paySign 服务端生成的支付签名。
   * @param {Function} [options.success] 成功回调。
   * @param {Function} [options.fail] 失败回调。
   * @param {Function} [options.complete] 完成回调。
   * @returns {Promise<Object>} uni.requestPayment 成功回调结果。
   */
  static payMiniProgram(options) {
    this.requireObject(options, 'options')
    const { timeStamp, nonceStr, package: packageValue, signType, paySign, success, fail, complete } = options
    this.requireText(timeStamp, 'timeStamp')
    this.requireText(nonceStr, 'nonceStr')
    this.requireText(packageValue, 'package')
    this.requireText(signType, 'signType')
    this.requireText(paySign, 'paySign')
    return this.requestPayment({
      timeStamp,
      nonceStr,
      package: packageValue,
      signType,
      paySign,
      success,
      fail,
      complete
    })
  }

  /**
   * 发起短剧虚拟代币充值。
   *
   * @param {Object} options 支付参数。
   * @param {String} options.offerId 米大师申请的应用 ID。
   * @param {Number} options.buyQuantity 购买数量。
   * @param {String} options.outTradeNo 业务订单号。
   * @param {String} options.attach 透传数据。
   * @param {String} options.paySig 服务端生成的支付签名。
   * @param {String} options.signature 服务端生成的用户态签名。
   * @param {Number} [options.env=0] 米大师环境，0 为正式环境，1 为沙箱环境。
   * @param {Function} [options.success] 成功回调。
   * @param {Function} [options.fail] 失败回调。
   * @param {Function} [options.complete] 完成回调。
   * @returns {Promise<Object>} uni.requestVirtualPayment 成功回调结果。
   */
  static purchaseVirtualCoins(options) {
    return this.requestVirtualPayment({
      ...options,
      mode: WechatVirtualPaymentMode.SHORT_SERIES_COIN
    })
  }

  /**
   * 发起短剧虚拟道具直购。
   *
   * @param {Object} options 支付参数。
   * @param {String} options.offerId 米大师申请的应用 ID。
   * @param {Number} options.buyQuantity 购买数量。
   * @param {String} options.outTradeNo 业务订单号。
   * @param {String} options.attach 透传数据。
   * @param {String} options.productId 道具 ID。
   * @param {String|Number} options.goodsPrice 道具单价，单位为分。
   * @param {String} options.paySig 服务端生成的支付签名。
   * @param {String} options.signature 服务端生成的用户态签名。
   * @param {Number} [options.env=0] 米大师环境，0 为正式环境，1 为沙箱环境。
   * @param {Function} [options.success] 成功回调。
   * @param {Function} [options.fail] 失败回调。
   * @param {Function} [options.complete] 完成回调。
   * @returns {Promise<Object>} uni.requestVirtualPayment 成功回调结果。
   */
  static purchaseVirtualGoods(options) {
    return this.requestVirtualPayment({
      ...options,
      mode: WechatVirtualPaymentMode.SHORT_SERIES_GOODS
    })
  }

  /**
   * 拉起微信商家转账确认收款页面。
   * <p>可直接使用服务端 {@code TransferToUserResponse} 的 mchId、packageInfo、appid 与 openid 字段。
   * 服务商模式须补充 subAppId 和 subMchId。</p>
   *
   * @param {Object} options 确认收款参数。
   * @param {String} options.mchId 微信支付商户号。
   * @param {String} options.packageInfo 服务端转账受理结果中的 packageInfo。
   * @param {String} [options.appId] 商户 AppID。
   * @param {String} [options.openId] 收款用户 服务商模式下的子商户OpenID。
   * @param {String} [options.subAppId]  AppID。
   * @param {String} [options.subMchId] 服务商模式下的子商户号。
   * @param {Function} [options.success] 成功回调。
   * @param {Function} [options.fail] 失败回调。
   * @param {Function} [options.complete] 完成回调。
   * @returns {Promise<Object>} uni.requestMerchantTransfer 成功回调结果。
   */
  static confirmMerchantTransfer(options) {
    this.requireObject(options, 'options')
    const {
      mchId,
      packageInfo,
      appId = options.appid,
      openId = options.openid,
      subAppId,
      subMchId,
      success,
      fail,
      complete
    } = options
    this.requireText(mchId, 'mchId')
    this.requireText(packageInfo, 'packageInfo')
    if (!appId && !subAppId) {
      throw new Error('微信商家转账参数 appId 与 subAppId 至少需要提供一个')
    }
    if (subAppId) {
      this.requireText(subMchId, 'subMchId')
    }
    return this.invoke('uni.requestMerchantTransfer', (requestOptions) => uni.requestMerchantTransfer(requestOptions), {
      mchId,
      package: packageInfo,
      appId,
      openId,
      subAppId,
      subMchId,
      success,
      fail,
      complete
    })
  }

  /**
   * 调用虚拟支付 API。
   *
   * @param {Object} options 虚拟支付参数。
   * @returns {Promise<Object>} 支付结果。
   */
  static requestVirtualPayment(options) {
    this.requireObject(options, 'options')
    const {
      offerId,
      buyQuantity,
      outTradeNo,
      attach,
      productId,
      goodsPrice,
      paySig,
      signature,
      env = 0,
      mode,
      success,
      fail,
      complete
    } = options
    this.requireText(offerId, 'offerId')
    this.requirePositiveNumber(buyQuantity, 'buyQuantity')
    this.requireText(outTradeNo, 'outTradeNo')
    this.requireText(attach, 'attach')
    this.requireText(paySig, 'paySig')
    this.requireText(signature, 'signature')
    this.requireVirtualPaymentMode(mode)
    this.requireVirtualPaymentEnvironment(env)
    const signData = {
      offerId,
      buyQuantity,
      env,
      currencyType: this.VIRTUAL_PAYMENT_CURRENCY,
      outTradeNo,
      attach
    }
    if (mode === WechatVirtualPaymentMode.SHORT_SERIES_GOODS) {
      this.requireText(productId, 'productId')
      this.requirePositiveNumber(goodsPrice, 'goodsPrice')
      signData.productId = productId
      signData.goodsPrice = goodsPrice
    }
    return this.invoke('uni.requestVirtualPayment', (requestOptions) => uni.requestVirtualPayment(requestOptions), {
      signData,
      mode,
      paySig,
      signature,
      success,
      fail,
      complete
    })
  }

  /**
   * 调用普通微信支付 API。
   *
   * @param {Object} options 支付参数。
   * @returns {Promise<Object>} 支付结果。
   */
  static requestPayment(options) {
    const { success, fail, complete, ...paymentOptions } = options || {}
    return this.invoke('uni.requestPayment', (requestOptions) => uni.requestPayment(requestOptions), {
      ...paymentOptions,
      provider: this.PROVIDER,
      success,
      fail,
      complete
    })
  }

  /**
   * 以 Promise 方式调用 uni-app API，并透传可选回调。
   *
   * @param {String} apiName API 名称。
   * @param {Function} api uni-app API。
   * @param {Object} options API 参数。
   * @returns {Promise<Object>} API 成功回调结果。
   */
  static invoke(apiName, api, options) {
    if (typeof uni === 'undefined' || typeof api !== 'function') {
      return Promise.reject(new Error(`当前运行环境不支持 ${apiName}`))
    }
    const { success, fail, complete, ...apiOptions } = options || {}
    return new Promise((resolve, reject) => {
      api({
        ...apiOptions,
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
   * 校验对象参数。
   *
   * @param {Object} value 参数值。
   * @param {String} name 参数名称。
   */
  static requireObject(value, name) {
    if (value === null || typeof value !== 'object' || Array.isArray(value)) {
      throw new Error(`微信支付参数必须为对象: ${name}`)
    }
  }

  /**
   * 校验必填字符串参数。
   *
   * @param {String} value 参数值。
   * @param {String} name 参数名称。
   */
  static requireText(value, name) {
    if (typeof value !== 'string' || value.trim().length === 0) {
      throw new Error(`微信支付参数不能为空: ${name}`)
    }
  }

  /**
   * 校验正数参数。
   *
   * @param {Number|String} value 参数值。
   * @param {String} name 参数名称。
   */
  static requirePositiveNumber(value, name) {
    const numberValue = Number(value)
    if ((typeof value !== 'number' && typeof value !== 'string') || !Number.isFinite(numberValue) || numberValue <= 0) {
      throw new Error(`微信支付参数必须为正数: ${name}`)
    }
  }

  /**
   * 校验 App 支付时间戳。
   *
   * @param {Number|String} value 时间戳。
   * @param {String} name 参数名称。
   */
  static requireTimestamp(value, name) {
    const numberValue = Number(value)
    if ((typeof value !== 'number' && typeof value !== 'string') || !Number.isFinite(numberValue) || numberValue <= 0) {
      throw new Error(`微信支付参数不能为空: ${name}`)
    }
  }

  /**
   * 校验虚拟支付类型。
   *
   * @param {String} value 支付类型。
   */
  static requireVirtualPaymentMode(value) {
    if (!Object.values(WechatVirtualPaymentMode).includes(value)) {
      throw new Error(`不支持的微信虚拟支付类型: ${value}`)
    }
  }

  /**
   * 校验米大师环境。
   *
   * @param {Number} value 环境标识。
   */
  static requireVirtualPaymentEnvironment(value) {
    if (value !== 0 && value !== 1) {
      throw new Error(`微信虚拟支付环境只能为 0 或 1: ${value}`)
    }
  }
}
