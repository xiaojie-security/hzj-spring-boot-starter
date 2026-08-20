import ConfigService from '../config/ConfigService.js'
import OperatingEnvironment from '../config/OperatingEnvironment.js'

/**
 * uni-app 授权范围。
 */
export const AuthorizeScope = Object.freeze({
  /** 用户信息。 */
  USER_INFO: 'scope.userInfo',
  /** 地理位置。 */
  USER_LOCATION: 'scope.userLocation',
  /** 后台地理位置。 */
  USER_LOCATION_BACKGROUND: 'scope.userLocationBackground',
  /** 通信地址。 */
  ADDRESS: 'scope.address',
  /** 录音。 */
  RECORD: 'scope.record',
  /** 保存到相册。 */
  WRITE_PHOTOS_ALBUM: 'scope.writePhotosAlbum',
  /** 摄像头。 */
  CAMERA: 'scope.camera',
  /** 获取发票。 */
  INVOICE: 'scope.invoice',
  /** 发票抬头。 */
  INVOICE_TITLE: 'scope.invoiceTitle',
  /** 微信运动步数。 */
  WERUN: 'scope.werun'
})

/**
 * uni-app 用户授权服务。
 * <p>仅封装 uni.authorize。APP 与 H5 不支持该 API，调用时会直接抛出异常。</p>
 */
export default class AuthorizeService {
  /**
   * 向用户发起授权请求。
   *
   * @param {String} scope 授权范围，使用 AuthorizeScope 常量或平台支持的 scope 值。
   * @param {Object} [callbacks] 可选回调。
   * @param {Function} [callbacks.success] 授权成功回调。
   * @param {Function} [callbacks.fail] 授权失败回调。
   * @param {Function} [callbacks.complete] 授权完成回调。
   * @returns {Promise<Object>} uni.authorize 成功回调结果。
   */
  static authorize(scope, callbacks = {}) {
    this.requireText(scope, 'scope')
    this.requireObject(callbacks, 'callbacks')
    this.requireSupportedOperatingEnvironment()
    if (typeof uni === 'undefined' || typeof uni.authorize !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持 uni.authorize'))
    }
    const { success, fail, complete } = callbacks
    return new Promise((resolve, reject) => {
      uni.authorize({
        scope,
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
   * 判断当前运行环境是否支持 uni.authorize。
   *
   * @returns {Boolean} 当前运行环境是否支持授权 API。
   */
  static isSupportedOperatingEnvironment() {
    if (!ConfigService.isOperatingEnvironmentValid()) {
      return false
    }
    const operatingEnvironment = ConfigService.getOperatingEnvironment()
    return operatingEnvironment !== OperatingEnvironment.APP && operatingEnvironment !== OperatingEnvironment.H5
  }

  /**
   * 校验当前运行环境支持 uni.authorize。
   */
  static requireSupportedOperatingEnvironment() {
    const operatingEnvironment = ConfigService.requireOperatingEnvironment()
    if (operatingEnvironment === OperatingEnvironment.APP || operatingEnvironment === OperatingEnvironment.H5) {
      throw new Error(`当前运行环境不支持 uni.authorize: ${operatingEnvironment}`)
    }
  }

  /**
   * 校验对象参数。
   *
   * @param {Object} value 参数值。
   * @param {String} name 参数名称。
   */
  static requireObject(value, name) {
    if (value === null || typeof value !== 'object' || Array.isArray(value)) {
      throw new Error(`授权参数必须为对象: ${name}`)
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
      throw new Error(`授权参数不能为空: ${name}`)
    }
  }
}
