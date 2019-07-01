package bigscreen.hubpd.com.service;

import java.util.Map;

/**
 * 内容分析
 *
 * @author cpc
 * @create 2019-04-09 15:49
 **/
public interface ContentAnalysisService {

    /**
     * 根据机构id以及系统标识获取指定机构的天、周、月的频道排行榜topN数据
     * @param lesseeId
     * @param sysType
     * @param timePeriodFlag
     * @param topNum
     * @return
     */
    public Map<String, Object> getChannelRank(String lesseeId, Integer sysType, Integer timePeriodFlag, Integer topNum);

    /**
     * 根据机构id以及系统标识获取指定机构的天、周、月的热门内容排行榜topN数据
     * @param lesseeId
     * @param sysType
     * @param timePeriodFlag
     * @param topNum
     * @return
     */
    public Map<String, Object> getHotRank(String lesseeId, Integer sysType, Integer timePeriodFlag, Integer topNum);
}
