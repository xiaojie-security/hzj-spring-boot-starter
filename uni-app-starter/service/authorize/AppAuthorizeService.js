import ConfigService from '../config/ConfigService.js'
import OperatingEnvironment from '../config/OperatingEnvironment.js'
import permission from './common/permission.js'

/**
 * App 原生权限状态。
 */
export const AppAuthorizeStatus = Object.freeze({
  /** 已授权。 */
  GRANTED: 'granted',
  /** 本次或普通拒绝。 */
  DENIED: 'denied',
  /** Android 永久拒绝。 */
  DENIED_ALWAYS: 'denied-always'
})

/**
 * App 常用权限定义。
 * <p>iOS 使用插件定义的权限标识，Android 使用系统权限常量。</p>
 */
export const AppAuthorizePermission = Object.freeze({
  /** 精确位置。 */
  LOCATION: Object.freeze({
    ios: 'location',
    android: 'android.permission.ACCESS_FINE_LOCATION'
  }),
  /** 模糊位置。 */
  COARSE_LOCATION: Object.freeze({
    android: 'android.permission.ACCESS_COARSE_LOCATION'
  }),
  /** 摄像头。 */
  CAMERA: Object.freeze({
    ios: 'camera',
    android: 'android.permission.CAMERA'
  }),
  /** 相册读取。 */
  PHOTO_LIBRARY: Object.freeze({
    ios: 'photoLibrary',
    android: 'android.permission.READ_EXTERNAL_STORAGE'
  }),
  /** 相册写入。 */
  WRITE_PHOTOS_ALBUM: Object.freeze({
    ios: 'photoLibrary',
    android: 'android.permission.WRITE_EXTERNAL_STORAGE'
  }),
  /** 麦克风。 */
  RECORD: Object.freeze({
    ios: 'record',
    android: 'android.permission.RECORD_AUDIO'
  }),
  /** 推送通知，仅 iOS 有对应的插件权限标识。 */
  PUSH: Object.freeze({
    ios: 'push'
  }),
  /** 通讯录读取。 */
  READ_CONTACTS: Object.freeze({
    ios: 'contact',
    android: 'android.permission.READ_CONTACTS'
  }),
  /** 通讯录写入。 */
  WRITE_CONTACTS: Object.freeze({
    ios: 'contact',
    android: 'android.permission.WRITE_CONTACTS'
  }),
  /** 日历读取。 */
  READ_CALENDAR: Object.freeze({
    ios: 'calendar',
    android: 'android.permission.READ_CALENDAR'
  }),
  /** 日历写入。 */
  WRITE_CALENDAR: Object.freeze({
    ios: 'calendar',
    android: 'android.permission.WRITE_CALENDAR'
  }),
  /** 备忘录，仅 iOS 支持。 */
  MEMO: Object.freeze({
    ios: 'memo'
  })
})

/**
 * uni-app App 原生授权服务。
 * <p>基于 DCloud wa-permission 插件封装，仅可在运行环境配置为 APP 的设备端调用。</p>
 */
export default class AppAuthorizeService {
  /**
   * 查询 iOS 原生权限是否已授权。
   *
   * @param {Object} appPermission AppAuthorizePermission 中的权限定义。
   * @returns {Boolean} 是否已授权。
   */
  static isGranted(appPermission) {
    this.requireAppEnvironment()
    this.requireIosPlatform()
    const permissionId = this.resolvePermissionId(appPermission, 'ios')
    return permission.judgeIosPermission(permissionId)
  }

  /**
   * 请求 App 原生权限。
   * <p>Android 会触发系统授权弹窗；iOS 原生权限需要在实际调用相机、定位等系统能力时触发，
   * 此方法仅返回当前授权状态。</p>
   *
   * @param {Object} appPermission AppAuthorizePermission 中的权限定义。
   * @returns {Promise<Object>} 统一权限结果。
   */
  static async request(appPermission) {
    this.requireAppEnvironment()
    const platform = this.getPlatform()
    if (platform === 'ios') {
      return this.createResult(this.isGranted(appPermission) ? AppAuthorizeStatus.GRANTED : AppAuthorizeStatus.DENIED, appPermission)
    }
    const permissionId = this.resolvePermissionId(appPermission, 'android')
    return this.requestAndroid(permissionId)
  }

  /**
   * 请求 Android 原生权限。
   *
   * @param {String} permissionId Android 系统权限常量。
   * @returns {Promise<Object>} 统一权限结果。
   */
  static async requestAndroid(permissionId) {
    this.requireAppEnvironment()
    this.requireAndroidPlatform()
    this.requireText(permissionId, 'permissionId')
    const result = await permission.requestAndroidPermission(permissionId)
    if (result === 1) {
      return this.createResult(AppAuthorizeStatus.GRANTED, { android: permissionId })
    }
    if (result === -1) {
      return this.createResult(AppAuthorizeStatus.DENIED_ALWAYS, { android: permissionId })
    }
    if (result === 0) {
      return this.createResult(AppAuthorizeStatus.DENIED, { android: permissionId })
    }
    const error = new Error(`Android 权限请求失败: ${permissionId}`)
    error.detail = result
    throw error
  }

  /**
   * 打开当前 App 的系统权限设置页。
   */
  static openPermissionSettings() {
    this.requireAppEnvironment()
    this.requirePlusRuntime()
    permission.gotoAppPermissionSetting()
  }

  /**
   * 查询系统定位服务是否开启。
   *
   * @returns {Boolean} 系统定位服务是否开启。
   */
  static isLocationServiceEnabled() {
    this.requireAppEnvironment()
    this.requirePlusRuntime()
    return permission.checkSystemEnableLocation()
  }

  /**
   * 判断当前是否为 iOS App。
   *
   * @returns {Boolean} 是否为 iOS App。
   */
  static isIos() {
    return this.getPlatform() === 'ios'
  }

  /**
   * 判断当前是否为 Android App。
   *
   * @returns {Boolean} 是否为 Android App。
   */
  static isAndroid() {
    return this.getPlatform() === 'android'
  }

  /**
   * 创建统一权限结果。
   *
   * @param {String} status 权限状态。
   * @param {Object} appPermission 权限定义。
   * @returns {Object} 权限结果。
   */
  static createResult(status, appPermission) {
    const platform = this.getPlatform()
    return {
      status,
      granted: status === AppAuthorizeStatus.GRANTED,
      deniedAlways: status === AppAuthorizeStatus.DENIED_ALWAYS,
      platform,
      permissionId: this.resolvePermissionId(appPermission, platform)
    }
  }

  /**
   * 从权限定义中获取当前平台的权限标识。
   *
   * @param {Object} appPermission 权限定义。
   * @param {String} platform 当前平台。
   * @returns {String} 权限标识。
   */
  static resolvePermissionId(appPermission, platform) {
    this.requireObject(appPermission, 'appPermission')
    const permissionId = appPermission[platform]
    if (typeof permissionId !== 'string' || permissionId.trim().length === 0) {
      throw new Error(`当前 ${platform} 平台不支持该权限`)
    }
    return permissionId
  }

  /**
   * 校验当前运行环境为 APP。
   */
  static requireAppEnvironment() {
    const operatingEnvironment = ConfigService.requireOperatingEnvironment()
    if (operatingEnvironment !== OperatingEnvironment.APP) {
      throw new Error(`App 原生授权服务仅支持 APP 运行环境: ${operatingEnvironment}`)
    }
  }

  /**
   * 获取当前 App 原生平台。
   *
   * @returns {String} ios 或 android。
   */
  static getPlatform() {
    this.requirePlusRuntime()
    const platformName = String(plus.os.name || '').toLowerCase()
    if (platformName === 'ios') {
      return 'ios'
    }
    if (platformName === 'android') {
      return 'android'
    }
    throw new Error(`不支持的 App 原生平台: ${plus.os.name}`)
  }

  /**
   * 校验 iOS 平台。
   */
  static requireIosPlatform() {
    if (this.getPlatform() !== 'ios') {
      throw new Error('当前 App 原生平台不是 iOS')
    }
  }

  /**
   * 校验 Android 平台。
   */
  static requireAndroidPlatform() {
    if (this.getPlatform() !== 'android') {
      throw new Error('当前 App 原生平台不是 Android')
    }
  }

  /**
   * 校验 5+ 原生运行时。
   */
  static requirePlusRuntime() {
    if (typeof plus === 'undefined' || !plus.os) {
      throw new Error('当前运行环境不支持 5+ 原生权限能力')
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
      throw new Error(`App 授权参数必须为对象: ${name}`)
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
      throw new Error(`App 授权参数不能为空: ${name}`)
    }
  }
}
