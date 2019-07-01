package bigscreen.hubpd.com.mapper.uar_basic;

import bigscreen.hubpd.com.bean.uar_basic.Media;

import java.util.Map;
import java.util.Set;

public interface MediaMapper {
    int insert(Media record);

    int insertSelective(Media record);

    /**
     * 查询指定系统类型的机构id集合
     * @param sysType       系统类型1：uar；2：甘肃
     * @return
     */
    public Set<String> findAllOriginIdListInBigscreen(Integer sysType);

    /**
     * 根据系统类型和机构id查询是否存在此机构
     * @return
     */
    public Media findMediaByOrgIdAndSystype(Map<String, Object> paramMap);
}