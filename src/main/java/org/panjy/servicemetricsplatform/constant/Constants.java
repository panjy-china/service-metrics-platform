package org.panjy.servicemetricsplatform.constant;

/**
 * 系统常量类
 * 用于存放系统中使用的各种常量
 */
public final class Constants {
    
    /**
     * 私有构造函数，防止实例化
     */
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
    
    /** 账户数量常量 */
    public static final int ACCOUNT_NUMBER = 31;
    
    /**
     * 时间格式常量
     */
    public static final class TimeFormat {
        /** 标准日期时间格式 */
        public static final String STANDARD_DATETIME = "yyyy-MM-dd HH:mm:ss";
        
        /** 标准日期格式 */
        public static final String STANDARD_DATE = "yyyy-MM-dd";
        
        /** 标准时间格式 */
        public static final String STANDARD_TIME = "HH:mm:ss";
        
        /** ISO日期时间格式 */
        public static final String ISO_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";
        
        /** 紧凑日期时间格式 */
        public static final String COMPACT_DATETIME = "yyyyMMddHHmmss";
        
        /** 默认时区 */
        public static final String DEFAULT_TIMEZONE = "Asia/Shanghai";
        
        /**
         * 私有构造函数，防止实例化
         */
        private TimeFormat() {
            throw new UnsupportedOperationException("TimeFormat class cannot be instantiated");
        }
    }
}
