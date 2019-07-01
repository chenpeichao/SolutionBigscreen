package bigscreen.hubpd.com.mapper.weishu_pdmi;

import bigscreen.hubpd.com.bean.weishu_pdmi.ArticleStatDay;
import bigscreen.hubpd.com.dto.OAWXChartLineDTO;
import bigscreen.hubpd.com.dto.OAWXTopNArticleDTO;

import java.util.List;
import java.util.Map;

public interface ArticleStatDayMapper {
    int insert(ArticleStatDay record);

    int insertSelective(ArticleStatDay record);

    /**
     * 根据公众号id集合以及指定时间间隔，查询其每日的阅读总和
     * @param paramMap
     * @return
     */
    public List<OAWXChartLineDTO> selectReadNumAndLikeNumByPubAccountIdsAndDuringDate(Map<String, Object> paramMap);

    /**
     * 查询指定公众号集合下文章阅读点赞倒序排列并得到相应指标
     * @param paramMap
     * @return
     */
    public List<OAWXTopNArticleDTO> selectTitleAndUrlByPubAccountIdsAndDuringDate(Map<String, Object> paramMap);
}