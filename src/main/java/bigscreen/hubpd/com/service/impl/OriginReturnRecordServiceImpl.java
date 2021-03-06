package bigscreen.hubpd.com.service.impl;

import bigscreen.hubpd.com.bean.uar_profile.OriginReturnRecord;
import bigscreen.hubpd.com.mapper.uar_profile.OriginReturnRecordMapper;
import bigscreen.hubpd.com.service.OriginReturnRecordService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大屏接口调用记录
 *
 * @author cpc
 * @create 2018-08-15 18:56
 **/
@Service
@Transactional
public class OriginReturnRecordServiceImpl implements OriginReturnRecordService {
    private Logger logger = Logger.getLogger(OriginReturnRecordServiceImpl.class);

    @Autowired
    private OriginReturnRecordMapper originReturnRecordMapper;

    public int insert(OriginReturnRecord record) {
        return originReturnRecordMapper.insert(record);
    }

    public int insertSelective(OriginReturnRecord record) {
        return originReturnRecordMapper.insertSelective(record);
    }

    /**
     * 根据机构id和查询时间，查询接口返回值
     *
     * @param originId   机构id
     * @param searchDate 查询时间
     * @return
     */
    public String findOriginReturnRecordByOriginId(String originId, String searchDate) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("originId", originId);
        params.put("searchDate", searchDate);
        //查询最近的一条数据
        List<String> originReturnRecordByOriginIdList = originReturnRecordMapper.findOriginReturnRecordByOriginId(params);
        if (originReturnRecordByOriginIdList != null && originReturnRecordByOriginIdList.size() > 0) {
            if (originReturnRecordByOriginIdList.size() > 1) {
                logger.error("机构id【" + originId + "】在接口缓存数据库中保存的接口返回信息保存记录错误，记录数大于1条");
            }
            return originReturnRecordByOriginIdList.get(0);
        } else {
            return "";
        }
    }
}
