package com.hzj.aliyun.provider.aliyun.ocr.enums;

/**
 * OCR 统一识别（RecognizeAllText）图片类型。
 *
 * <p>{@link #getCode()} 为阿里云 {@code Type} 参数取值，枚举名与取值一一对应，
 * 便于业务侧按语义引用，避免硬编码字符串。</p>
 */
public enum AliyunOcrType {

    /** 通用文字识别高精版。 */
    ADVANCED("Advanced"),

    /** 通用文字识别基础版。 */
    GENERAL("General"),

    /** 表格。 */
    TABLE("Table"),

    /** 多语言文字。 */
    MULTI_LANG("MultiLang"),

    /** 手写文字。 */
    HAND_WRITING("HandWriting"),

    /** 电商图片文字。 */
    COMMERCE("Commerce"),

    /** 身份证。 */
    ID_CARD("IdCard"),

    /** 银行卡。 */
    BANK_CARD("BankCard"),

    /** 中国护照。 */
    CHINESE_PASSPORT("ChinesePassport"),

    /** 国际护照。 */
    INTERNATIONAL_PASSPORT("InternationalPassport"),

    /** 国际身份证。 */
    INTERNATIONAL_ID_CARD("InternationalIdCard"),

    /** 社保卡。 */
    SOCIAL_SECURITY_CARD("SocialSecurityCard"),

    /** 中国香港身份证。 */
    HK_ID_CARD("HKIdCard"),

    /** 来往中国大陆（内地）通行证。 */
    PERMIT_TO_MAINLAND("PermitToMainland"),

    /** 往来港澳台通行证。 */
    PERMIT_TO_HK_MO_TW("PermitToHK_MO_TW"),

    /** 户口本首页。 */
    HOUSEHOLD_HEAD("HouseholdHead"),

    /** 户口本常住人口页。 */
    HOUSEHOLD_RESIDENT("HouseholdResident"),

    /** 出生证明。 */
    BIRTH_CERTIFICATION("BirthCertification"),

    /** 驾驶证。 */
    DRIVING_LICENSE("DrivingLicense"),

    /** 行驶证。 */
    VEHICLE_LICENSE("VehicleLicense"),

    /** 车辆合格证。 */
    VEHICLE_CERTIFICATION("VehicleCertification"),

    /** 机动车登记证。 */
    VEHICLE_REGISTRATION("VehicleRegistration"),

    /** 车牌。 */
    LICENSE_PLATE_NUMBER("LicensePlateNumber"),

    /** 车辆 vin 码。 */
    CAR_VIN_CODE("CarVinCode"),

    /** 营业执照。 */
    BUSINESS_LICENSE("BusinessLicense"),

    /** 国际企业执照。 */
    INTERNATIONAL_BUSINESS_LICENSE("InternationalBusinessLicense"),

    /** 商标注册证。 */
    TRADEMARK_CERTIFICATE("TrademarkCertificate"),

    /** 不动产权证。 */
    ESTATE_CERTIFICATION("EstateCertification"),

    /** 税收完税证明。 */
    TAX_CLEARANCE_CERTIFICATE("TaxClearanceCertificate"),

    /** 银行开户许可证。 */
    BANK_ACCOUNT_PERMIT("BankAccountPermit"),

    /** 食品生产许可证。 */
    FOOD_PRODUCE_LICENSE("FoodProduceLicense"),

    /** 食品经营许可证。 */
    FOOD_MANAGEMENT_LICENSE("FoodManagementLicense"),

    /** 化妆品生产许可证。 */
    COSMETIC_PRODUCE_LICENSE("CosmeticProduceLicense"),

    /** 医疗器械经营许可证。 */
    MEDICAL_DEVICE_MANAGE_LICENSE("MedicalDeviceManageLicense"),

    /** 医疗器械生产许可证。 */
    MEDICAL_DEVICE_PRODUCE_LICENSE("MedicalDeviceProduceLicense"),

    /** 第二类医疗器械经营备案凭证。 */
    CLASS_II_MEDICAL_DEVICE_MANAGE_LICENSE("ClassIIMedicalDeviceManageLicense"),

    /** 增值税发票。 */
    INVOICE("Invoice"),

    /** 增值税发票卷票。 */
    ROLL_TICKET("RollTicket"),

    /** 通用机打发票。 */
    COMMON_PRINTED_INVOICE("CommonPrintedInvoice"),

    /** 混贴票证。 */
    MIXED_INVOICE("MixedInvoice"),

    /** 定额发票。 */
    QUOTA_INVOICE("QuotaInvoice"),

    /** 非税收入发票。 */
    NON_TAX_INVOICE("NonTaxInvoice"),

    /** 机动车销售统一发票。 */
    CAR_INVOICE("CarInvoice"),

    /** 二手车销售统一发票。 */
    USED_CAR_INVOICE("UsedCarInvoice"),

    /** 出租车发票。 */
    TAXI_INVOICE("TaxiInvoice"),

    /** 过路过桥费发票。 */
    TOLL_INVOICE("TollInvoice"),

    /** 火车票。 */
    TRAIN_TICKET("TrainTicket"),

    /** 航空行程单。 */
    AIR_ITINERARY("AirItinerary"),

    /** 客运车船票。 */
    BUS_SHIP_TICKET("BusShipTicket"),

    /** 网约车行程单。 */
    RIDE_HAILING_ITINERARY("RideHailingItinerary"),

    /** 银行承兑汇票。 */
    BANK_ACCEPTANCE("BankAcceptance"),

    /** 酒店流水。 */
    HOTEL_CONSUME("HotelConsume"),

    /** 购物小票。 */
    SHOPPING_RECEIPT("ShoppingReceipt"),

    /** 支付详情页。 */
    PAYMENT_RECORD("PaymentRecord"),

    /** 电商订单页。 */
    PURCHASE_RECORD("PurchaseRecord"),

    /** 电子面单。 */
    WAY_BILL("WayBill"),

    /** 二维码。 */
    QR_CODE("QrCode"),

    /** 条形码。 */
    BAR_CODE("BarCode"),

    /** 公章。 */
    STAMP("Stamp");

    /** 阿里云 Type 参数取值。 */
    private final String code;

    /**
     * 创建图片类型。
     *
     * @param code 阿里云 Type 参数取值
     */
    AliyunOcrType(String code) {
        this.code = code;
    }

    /**
     * 获取阿里云 Type 参数取值。
     *
     * @return Type 参数取值
     */
    public String getCode() {
        return code;
    }

    /**
     * 根据阿里云 Type 参数取值解析枚举。
     *
     * @param code Type 参数取值
     * @return 图片类型；无法匹配时返回 {@code null}
     */
    public static AliyunOcrType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AliyunOcrType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
