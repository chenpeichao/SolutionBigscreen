package bigscreen.hubpd.com.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 常量工具类
 * Created by ceek on 2018-08-09 22:45.
 */
@Component
public class Constants {
    /** 请求参数周期标识 */
    /** 天：1 */
    public static Integer PARAM_PERIOD_DAY;
    /** 周：2 */
    public static Integer PARAM_PERIOD_WEEK;
    /** 月：3 */
    public static Integer PARAM_PERIOD_MONTH;
    /** 所有时间维度标识1:天、2：周、3：月 */
    public static String PARAM_TIME_PERIOD_ALL;
    /** 所有app应用标识1：网站、2：客户端、3：微信 */
    public static String PARAM_APP_FLAG_ALL;


    /** 默认系统标识(甘肃为2) */
    public static Integer SYS_TYPE_DEFAULT;


    /** 参数topN的默认大小为5 */
    public static String PARAM_TOPN_DEFAULT_NUM;
    /** 参数topN的最大值为30 */
    public static String PARAM_TOPN_MAX_NUM;


    @Value("${param_period_day}")
    public void setParamPeriodDay(Integer paramPeriodDay) {
        this.PARAM_PERIOD_DAY = paramPeriodDay;
    }
    @Value("${param_period_week}")
    public void setParamPeriodWeek(Integer paramPeriodWeek) {
        this.PARAM_PERIOD_WEEK = paramPeriodWeek;
    }
    @Value("${param_period_month}")
    public void setParamPeriodMonth(Integer paramPeriodMonth) {
        this.PARAM_PERIOD_MONTH = paramPeriodMonth;
    }
    @Value("${param_time_period_all}")
    public void setParamTimePeriodAll(String paramTimePeriodAll) {
        this.PARAM_TIME_PERIOD_ALL = paramTimePeriodAll;
    }
    @Value("${param_app_flag_all}")
    public void setParamAppFlagAll(String paramAppFlagAll) {
        this.PARAM_APP_FLAG_ALL = paramAppFlagAll;
    }
    @Value("${sys_type_default}")
    public void setSysTypeDefault(Integer sysTypeDefault) {
        this.SYS_TYPE_DEFAULT = sysTypeDefault;
    }
    @Value("${param_topN_default_num}")
    public void setParamTopNDefaultNum(String paramTopNDefaultNum) {
        this.PARAM_TOPN_DEFAULT_NUM = paramTopNDefaultNum;
    }
    @Value("${param_topN_max_num}")
    public void setParamTopNMaxNum(String paramTopNMaxNum) {
        this.PARAM_TOPN_MAX_NUM = paramTopNMaxNum;
    }
}
