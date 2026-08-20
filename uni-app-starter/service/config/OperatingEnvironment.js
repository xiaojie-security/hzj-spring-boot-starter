/**
 * uni-app 运行环境。
 * <p>每个属性均以中文环境名称作为键、环境编码作为值。</p>
 */
const OperatingEnvironment = Object.freeze({
  /** 移动应用。 */
  APP: 'APP',
  /** H5。 */
  H5: 'H5',
  /** 微信小程序。 */
  WECHAT_MINI_PROGRAM: 'wx-mini-program',
  /** 支付宝小程序。 */
  ALIPAY_MINI_PROGRAM: 'alipay-mini-program',
  /** 百度小程序。 */
  BAIDU_MINI_PROGRAM: 'baidu-mini-program',
  /** 抖音小程序。 */
  TOUTIAO_MINI_PROGRAM: 'toutiao-mini-program',
  /** 飞书小程序。 */
  FEISHU_MINI_PROGRAM: 'feishu-mini-program',
  /** QQ 小程序。 */
  QQ_MINI_PROGRAM: 'qq-mini-program',
  /** 快手小程序。 */
  KUAISHOU_MINI_PROGRAM: 'ks-mini-program',
  /** 京东小程序。 */
  JD_MINI_PROGRAM: 'jd-mini-program',
  /** HarmonyOS 元服务。 */
  HARMONYOS_META_SERVICE: 'harmonyos-meta-service',
  /** 小红书小程序。 */
  XIAOHONGSHU_MINI_PROGRAM: 'xhs-mini-program'
})

const environmentNameMapping = Object.freeze({
  [OperatingEnvironment.APP]: '移动应用',
  [OperatingEnvironment.H5]: 'H5',
  [OperatingEnvironment.WECHAT_MINI_PROGRAM]: '微信小程序',
  [OperatingEnvironment.ALIPAY_MINI_PROGRAM]: '支付宝小程序',
  [OperatingEnvironment.BAIDU_MINI_PROGRAM]: '百度小程序',
  [OperatingEnvironment.TOUTIAO_MINI_PROGRAM]: '抖音小程序',
  [OperatingEnvironment.FEISHU_MINI_PROGRAM]: '飞书小程序',
  [OperatingEnvironment.QQ_MINI_PROGRAM]: 'QQ小程序',
  [OperatingEnvironment.KUAISHOU_MINI_PROGRAM]: '快手小程序',
  [OperatingEnvironment.JD_MINI_PROGRAM]: '京东小程序',
  [OperatingEnvironment.HARMONYOS_META_SERVICE]: '元服务',
  [OperatingEnvironment.XIAOHONGSHU_MINI_PROGRAM]: '小红书小程序'
})

const environmentCodeMapping = Object.freeze(
  Object.entries(environmentNameMapping).reduce((result, [code, name]) => {
    result[name] = code
    return result
  }, {})
)

/**
 * 根据中文名称获取运行环境编码。
 *
 * @param {String} name 中文环境名称。
 * @returns {String|undefined} 环境编码。
 */
export function getOperatingEnvironmentCode(name) {
  return typeof name === 'string' ? environmentCodeMapping[name.trim()] : undefined
}

/**
 * 根据环境编码获取中文环境名称。
 *
 * @param {String} code 环境编码。
 * @returns {String|undefined} 中文环境名称。
 */
export function getOperatingEnvironmentName(code) {
  return typeof code === 'string' ? environmentNameMapping[code.trim()] : undefined
}

/**
 * 判断运行环境编码是否合法。
 *
 * @param {String} code 环境编码。
 * @returns {Boolean} 是否合法。
 */
export function isValidOperatingEnvironment(code) {
  return getOperatingEnvironmentName(code) !== undefined
}

/**
 * 判断中文运行环境名称是否合法。
 *
 * @param {String} name 中文环境名称。
 * @returns {Boolean} 是否合法。
 */
export function isValidOperatingEnvironmentName(name) {
  return getOperatingEnvironmentCode(name) !== undefined
}

/**
 * 运行环境编码与中文名称的双向映射。
 */
export const OperatingEnvironmentMapping = Object.freeze({
  codeToName: environmentNameMapping,
  nameToCode: environmentCodeMapping
})

export default OperatingEnvironment
