package bigscreen.hubpd.com.service;

import java.util.Map;

/**
 * 运营分析service
 *
 * @author cpc
 * @create 2019-04-08 11:39
 **/
public interface OperationAnalysisService {
    /**
     * 获取指定系统的指定租户授权公众号的7天、7周、7月的阅读数和点赞数
     * @param lesseeId              租户id
     * @param sysType               系统类型
     * @param imePeriodFlag         时间周期
     * @return
     */
    public Map<String, Object> getWXSevenDWMReadAndLikeData(String lesseeId, Integer sysType, Integer imePeriodFlag);

    /**
     * 获取指定系统的指定租户授权网站的7天、7周、7月的pv和uv
     * @param lesseeId              租户id
     * @param sysType               系统类型
     * @param imePeriodFlag         时间周期
     * @return
     */
    public Map<String, Object> getWebSevenYMDPVAndUVData(String lesseeId, Integer sysType, Integer imePeriodFlag);

    /**
     * 获取指定系统的指定租户授权客户端的7天、7周、7月的pv和uv
     * @param lesseeId              租户id
     * @param sysType               系统类型
     * @param imePeriodFlag         时间周期
     * @return
     */
    public Map<String, Object> getAppSevenYMDPVAndUVData(String lesseeId, Integer sysType, Integer imePeriodFlag);

    /**
     * 获取网站、客户端、微博、微信topN文章
     * @param lesseeIdStr           租户id
     * @param sysType               系统标识；默认为2（甘肃项目）
     * @param timePeriodFlag        时间维度(1：天；2：周；3：月)
     * @param appFlag               应用标识（1：表示网站；2：表示客户端；3：表示微信；4：表示微博）
     * @param topNum                获取前topN
     * @return
     */
    public Map<String, Object> getTopNArticleInAppFlag(String lesseeIdStr, Integer sysType, Integer timePeriodFlag, Integer appFlag, Integer topNum);
}
