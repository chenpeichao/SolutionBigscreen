package bigscreen.hubpd.com.mapper.uar_profile;

import bigscreen.hubpd.com.bean.uar_profile.OriginReturnRecord;

import java.util.List;
import java.util.Map;

public interface OriginReturnRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OriginReturnRecord record);

    int insertSelective(OriginReturnRecord record);

    OriginReturnRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OriginReturnRecord record);

    int updateByPrimaryKeyWithBLOBs(OriginReturnRecord record);

    int updateByPrimaryKey(OriginReturnRecord record);

    /**
     * 根据机构id和查询时间，查询接口返回值
     *
     * @param params 机构id，查询时间
     * @return
     */
    public List<String> findOriginReturnRecordByOriginId(Map<String, Object> params);
}