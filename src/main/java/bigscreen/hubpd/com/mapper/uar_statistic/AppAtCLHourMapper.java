package bigscreen.hubpd.com.mapper.uar_statistic;

import bigscreen.hubpd.com.bean.uar_statistic.AppAtCLHour;
import bigscreen.hubpd.com.bean.uar_statistic.AppAtCLHourKey;
import bigscreen.hubpd.com.dto.COAppColumnRankDTO;

import java.util.List;
import java.util.Map;

public interface AppAtCLHourMapper {
    int deleteByPrimaryKey(AppAtCLHourKey key);

    int insert(AppAtCLHour record);

    int insertSelective(AppAtCLHour record);

    AppAtCLHour selectByPrimaryKey(AppAtCLHourKey key);

    int updateByPrimaryKeySelective(AppAtCLHour record);

    int updateByPrimaryKey(AppAtCLHour record);

    /**
     * 获取指定应用集合的栏目分组的pv数
     * @param paramMap
     * @return
     */
    public List<COAppColumnRankDTO> getPVByAppKeyAndCloumn(Map<String, Object> paramMap);
}