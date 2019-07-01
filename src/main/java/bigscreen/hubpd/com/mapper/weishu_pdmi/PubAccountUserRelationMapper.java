package bigscreen.hubpd.com.mapper.weishu_pdmi;

import bigscreen.hubpd.com.bean.weishu_pdmi.PubAccountUserRelation;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface PubAccountUserRelationMapper {
    int insert(PubAccountUserRelation record);

    int insertSelective(PubAccountUserRelation record);

    /**
     * 根据租户类型和系统类型，查询所有授权的自由公众号id集合
     * @return
     */
    public List<Integer> getPubAccountIdsByOrgIdAndSysType(Map<String, Object> paramMap);
}