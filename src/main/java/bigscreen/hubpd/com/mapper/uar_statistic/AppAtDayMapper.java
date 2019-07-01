package bigscreen.hubpd.com.mapper.uar_statistic;

import bigscreen.hubpd.com.bean.uar_statistic.AppAtDay;
import bigscreen.hubpd.com.bean.uar_statistic.AppAtDayKey;
import bigscreen.hubpd.com.dto.OAAppChatLineDTO;

import java.util.List;
import java.util.Map;

public interface AppAtDayMapper {
    int deleteByPrimaryKey(AppAtDayKey key);

    int insert(AppAtDay record);

    int insertSelective(AppAtDay record);

    AppAtDay selectByPrimaryKey(AppAtDayKey key);

    int updateByPrimaryKeySelective(AppAtDay record);

    int updateByPrimaryKey(AppAtDay record);

    /**
     * 根据appkey以及时间，获取指定应用集合的pv和uv数
     * @param paramMap
     * @return
     */
    public List<OAAppChatLineDTO> selectPVAndUVByAppKeySetAndDuringDate(Map<String, Object> paramMap);
}