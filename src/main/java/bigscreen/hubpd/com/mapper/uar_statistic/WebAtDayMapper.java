package bigscreen.hubpd.com.mapper.uar_statistic;

import bigscreen.hubpd.com.bean.uar_statistic.WebAtDay;
import bigscreen.hubpd.com.bean.uar_statistic.WebAtDayKey;
import bigscreen.hubpd.com.dto.OAWebChatLineDTO;

import java.util.List;
import java.util.Map;

public interface WebAtDayMapper {
    int deleteByPrimaryKey(WebAtDayKey key);

    int insert(WebAtDay record);

    int insertSelective(WebAtDay record);

    WebAtDay selectByPrimaryKey(WebAtDayKey key);

    int updateByPrimaryKeySelective(WebAtDay record);

    int updateByPrimaryKey(WebAtDay record);

    /**
     * 根据appkey以及时间，获取指定应用集合的pv和uv数
     * @param paramMap
     * @return
     */
    public List<OAWebChatLineDTO> selectPVAndUVByAppKeySetAndDuringDate(Map<String, Object> paramMap);
}