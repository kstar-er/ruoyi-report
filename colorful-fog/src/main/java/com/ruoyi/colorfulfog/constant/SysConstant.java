package com.ruoyi.colorfulfog.constant;

/**
 * 存放系统常量。
 */
public interface SysConstant {


    /**
     * 存放缓存的名字
     */
    interface CacheName {
        String TOKEN_CACHE = "token_cache";

        //手机验证码
        String SMS_CODE = "sms_code";
    }

    /**
     * Mysql的key-value表的key集合。
     * 需要提前将key放入数据库中。
     */
    interface KeySet {
        /**
         * 生成编码相关的key
         */
        String COST_BILL_NUM  = "COST_BILL_NUM";
        String RESULT_FIELD_NUM = "RESULT_FIELD_NUM";
        String DEPEND_CODE_NUM   = "DEPEND_CODE_NUM";
        String SCHEME_CODE_NUM   = "SCHEME_CODE_NUM";

        String COLLECT_RESULT_CODE_NUM = "COLLECT_RESULT_CODE_NUM";
        String COLLECT_DATA_CODE_NUM = "COLLECT_DATA_CODE_NUM";
        String COLLECT_SCHEME_CODE_NUM = "COLLECT_SCHEME_CODE_NUM";
        String RESULT_BATCH_CODE_NUM = "RESULT_BATCH_CODE_NUM";
    }


    interface Time {
        /**
         *  时间戳
         */
        long DAY_TIME_STAMP= 86399999;
    }
    interface SPECIAL_VALUE{
        String OTHER = "其他！";
        String VALUE_EQUAL_KEY = "$key";
    }


}
