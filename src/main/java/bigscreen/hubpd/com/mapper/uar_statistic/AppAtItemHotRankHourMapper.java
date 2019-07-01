package bigscreen.hubpd.com.mapper.uar_statistic;

import bigscreen.hubpd.com.bean.uar_statistic.AppAtItemHotRankHour;
import bigscreen.hubpd.com.bean.uar_statistic.AppAtItemHotRankHourKey;
import bigscreen.hubpd.com.dto.COAppItemRankDTO;

import java.util.List;
import java.util.Map;

public interface AppAtItemHotRankHourMapper {
    int deleteByPrimaryKey(AppAtItemHotRankHourKey key);

    int insert(AppAtItemHotRankHour record);

    int insertSelective(AppAtItemHotRankHour record);

    AppAtItemHotRankHour selectByPrimaryKey(AppAtItemHotRankHourKey key);

    int updateByPrimaryKeySelective(AppAtItemHotRankHour record);

    int updateByPrimaryKeyWithBLOBs(AppAtItemHotRankHour record);

    int updateByPrimaryKey(AppAtItemHotRankHour record);

    /**
     * 获取指定应用集合的url求topn的pv数
     * @param paramMap
     * @return
     */
    public List<COAppItemRankDTO> getPVAndUrlAndTitleByAppKeyAndDate(Map<String, Object> paramMap);
}