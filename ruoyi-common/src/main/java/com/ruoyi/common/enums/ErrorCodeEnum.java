package com.ruoyi.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    /**
     * 通用状态
     */
    SUCCESS(200, "ok"),
    UNKNOWN_ERROR(404, "未知异常"),
    PARAMETER_ERROR(405, "参数错误"),
    VERIFY_ERROR(406,"验证失败"),
    TIMEOUT_ERROR(407,"验证超时"),
    IP_FREQUENT_VISITS(408,"请求失败,你访问太频繁了~"),
    INFO(409, "提示"),
    /**
     * 权限相关
     */
    LOGIN_FAIL(1001, "账号或密码错误"),
    NO_LOGIN(1002, "没有登录"),
    NO_DATA_RULE(1003, "没有数据权限"),
    NO_DRIVER(1004,"司机不存在"),
    PASSWORD_ERROR(1005,"密码错误"),
    DUPLICATE_ACCOUNT(1006,"账号重复"),
    RUNTIME_NO_USER(1007, "线程中没有用户信息（没有登录"),
    USER_NO_ORG(1008, "jwt用户信息中没有组织"),
    DATA_RULE_CONFIG_ERROR(1009, "数据规则配置错误"),
    ORGANIZATION_NOT_FOUND(1010,"组织不存在"),

    /**
     * 流程相关
     */
    PROCESS_STATUS_NO_FOUND(2001, "流程未定义"),
    PROCESS_STATUS_NO_RIGHT(20002, "流程状态不正确:"),
    PROCESS_STATUS_NO_AUTHORITY(20003,"该流程下不允许修改"),
    PROCESS_STATUS_HAVE_DONE(20004,"该状态已完成，不可以再设置"),
    PROCESS_STATUS_CAN_NOT_ROLL_BACK(2005, "无法回退"),
    PROCESS_STATUS_NO_REMOVE(2006,"该状态下不允许删除"),
    /**
     * 物料相关
     */
    MATERIAL_NO_FOUND(4001, "物料不存在.请先维护"),
    MATERIAL_NOT_SET(4002,"不是套机，没有结构"),
    DATA_ERROR(4003,"数据错误<br/>"),
    ONE_HEAD_DUP_MATERIAL(4004,"一个条码头多个物料的错误"),
    POSITION_DUO_MATERIAL(4005,"该仓位已经添加过相同的物料，数量为："),
    WAREHOUSE_IS_STOP_TAKE_STOCK(4006,"该仓库已停止库存盘点"),
    DUPLICATE_MATERIAL_CODE(4007,"物料代码重复"),
    ERROR_BAR_CODE_ENUM(4008,"错误的条码长度"),
    MATERIAL_NO_BAR_CODE_HEAD(4009,"物料没有条码头，请维护"),
    BAR_CODE_HEAD_NO_MATERIAL(4010,"该条码头没有对应的物料信息，请维护"),
    DUPLICATE_BAR_CODE_HEAD(4011,"该条码头已有对应物料"),
    PART_NUMBER_NO_ZERO(40012,"净数量不能为0"),
    BARCODE_HEAD_ERROR(40013,"条码头错误"),



    /**
     *  数据相关
     */
    DUPLICATE_VALUES(3001,"该值已存在，不可重复:"),


    /**
     *  物流订单相关
     *
     * */
    WAREHOUSE_NOT_EXIST(50001,"仓库不存在，请确认:"),
    WAREHOUSE_TYPE_NOT_ALLOW_ALLOCATION(50004,"仓库类型不允许调拨:"),
    MERCHANT_NOT_EXIST(50002,"商家不存在，请确认:"),
    DUPLICATE_PICKING_KEY(50003, "提货密钥已经存在，请勿重复导入,重复密钥:"),

    /**
     *  派车单相关
     *
     * */
    TRANSPORT_ORDER_CODE_NOT_EXIST(60001,"派车单编号不存在，请确认:"),
    TRANSPORT_ORDER_DETAIL_NOT_EXIST(60002,"派车单详情为空"),
    CARRIER_NOT_EXIST(60003,"车队不存在，请确认:"),

    /**
     *  网点相关
     *
     * */
    BRANCH_NOT_EXIST(70001,"网点不存在，请确认:"),
    ORDER_NO_BRANCH(70002,"订单还未设置网点"),
    BRANCH_ORDER_NOT_EXIST(70003,"无法找到销售订单"),
    BRANCH_TRANSPORT_ORDER_NOT_EXIST(70004,"网点派车单不存在"),
    REGION_NOT_HAVA_BRANCH(70005,"该地区未自动分配网点"),
    BRANCH_NOT_DIRECT_AND_NO_NOT_DIRECT(70006,"网点属性既不是直配也不是非直配"),
    DUPLICATE_BAR_CODE(70007,"条码重复,已打印:"),
    BRANCH_ORDER_HAVE_OUT_WAREHOUSE(70008, "网点订单已经出库"),
    BRANCH_ORDER_HAVE_AGG(70009, "网点物流订单已制作集配单"),
    BRANCH_TRACK_NO_FOUND(70010, "物流轨迹未找到"),

    /**
     * 库存相关
     */
    STOCK_INIT_FAIL(80001, "库存初始化失败"),
    NO_STOCK_RECORD(80002, "无库存记录"),
    STOCK_NO_ENOUGH(8003, "库存不足"),
    FROZEN_NUMBER_NO_ENOUGH(8004, "可以留料的数量不足"),
    POSITION_NO_BAR_CODE(8005, "仓位中没有该条码"),
    WAREHOUSE_BAR_CODE_DUP(8006, "仓库中条码重复"),
    BAR_CODE_DUP_IN_WAREHOUSE(8007, "条码重复入库"),
    BAR_CODE_IS_FROZEN(8008, "条码已经被冻结"),
    BAR_CODE_IS_NO_FROZEN(8008, "条码不在冻结状态"),
    BAR_CODE_DUP_SCAN(8007, "该条码已扫码："),
    MATERIAL_IN_WAREHOUSE_FINISH(8008,"该物料已完成入库，请勿超出入库数量入库"),
    MATERIAL_IS_NO_FROZEN(8009,"该物料没有冻结记录"),
    MATERIAL_IS_NO_FROZEN_MAX(8010,"该物料冻结记录不足"),
    MATERIAL_IS_NO_FROZEN_UNKNOWN(8011,"该物料解冻异常"),





    /**
     * 仓库、仓位档案相关
     */
    WAREHOUSE_POSITION_NOT_EXIST(90001,"仓位不存在"),
    MERCHANT_TO_WAREHOUSE_NO_EXIST(90002, "商家无对应的前置仓信息"),
    WAREHOUSE_POSITION_NOT_RECOMMEND(90003,"该物料不在推荐仓位中"),

    /**
     * 调拨相关
     */
    ALLOCATION_PLAN_EXISTED(100001, "调拨计划已经存在"),
    ALLOCATION_PLAN_NO_EXIST(100002, "调拨计划不存在"),
    ALLOCATION_PLAN_MATERIAL_NO_ENOUGH(100003, "调拨计划的物料数量不足"),
    ALLOCATION_ORDER_NOT_FOUND(100004,"调拨单不存在"),
    ALLOCATION_PLAN_PROCESS(100005, "该调拨单状态发生变化，请重新制作"),

    /**
     * 销售订单相关
     */
    SALE_ORDER_UNKNOWN_ERROR(110001,"未知异常"),
    SALE_ORDER_NO_FOUND(110002,"只能制作自己的发货订单"),
    SALE_ORDER_PROCESS(110003,"该发货状态发生变化，请重新制作"),
    SALE_ORDER_NO_EXIST(110004,"该网点销售订单不存在"),
    SALE_ORDER_EXIST(110005,"发货订单号已存在"),
    SALE_ORDER_RETURN_EXIST(110005,"该销售退货单已经存在"),
    SALE_ORDER_NOT_EXIST(110005,"该销售订单不存在"),
    SALE_ORDER_EXIST_OUT(110006,"该发货单已做过出库单"),

    /**
     * 库位调整相关
     */
    POSITION_RESET_ORG_POSITION_ERR(120001, "仓位调整时，原仓位错误"),


    /**
     * 出库单相关
     */
    OUT_WAREHOUSE_ORDER_ERROR(130001,"未知异常"),
    OUT_WAREHOUSE_ORDER_EXIST(130002, "出库单不存在"),
    OUT_WAREHOUSE_ORDER_NUM_MAX(130003, "该物料已出库完成"),
    OUT_WAREHOUSE_ORDER_NO_OUT(130004, "请检查条码头，是否是对应物料！"),
    OUT_WAREHOUSE_ORDER_NO_AUDIT(130005, "出库单未审核"),
    OUT_WAREHOUSE_ORDER_NUMBER(130006, "出库数量错误"),
    OUT_WAREHOUSE_ORDER_NOT_MERCHANT(13007,"只允许相同商家的发货单制作成一张出库单"),
    OUT_WAREHOUSE_ORDER_NOT_BARCODE(13008,"更换条码时出现错误"),


    /**
     * 入库单
     */

    IN_WAREHOUSE_ORDER_ERROR(140001,"未知异常"),
    IN_WAREHOUSE_ORDER_EXIST(140002, "入库单不存在"),
    IN_WAREHOUSE_ORDER_NUM_MAX(140003, "该物料入出库完成"),
    IN_WAREHOUSE_ORDER_FAIL(140004,"入库失败，原因:"),
    IN_WAREHOUSE_ORDER_NUMBER_TOO_MUCH(140005, "入库数量过多"),
    IN_WAREHOUSE_ORDER_MATERIAL_LACK_BARCODE_HEAD(140006,"入库单物料缺失条码头:"),
    /**
     * 提货订单相关
     */
    PICKING_ADD_ERR(140001, "新增时出现错误"),

    /**
     * 商家调拨单相关
     */
    MER_ALLOCATION_ERR_CREATE(150001,"制作商家调拨单出错"),
    MER_ALLOCATION_ERR(150002,"未知异常"),
    MER_ALLOCATION_EXIST(150003,"该商家调拨单已经存在"),
    MER_ALLOCATION_NOT_EXIST(150004,"该商家调拨单不存在"),


    /**
     * 盘点单相关
     */
    INVENTORY_ERR_CREATE(150001,"制作盘点表出错"),
    INVENTORY_ERR(150002,"未知异常"),

    /**
     * 是否推送成功
     */
    GJ_ERR_TOKEN(150003,"获取格匠TOKEN失败"),
    GJ_ERR_TS(150004,"推送到格匠失败"),

    /**
     * 账单相关错误
     */
    COST_LACK_MERCHANT_BELONG(160001,"商家缺少所属销售公司分类"),
    COST_LACK_MATERIAL_CLASSIFY(160002,"物料缺少分类"),
    COST_LACK_MATERIAL_PRICE(160003,"物料缺少开单价"),
    COST_LACK_RULE(160004,"缺少对应规则匹配"),
    COST_ORDER_TYPE_NOT_EXIST(160005,"订单类型不存在"),

    /**
     * 发货单相关
     */
    BIG_SEND_ORDER_NO_EXISTS(170001, "发货单不存在"),
    BIG_SEND_ORDER_NO_ADD(170002, "以下发货单导入发现异常"),


    ;

    private Integer code;
    private String msg;

    ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
