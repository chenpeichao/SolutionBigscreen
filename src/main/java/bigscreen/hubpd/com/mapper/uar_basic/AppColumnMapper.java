package bigscreen.hubpd.com.mapper.uar_basic;

import bigscreen.hubpd.com.bean.uar_basic.AppColumn;

import java.util.Map;

public interface AppColumnMapper {
    int deleteByPrimaryKey(Integer columnid);

    int insert(AppColumn record);

    int insertSelective(AppColumn record);

    AppColumn selectByPrimaryKey(Integer columnid);

    int updateByPrimaryKeySelective(AppColumn record);

    int updateByPrimaryKey(AppColumn record);

    /**
     * 根据应用appId和栏目tag关联栏目表进行栏目中文名查询
     * @param paramMap
     * @return
     */
    public String findColumnNameByAppIdAndColumnTag(Map<String, Object> paramMap);
}