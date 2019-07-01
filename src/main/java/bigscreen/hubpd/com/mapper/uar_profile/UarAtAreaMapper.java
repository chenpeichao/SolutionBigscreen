package bigscreen.hubpd.com.mapper.uar_profile;

import bigscreen.hubpd.com.bean.uar_profile.UarAtArea;
import bigscreen.hubpd.com.dto.UserAreaCountDTO;

import java.util.List;
import java.util.Map;

public interface UarAtAreaMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UarAtArea record);

    int insertSelective(UarAtArea record);

    UarAtArea selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UarAtArea record);

    int updateByPrimaryKey(UarAtArea record);

    public List<UserAreaCountDTO> findUserCountInAreaByAppKeySet(Map<String, Object> paramMaps);

    /**
     * 查询指定appkey集合下的省或直辖市的用户数
     * @param paramMaps
     * @return
     */
    public List<UserAreaCountDTO> findUserCountInProvinceByAppKeySet(Map<String, Object> paramMaps);
}