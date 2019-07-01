package bigscreen.hubpd.com.service;


import bigscreen.hubpd.com.bean.uar_profile.OriginReturnRecord;

/**
 * 大屏接口调用记录
 *
 * @author cpc
 * @create 2018-08-15 18:55
 **/
public interface OriginReturnRecordService {
    public int insert(OriginReturnRecord record);

    public int insertSelective(OriginReturnRecord record);

    /**
     * 根据机构id和查询时间，查询接口返回值
     *
     * @param originId   机构id
     * @param searchDate 查询时间
     * @return
     */
    public String findOriginReturnRecordByOriginId(String originId, String searchDate);
}
