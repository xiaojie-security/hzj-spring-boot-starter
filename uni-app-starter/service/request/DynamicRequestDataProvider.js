/**
 * 动态请求数据提供者。
 * <p>统一提供时间戳、随机串、设备 ID、认证令牌与刷新令牌等请求动态数据。</p>
 */
export default class DynamicRequestDataProvider {
  /** 设备 ID 本地存储键。 */
  static get DEVICE_ID_STORAGE_KEY() {
    return 'device-id'
  }

  /**
   * 注册认证数据提供者。
   * <p>提供者可以是接收请求上下文并返回数据的函数，也可以是实现 getRequestData 方法的对象。</p>
   *
   * @param {Function|Object} provider 认证数据提供者。
   */
  static setProvider(provider) {
    if (typeof provider !== 'function' && (provider === null || typeof provider?.getRequestData !== 'function')) {
      throw new Error('动态请求数据提供者必须是函数或包含 getRequestData 方法的对象')
    }
    this.provider = provider
  }

  /**
   * 清除认证数据提供者。
   */
  static clearProvider() {
    this.provider = undefined
  }

  /**
   * 获取当前请求的全部动态数据。
   *
   * @param {Object} context 请求上下文。
   * @returns {Promise<Object>} 动态请求数据。
   */
  static async getRequestData(context = {}) {
    const defaultData = {
      timestamp: String(Date.now()),
      nonce: this.createUuid(),
      deviceId: this.getDeviceId()
    }
    const providerData = await this.getProviderData(context)
    return {
      ...defaultData,
      ...this.removeUndefinedValues(providerData)
    }
  }

  /**
   * 获取持久化设备 ID；首次调用时自动生成 UUID。
   *
   * @returns {String} 设备 ID。
   */
  static getDeviceId() {
    const cachedDeviceId = this.deviceId
    if (this.isText(cachedDeviceId)) {
      return cachedDeviceId
    }
    const storageDeviceId = this.getStorageValue(this.DEVICE_ID_STORAGE_KEY)
    if (this.isText(storageDeviceId)) {
      this.deviceId = storageDeviceId
      return storageDeviceId
    }
    const deviceId = this.createUuid()
    this.deviceId = deviceId
    this.setStorageValue(this.DEVICE_ID_STORAGE_KEY, deviceId)
    return deviceId
  }

  /**
   * 生成随机 UUID。
   *
   * @returns {String} UUID 字符串。
   */
  static createUuid() {
    if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
      return crypto.randomUUID()
    }
    if (typeof crypto !== 'undefined' && typeof crypto.getRandomValues === 'function') {
      const values = crypto.getRandomValues(new Uint8Array(16))
      values[6] = (values[6] & 0x0f) | 0x40
      values[8] = (values[8] & 0x3f) | 0x80
      const hexadecimalValues = Array.from(values, (value) => value.toString(16).padStart(2, '0'))
      return `${hexadecimalValues.slice(0, 4).join('')}-${hexadecimalValues.slice(4, 6).join('')}-${hexadecimalValues.slice(6, 8).join('')}-${hexadecimalValues.slice(8, 10).join('')}-${hexadecimalValues.slice(10, 16).join('')}`
    }
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (character) => {
      const randomValue = Math.floor(Math.random() * 16)
      const value = character === 'x' ? randomValue : (randomValue & 0x03) | 0x08
      return value.toString(16)
    })
  }

  /**
   * 获取注册提供者返回的认证数据。
   *
   * @param {Object} context 请求上下文。
   * @returns {Promise<Object>} 提供者返回的数据。
   */
  static async getProviderData(context) {
    if (!this.provider) {
      return {}
    }
    const data = typeof this.provider === 'function'
      ? await this.provider(context)
      : await this.provider.getRequestData(context)
    if (data === undefined || data === null) {
      return {}
    }
    if (typeof data !== 'object' || Array.isArray(data)) {
      throw new Error('动态请求数据提供者必须返回对象')
    }
    return data
  }

  /**
   * 读取本地存储值。
   *
   * @param {String} key 存储键。
   * @returns {*} 存储值。
   */
  static getStorageValue(key) {
    if (typeof uni === 'undefined' || typeof uni.getStorageSync !== 'function') {
      return undefined
    }
    try {
      return uni.getStorageSync(key)
    } catch (error) {
      return undefined
    }
  }

  /**
   * 写入本地存储值。
   *
   * @param {String} key 存储键。
   * @param {*} value 存储值。
   */
  static setStorageValue(key, value) {
    if (typeof uni === 'undefined' || typeof uni.setStorageSync !== 'function') {
      return
    }
    try {
      uni.setStorageSync(key, value)
    } catch (error) {
      // 本地存储不可用时，本次运行周期内仍会使用内存设备 ID。
    }
  }

  /**
   * 移除值为 undefined 的属性，避免覆盖默认动态数据。
   *
   * @param {Object} data 原始数据。
   * @returns {Object} 已过滤数据。
   */
  static removeUndefinedValues(data) {
    return Object.entries(data).reduce((result, [key, value]) => {
      if (value !== undefined) {
        result[key] = value
      }
      return result
    }, {})
  }

  /**
   * 判断值是否为非空字符串。
   *
   * @param {*} value 待判断值。
   * @returns {Boolean} 是否为非空字符串。
   */
  static isText(value) {
    return typeof value === 'string' && value.trim().length > 0
  }
}
