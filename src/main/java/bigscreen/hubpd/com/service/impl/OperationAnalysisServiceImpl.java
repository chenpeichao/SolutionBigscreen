package bigscreen.hubpd.com.service.impl;

import bigscreen.hubpd.com.dto.OAAppChatLineDTO;
import bigscreen.hubpd.com.dto.OAWXChartLineDTO;
import bigscreen.hubpd.com.dto.OAWXTopNArticleDTO;
import bigscreen.hubpd.com.dto.OAWebChatLineDTO;
import bigscreen.hubpd.com.mapper.uar_statistic.AppAtDayMapper;
import bigscreen.hubpd.com.mapper.uar_statistic.WebAtDayMapper;
import bigscreen.hubpd.com.mapper.weishu_pdmi.ArticleStatDayMapper;
import bigscreen.hubpd.com.mapper.weishu_pdmi.PubAccountUserRelationMapper;
import bigscreen.hubpd.com.service.AppInfoService;
import bigscreen.hubpd.com.service.MediaService;
import bigscreen.hubpd.com.service.OperationAnalysisService;
import bigscreen.hubpd.com.utils.DateUtils;
import bigscreen.hubpd.com.utils.ESConfigConstants;
import bigscreen.hubpd.com.utils.ErrorCode;
import bigscreen.hubpd.com.utils.StatisticESClient;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.MetricsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 运营分析service实现类
 *
 * @author cpc
 * @create 2019-04-08 11:40
 **/
@Service
public class OperationAnalysisServiceImpl implements OperationAnalysisService {
    private Logger logger = Logger.getLogger(OperationAnalysisServiceImpl.class);

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private PubAccountUserRelationMapper pubAccountUserRelationMapper;
    @Autowired
    private ArticleStatDayMapper articleStatDayMapper;
    @Autowired
    private WebAtDayMapper webAtDayMapper;
    @Autowired
    private AppAtDayMapper appAtDayMapper;
    @Autowired
    private MediaService mediaService;

    /**
     * 获取指定系统的指定租户授权公众号的7天、7周、7月的阅读数和点赞数
     * @param lesseeId              租户id
     * @param sysType               系统类型
     * @param imePeriodFlag         时间周期
     * @return
     */
    public Map<String, Object> getWXSevenDWMReadAndLikeData(String lesseeId, Integer sysType, Integer imePeriodFlag) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        if(null == mediaService.findMediaByOrgIdAndSystype(lesseeId, sysType)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_NOT_FOUND_MEDIA_ID);
            resultMap.put("message", "系统中不存在指定机构");
            return resultMap;
        }

        //1、从公众号用户关联表中，通过指定租户id查询授权公众号
        //TODO:cpc 由于目前只有一个机构，而且时间问题，所以暂时不进行systype的微信模块的区分
        Map<String, Object> paramMapGetPubAccountIdsByOrgIdAndSysType = new HashMap<String, Object>();
        paramMapGetPubAccountIdsByOrgIdAndSysType.put("lesseeId", lesseeId);
        paramMapGetPubAccountIdsByOrgIdAndSysType.put("sysType", sysType);
        List<Integer> selfPubAccountIds = pubAccountUserRelationMapper.getPubAccountIdsByOrgIdAndSysType(paramMapGetPubAccountIdsByOrgIdAndSysType);
        //2、获取上部查询出来的公众号的210天的阅读数和点赞数信息
        Date currentDate = new Date();
        String startDateStr = "";
        String endDateStr = "";
        try {
            startDateStr = DateUtils.getBeforeDateStrByDateAndPattern(currentDate, -210, "yyyy-MM-dd");
            endDateStr = DateUtils.getBeforeDateStrByDateAndPattern(currentDate, -1, "yyyy-MM-dd");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> paramMapSelectReadNumAndLikeNumByPubAccountIdsAndDuringDate = new HashMap<String, Object>();
        paramMapSelectReadNumAndLikeNumByPubAccountIdsAndDuringDate.put("pubAccountIds", selfPubAccountIds);
        paramMapSelectReadNumAndLikeNumByPubAccountIdsAndDuringDate.put("startDate", startDateStr);
        paramMapSelectReadNumAndLikeNumByPubAccountIdsAndDuringDate.put("endDate", endDateStr);
        List<Object> resultItemList = new ArrayList<Object>();

        if(null != selfPubAccountIds && selfPubAccountIds.size() > 0) {
            List<OAWXChartLineDTO> oawxChartLineDTOLists = articleStatDayMapper.selectReadNumAndLikeNumByPubAccountIdsAndDuringDate(paramMapSelectReadNumAndLikeNumByPubAccountIdsAndDuringDate);

            //由于查询结果中可能存在前210天中的天数缺失时，数据会丢失，所以造成数据不准，故在此进行天数缺失数据填充0处理
            List<String> duringDateStringList = DateUtils.getDuringDaysString(startDateStr, endDateStr, "yyyy-MM-dd");

            List<OAWXChartLineDTO> resultOAWXChartLineDTO = new ArrayList<OAWXChartLineDTO>();
            for(String dateString : duringDateStringList) {
                OAWXChartLineDTO oawxChartLineDTO = new OAWXChartLineDTO(0l, 0l);
                oawxChartLineDTO.setPublishDate(dateString);
                for(OAWXChartLineDTO oawxChartLineDTOTmp : oawxChartLineDTOLists) {
                    if(dateString.equals(oawxChartLineDTOTmp.getPublishDate())) {
                        oawxChartLineDTO.setLikeNum(oawxChartLineDTOTmp.getLikeNum());
                        oawxChartLineDTO.setReadNum(oawxChartLineDTOTmp.getReadNum());
                        break;
                    }
                }
                resultOAWXChartLineDTO.add(oawxChartLineDTO);
            }

            Collections.sort(resultOAWXChartLineDTO, new Comparator<OAWXChartLineDTO>() {
                @Override
                public int compare(OAWXChartLineDTO o1, OAWXChartLineDTO o2) {
                    return o2.getPublishDate().compareTo(o1.getPublishDate());
                }
            });


            int index = 1;
            switch (imePeriodFlag) {
                case 1:
                    for(int i = 1; i < 8; i++) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        //当返回值的和刚好满足当前需要的折现图上点的数据时，进行封装
                        if(null != resultOAWXChartLineDTO && resultOAWXChartLineDTO.size() >= i) {
                            tmpMap.put("readNum", resultOAWXChartLineDTO.get(i-1).getReadNum());
                            tmpMap.put("likeNum", resultOAWXChartLineDTO.get(i-1).getLikeNum());
                            tmpMap.put("index", i);     //用于标识折线图上点的索引
                        } else {
                            tmpMap.put("readNum", 0);
                            tmpMap.put("likeNum", 0);
                            tmpMap.put("index", i);     //用于标识折线图上点的索引
                        }
                        resultItemList.add(tmpMap);
                    };
                    break;
                case 2:
                    index = 1;
                    for(int i = 1; i < 50; ) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        Long readNumTmp = 0l;
                        Long likeNumTmp = 0l;

                        for(int j = 1; j < 8; j++) {
                            if(null != resultOAWXChartLineDTO && resultOAWXChartLineDTO.size() >= i) {
                                readNumTmp += resultOAWXChartLineDTO.get(i-1).getReadNum();
                                likeNumTmp += resultOAWXChartLineDTO.get(i-1).getLikeNum();
                            }

                            i++;
                        }

                        tmpMap.put("readNum", readNumTmp);
                        tmpMap.put("likeNum", likeNumTmp);
                        tmpMap.put("index", index);     //用于标识折线图上点的索引
                        //用于记录返回值中的index坐标(只显示7个点)
                        index ++;

                        resultItemList.add(tmpMap);
                    };
                    break;
                case 3:
                    index = 1;
                    for(int i = 1; i < 211; ) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        Long readNumTmp = 0l;
                        Long likeNumTmp = 0l;

                        for(int j = 1; j < 31; j++) {
                            if(null != resultOAWXChartLineDTO && resultOAWXChartLineDTO.size() >= i) {
                                readNumTmp += resultOAWXChartLineDTO.get(i-1).getReadNum();
                                likeNumTmp += resultOAWXChartLineDTO.get(i-1).getLikeNum();
                            }

                            i++;
                        }

                        tmpMap.put("readNum", readNumTmp);
                        tmpMap.put("likeNum", likeNumTmp);
                        tmpMap.put("index", index);     //用于标识折线图上点的索引
                        //用于记录返回值中的index坐标(只显示7个点)
                        index ++;
                        resultItemList.add(tmpMap);
                    };
                    break;
                default:
                    logger.info("error search : 未找到有效的时间周期值");
                    for(int i = 1; i < 8; i++) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        tmpMap.put("readNum", 0);
                        tmpMap.put("likeNum", 0);
                        tmpMap.put("index", i);     //用于标识折线图上点的索引
                        resultItemList.add(tmpMap);
                    }
            }
        } else {
            logger.info("查询的租户id【"+lesseeId+"】没有对应的自有公众号");
            for(int i = 1; i < 8; i++) {
                Map<String, Object> tmpMap = new HashMap<String, Object>();
                tmpMap.put("readNum", 0);
                tmpMap.put("likeNum", 0);
                tmpMap.put("index", i);     //用于标识折线图上点的索引
                resultItemList.add(tmpMap);
            }
        }

        resultMap.put("code", 0);
        resultMap.put("data", resultItemList);
        return resultMap;
    }

    /**
     * 获取指定系统的指定租户授权网站的7天、7周、7月的pv和uv
     * @param lesseeId              租户id
     * @param sysType               系统类型
     * @param imePeriodFlag         时间周期
     * @return
     */
    public Map<String, Object> getWebSevenYMDPVAndUVData(String lesseeId, Integer sysType, Integer imePeriodFlag) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        if(null == mediaService.findMediaByOrgIdAndSystype(lesseeId, sysType)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_NOT_FOUND_MEDIA_ID);
            resultMap.put("message", "系统中不存在指定机构");
            return resultMap;
        }

        //1、根据租户id查询租户下的所有网站的appkey
        Set<String> appKeySet = appInfoService.getAppKeyByLesseeIdAndAppType(lesseeId, 1);

        //TODO:pcchen 网站折线图测试环境数据写死
//        if("62521361b706427493611ea51e55ca2d".equals(lesseeId)) {
//            appKeySet.add("UAR-000119_226");
//        }

        Date currentDate = new Date();
        String startDateStr = "";
        String endDateStr = "";
        try {
            startDateStr = DateUtils.getBeforeDateStrByDateAndPattern(currentDate, -210, "yyyyMMdd");
            endDateStr = DateUtils.getBeforeDateStrByDateAndPattern(currentDate, -1, "yyyyMMdd");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //2、根据网站appkey，查询网站的pv、uv
        List<Object> resultItemList = new ArrayList<Object>();

        if(null != appKeySet && appKeySet.size() > 0) {
            Map<String, Object> selectPVAndUVByAppKeySetAndDuringDateParamMap = new HashMap<String, Object>();
            selectPVAndUVByAppKeySetAndDuringDateParamMap.put("appKeySet", appKeySet);
            selectPVAndUVByAppKeySetAndDuringDateParamMap.put("startDay", Long.parseLong(startDateStr));
            selectPVAndUVByAppKeySetAndDuringDateParamMap.put("endDay", Long.parseLong(endDateStr));
            List<OAWebChatLineDTO> resultOAWebChatLineDTOTmp = webAtDayMapper.selectPVAndUVByAppKeySetAndDuringDate(selectPVAndUVByAppKeySetAndDuringDateParamMap);

            //由于查询结果中可能存在前210天中的天数缺失时，数据会丢失，所以造成数据不准，故在此进行天数缺失数据填充0处理
            List<String> duringDateStringList = DateUtils.getDuringDaysString(startDateStr, endDateStr, "yyyyMMdd");

            List<OAWebChatLineDTO> resultOAWebChatLineDTO = new ArrayList<OAWebChatLineDTO>();
            for(String dateString : duringDateStringList) {
                OAWebChatLineDTO oaWebChatLineDTO = new OAWebChatLineDTO(0l, 0l);
                oaWebChatLineDTO.setDay(Long.parseLong(dateString));
                for(OAWebChatLineDTO oaWebChatLineDTOTmp : resultOAWebChatLineDTOTmp) {
                    if(oaWebChatLineDTOTmp.getDay() == Long.parseLong(dateString)) {
                        oaWebChatLineDTO.setPv(oaWebChatLineDTOTmp.getPv());
                        oaWebChatLineDTO.setUv(oaWebChatLineDTOTmp.getUv());
                        break;
                    }
                }
                resultOAWebChatLineDTO.add(oaWebChatLineDTO);
            }

            Collections.sort(resultOAWebChatLineDTO, new Comparator<OAWebChatLineDTO>() {
                @Override
                public int compare(OAWebChatLineDTO o1, OAWebChatLineDTO o2) {
                    return o2.getDay().intValue() - o1.getDay().intValue();
                }
            });

            int index = 1;
            switch (imePeriodFlag) {
                case 1:
                    for(int i = 1; i < 8; i++) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        //当返回值的和刚好满足当前需要的折现图上点的数据时，进行封装
                        if(null != resultOAWebChatLineDTO && resultOAWebChatLineDTO.size() >= i) {
                            tmpMap.put("pv", resultOAWebChatLineDTO.get(i-1).getPv());
                            tmpMap.put("uv", resultOAWebChatLineDTO.get(i-1).getUv());
                            tmpMap.put("index", i);     //用于标识折线图上点的索引
                        } else {
                            tmpMap.put("pv", 0);
                            tmpMap.put("uv", 0);
                            tmpMap.put("index", i);     //用于标识折线图上点的索引
                        }
                        resultItemList.add(tmpMap);
                    };
                    break;
                case 2:
                    index = 1;
                    for(int i = 1; i < 50; ) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        Long pvNumTmp = 0l;
                        Long uvNumTmp = 0l;

                        for(int j = 1; j < 8; j++) {
                            if(null != resultOAWebChatLineDTO && resultOAWebChatLineDTO.size() >= i) {
                                pvNumTmp += resultOAWebChatLineDTO.get(i-1).getPv();
                                uvNumTmp += resultOAWebChatLineDTO.get(i-1).getUv();
                            }

                            i++;
                        }

                        tmpMap.put("pv", pvNumTmp);
                        tmpMap.put("uv", uvNumTmp);
                        tmpMap.put("index", index);     //用于标识折线图上点的索引
                        //用于记录返回值中的index坐标(只显示7个点)
                        index ++;

                        resultItemList.add(tmpMap);
                    };
                    break;
                case 3:
                    index = 1;
                    for(int i = 1; i < 211; ) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        Long pvNumTmp = 0l;
                        Long uvNumTmp = 0l;

                        for(int j = 1; j < 31; j++) {
                            if(null != resultOAWebChatLineDTO && resultOAWebChatLineDTO.size() >= i) {
                                pvNumTmp += resultOAWebChatLineDTO.get(i-1).getPv();
                                uvNumTmp += resultOAWebChatLineDTO.get(i-1).getUv();
                            }

                            i++;
                        }

                        tmpMap.put("pv", pvNumTmp);
                        tmpMap.put("uv", uvNumTmp);
                        tmpMap.put("index", index);     //用于标识折线图上点的索引
                        //用于记录返回值中的index坐标(只显示7个点)
                        index ++;
                        resultItemList.add(tmpMap);
                    };
                    break;
                default:
                    logger.info("error search : 未找到有效的时间周期值");
                    for(int i = 1; i < 8; i++) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        tmpMap.put("pv", 0);
                        tmpMap.put("uv", 0);
                        tmpMap.put("index", i);     //用于标识折线图上点的索引
                        resultItemList.add(tmpMap);
                    }
            }
        } else {
            for(int i = 1; i < 8; i++) {
                Map<String, Object> tmpMap = new HashMap<String, Object>();
                tmpMap.put("pv", 0);
                tmpMap.put("uv", 0);
                tmpMap.put("index", i);     //用于标识折线图上点的索引
                resultItemList.add(tmpMap);
            }
        }

        resultMap.put("code", 0);
        resultMap.put("data", resultItemList);
        return resultMap;
    }

    /**
     * 获取指定系统的指定租户授权客户端的7天、7周、7月的pv和uv
     * @param lesseeId              租户id
     * @param sysType               系统类型
     * @param imePeriodFlag         时间周期
     * @return
     */
    public Map<String, Object> getAppSevenYMDPVAndUVData(String lesseeId, Integer sysType, Integer imePeriodFlag) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        if(null == mediaService.findMediaByOrgIdAndSystype(lesseeId, sysType)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_NOT_FOUND_MEDIA_ID);
            resultMap.put("message", "系统中不存在指定机构");
            return resultMap;
        }

        Set<String> appKeySet = appInfoService.getAppKeyByLesseeIdAndAppType(lesseeId, 2);

        //TODO:pcchen 客户端折线图测试环境数据写死
//        if("62521361b706427493611ea51e55ca2d".equals(lesseeId)) {
//            appKeySet.add("UAR-000282_438");
//            appKeySet.add("UAR-000369_783");
//        }

        Date currentDate = new Date();
        String startDateStr = "";
        String endDateStr = "";
        try {
            startDateStr = DateUtils.getBeforeDateStrByDateAndPattern(currentDate, -210, "yyyyMMdd");
            endDateStr = DateUtils.getBeforeDateStrByDateAndPattern(currentDate, -1, "yyyyMMdd");
        } catch (Exception e) {
            e.printStackTrace();
        }

             //2、根据网站appkey，查询网站的pv、uv
            List<Object> resultItemList = new ArrayList<Object>();

            if(null != appKeySet && appKeySet.size() > 0) {
                Map<String, Object> selectPVAndUVByAppKeySetAndDuringDateParamMap = new HashMap<String, Object>();
                selectPVAndUVByAppKeySetAndDuringDateParamMap.put("appKeySet", appKeySet);
                selectPVAndUVByAppKeySetAndDuringDateParamMap.put("startDay", Long.parseLong(startDateStr));
                selectPVAndUVByAppKeySetAndDuringDateParamMap.put("endDay", Long.parseLong(endDateStr));
                List<OAAppChatLineDTO> resultOAAppChatLineDTOTmp = appAtDayMapper.selectPVAndUVByAppKeySetAndDuringDate(selectPVAndUVByAppKeySetAndDuringDateParamMap);

                //由于查询结果中可能存在前210天中的天数缺失时，数据会丢失，所以造成数据不准，故在此进行天数缺失数据填充0处理
                List<String> duringDateStringList = DateUtils.getDuringDaysString(startDateStr, endDateStr, "yyyyMMdd");

                List<OAAppChatLineDTO> resultOAAppChatLineDTO = new ArrayList<OAAppChatLineDTO>();
                for(String dateString : duringDateStringList) {
                    OAAppChatLineDTO oaAppChatLineDTO = new OAAppChatLineDTO(0l, 0l);
                    oaAppChatLineDTO.setDay(Long.parseLong(dateString));
                    for(OAAppChatLineDTO oaAppChatLineDTOTmp : resultOAAppChatLineDTOTmp) {
                        if(oaAppChatLineDTOTmp.getDay() == Long.parseLong(dateString)) {
                            oaAppChatLineDTO.setPv(oaAppChatLineDTOTmp.getPv());
                            oaAppChatLineDTO.setUv(oaAppChatLineDTOTmp.getUv());
                            break;
                        }
                    }
                    resultOAAppChatLineDTO.add(oaAppChatLineDTO);
                }

                Collections.sort(resultOAAppChatLineDTO, new Comparator<OAAppChatLineDTO>() {
                    @Override
                    public int compare(OAAppChatLineDTO o1, OAAppChatLineDTO o2) {
                        return o2.getDay().intValue() - o1.getDay().intValue();
                    }
                });

                int index = 1;
            switch (imePeriodFlag) {
                case 1:
                    for(int i = 1; i < 8; i++) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        //当返回值的和刚好满足当前需要的折现图上点的数据时，进行封装
                        if(null != resultOAAppChatLineDTO && resultOAAppChatLineDTO.size() >= i) {
                            tmpMap.put("pv", resultOAAppChatLineDTO.get(i-1).getPv());
                            tmpMap.put("activeUserNum", resultOAAppChatLineDTO.get(i-1).getUv());
                            tmpMap.put("index", i);     //用于标识折线图上点的索引
                        } else {
                            tmpMap.put("pv", 0);
                            tmpMap.put("activeUserNum", 0);
                            tmpMap.put("index", i);     //用于标识折线图上点的索引
                        }
                        resultItemList.add(tmpMap);
                    };
                    break;
                case 2:
                    index = 1;
                    for(int i = 1; i < 50; ) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        Long pvNumTmp = 0l;
                        Long uvNumTmp = 0l;

                        for(int j = 1; j < 8; j++) {
                            if(null != resultOAAppChatLineDTO && resultOAAppChatLineDTO.size() >= i) {
                                pvNumTmp += resultOAAppChatLineDTO.get(i-1).getPv();
                                uvNumTmp += resultOAAppChatLineDTO.get(i-1).getUv();
                            }

                            i++;
                        }

                        tmpMap.put("pv", pvNumTmp);
                        tmpMap.put("activeUserNum", uvNumTmp);
                        tmpMap.put("index", index);     //用于标识折线图上点的索引
                        //用于记录返回值中的index坐标(只显示7个点)
                        index ++;

                        resultItemList.add(tmpMap);
                    };
                    break;
                case 3:
                    index = 1;
                    for(int i = 1; i < 211; ) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        Long pvNumTmp = 0l;
                        Long uvNumTmp = 0l;

                        for(int j = 1; j < 31; j++) {
                            if(null != resultOAAppChatLineDTO && resultOAAppChatLineDTO.size() >= i) {
                                pvNumTmp += resultOAAppChatLineDTO.get(i-1).getPv();
                                uvNumTmp += resultOAAppChatLineDTO.get(i-1).getUv();
                            }

                            i++;
                        }

                        tmpMap.put("pv", pvNumTmp);
                        tmpMap.put("activeUserNum", uvNumTmp);
                        tmpMap.put("index", index);     //用于标识折线图上点的索引
                        //用于记录返回值中的index坐标(只显示7个点)
                        index ++;
                        resultItemList.add(tmpMap);
                    };
                    break;
                default:
                    logger.info("error search : 未找到有效的时间周期值");
                    for(int i = 1; i < 8; i++) {
                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        tmpMap.put("pv", 0);
                        tmpMap.put("activeUserNum", 0);
                        tmpMap.put("index", i);     //用于标识折线图上点的索引
                        resultItemList.add(tmpMap);
                    }
            }
        } else {
            for(int i = 1; i < 8; i++) {
                Map<String, Object> tmpMap = new HashMap<String, Object>();
                tmpMap.put("pv", 0);
                tmpMap.put("activeUserNum", 0);
                tmpMap.put("index", i);     //用于标识折线图上点的索引
                resultItemList.add(tmpMap);
            }
        }

        resultMap.put("code", 0);
        resultMap.put("data", resultItemList);
        return resultMap;
    }

    /**
     * 获取网站、客户端、微博、微信topN文章
     * @param lesseeId           租户id
     * @param sysType               系统标识；默认为2（甘肃项目）
     * @param timePeriodFlag        时间维度(1：天；2：周；3：月)
     * @param appFlag               应用标识（1：表示网站；2：表示客户端；3：表示微信；4：表示微博）
     * @param topNum                获取前topN
     * @return
     */
    public Map<String, Object> getTopNArticleInAppFlag(String lesseeId, Integer sysType, Integer timePeriodFlag, Integer appFlag, Integer topNum) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        if(null == mediaService.findMediaByOrgIdAndSystype(lesseeId, sysType)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_NOT_FOUND_MEDIA_ID);
            resultMap.put("message", "系统中不存在指定机构");
            return resultMap;
        }

        Set<String> appKeySet = new HashSet<String>();

        //获取自有公众号id列表
        Map<String, Object> paramMapGetPubAccountIdsByOrgIdAndSysType = new HashMap<String, Object>();
        paramMapGetPubAccountIdsByOrgIdAndSysType.put("lesseeId", lesseeId);
        paramMapGetPubAccountIdsByOrgIdAndSysType.put("sysType", sysType);
        List<Integer> selfPubAccountIds = pubAccountUserRelationMapper.getPubAccountIdsByOrgIdAndSysType(paramMapGetPubAccountIdsByOrgIdAndSysType);

        List<Map<String, Object>> resultTitleAndUrlMapList = new ArrayList<Map<String, Object>>();
        switch (appFlag) {
            case 1:         //网站
                appKeySet = appInfoService.getAppKeyByLesseeIdAndAppType(lesseeId, 1);
                logger.info("appKey的集合为" + appKeySet);
                //TODO:pcchen 网站topN文章测试环境数据写死
//                if("62521361b706427493611ea51e55ca2d".equals(lesseeId)) {
//                    appKeySet.add("UAR-000139_459");
//                    appKeySet.add("UAR-000201_728");
//                }
                if(null != appKeySet && appKeySet.size() > 0) {
                    try {
                        switch (timePeriodFlag) {
                            case 1:
                                //包含pv以及itemId的title和uri
                                resultTitleAndUrlMapList = getTopNArticleInWebAndAppByDateFlag(appKeySet, topNum, -7, -1, "yyyyMMdd");
                                break;
                            case 2:
                                resultTitleAndUrlMapList = getTopNArticleInWebAndAppByDateFlag(appKeySet, topNum, -30, -1, "yyyyMMdd");
                                break;
                            case 3:
                                resultTitleAndUrlMapList = getTopNArticleInWebAndAppByDateFlag(appKeySet, topNum, -210, -1, "yyyyMMdd");
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                break;
            case 2:         //客户端
                appKeySet = appInfoService.getAppKeyByLesseeIdAndAppType(lesseeId, 2);
                logger.info("appKey的集合为" + appKeySet);

                //TODO:pcchen 移动应用topN文章测试环境数据写死
//                if("62521361b706427493611ea51e55ca2d".equals(lesseeId)) {
//                    appKeySet.add("UAR-000139_459");
//                    appKeySet.add("UAR-000159_219");
//                }
                if(null != appKeySet && appKeySet.size() > 0) {
                    try {
                        switch (timePeriodFlag) {
                            case 1:
                                //包含pv以及itemId的title和uri
                                resultTitleAndUrlMapList = getTopNArticleInWebAndAppByDateFlag(appKeySet, topNum, -7, -1, "yyyyMMdd");
                                break;
                            case 2:
                                resultTitleAndUrlMapList = getTopNArticleInWebAndAppByDateFlag(appKeySet, topNum, -30, -1, "yyyyMMdd");
                                break;
                            case 3:
                                resultTitleAndUrlMapList = getTopNArticleInWebAndAppByDateFlag(appKeySet, topNum, -210, -1, "yyyyMMdd");
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                break;
            case 3:         //微信
                if(null != selfPubAccountIds && selfPubAccountIds.size() > 0) {
                    switch (timePeriodFlag) {
                        case 1:
                            resultTitleAndUrlMapList = getTitleAndUrlByPubAccountIdsAndDuringDateFlag(selfPubAccountIds, topNum, 0, -7, -1);
                            break;
                        case 2:
                            resultTitleAndUrlMapList = getTitleAndUrlByPubAccountIdsAndDuringDateFlag(selfPubAccountIds, topNum, 0, -30, -1);
                            break;
                        case 3:
                            resultTitleAndUrlMapList = getTitleAndUrlByPubAccountIdsAndDuringDateFlag(selfPubAccountIds, topNum, 0, -210, -1);
                            break;
                        default:
                            break;
                    }
                }
                break;
//            case 4:         //微博
//                break;
        }
        resultMap.put("code", 0);
        resultMap.put("data", resultTitleAndUrlMapList);
        return resultMap;
    }


    /**
     * 获取指定公众号集合在指定周期下按照阅读数和点赞数排序的文章集合(title和url)
     * @param pubAccountIds         公众号集合
     * @param topNum                显示数量
     * @param start                 起始标识
     * @param startDateFlag         开始日期标识
     * @param endDateFlag           技术日期标识
     * @return
     */
    public List<Map<String, Object>> getTitleAndUrlByPubAccountIdsAndDuringDateFlag(List<Integer> pubAccountIds, Integer topNum, Integer start,
                                         Integer startDateFlag, Integer endDateFlag) {
        Date currentDate = new Date();
        String startDateStr = "";
        String endDateStr = "";
        try {
            startDateStr = DateUtils.getBeforeDateStrByDateAndPattern(currentDate, startDateFlag, "yyyy-MM-dd");
            endDateStr = DateUtils.getBeforeDateStrByDateAndPattern(currentDate, endDateFlag, "yyyy-MM-dd");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("start", start);
        paramMap.put("size", topNum);
        paramMap.put("pubAccountIds", pubAccountIds);
        paramMap.put("startDate", startDateStr);
        paramMap.put("endDate", endDateStr);
        List<OAWXTopNArticleDTO> oawxTopNArticleDTOs = articleStatDayMapper.selectTitleAndUrlByPubAccountIdsAndDuringDate(paramMap);

        List<Map<String, Object>> titleUrlResultMapList = new ArrayList<Map<String, Object>>();
        int i = 1;
        for(OAWXTopNArticleDTO oawxTopNArticleDTO : oawxTopNArticleDTOs) {
            Map<String, Object> titleUrlResultMap = new HashMap<String, Object>();
            titleUrlResultMap.put("title", oawxTopNArticleDTO.getTitle());
            titleUrlResultMap.put("url", oawxTopNArticleDTO.getUrl());
//            titleUrlResultMap.put("readNum", oawxTopNArticleDTO.getReadNum());
            titleUrlResultMap.put("index", i++);
            titleUrlResultMapList.add(titleUrlResultMap);
        }
        return titleUrlResultMapList;
    }

    public List<Map<String, Object>> getTopNArticleInWebAndAppByDateFlag(Set<String> appKeySet, Integer topNum, Integer startDateFlag, Integer endDateFlag, String datePattern) {
        Date currentDate = new Date();
        String startDateStr = "";
        String endDateStr = "";
        try {
            startDateStr = DateUtils.getBeforeDateStrByDateAndPattern(currentDate, startDateFlag, datePattern);
            endDateStr = DateUtils.getBeforeDateStrByDateAndPattern(currentDate, endDateFlag, datePattern);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //包含pv以及itemId的title和uri
        return getTopNArticleInWebAndApp(appKeySet, topNum, Long.parseLong(startDateStr), Long.parseLong(endDateStr));
    }

    private List<Map<String, Object>> getTopNArticleInWebAndApp(Set<String> atSet, Integer topNum, Long from, Long to) {
        List<Map<String, Object>> titleUrlMapList = new ArrayList<Map<String, Object>>();

        TransportClient client = null;
        JSONObject resultJsonObj = new JSONObject();
        try {
            client = StatisticESClient.getClientIns();
        } catch (IOException e) {
            logger.error("es配置信息错误");
            e.printStackTrace();
            return titleUrlMapList;
        }
        try {
            TermsBuilder root1 = AggregationBuilders.terms("item_id")
                    .field("item_id").size(topNum);          // fmyblack: size设为0才能获得准确值，但会损耗性能

            List<QueryBuilder> atQueryBuilderList = new ArrayList<QueryBuilder>();
            for(String appKey : atSet) {
                QueryBuilder queryBuilder = QueryBuilders.termQuery("at", appKey);
                atQueryBuilderList.add(queryBuilder);
            }
            SearchRequestBuilder srb = client
                    .prepareSearch(ESConfigConstants.ES_STATISTIC_INDEX_ITEM_STATISTIC)
                    .setTypes(ESConfigConstants.ES_STATISTIC_TYPE_ITEM_BASIC_STATISTIC)
                    .setQuery(
                            filterQuery(shouldQuery(atQueryBuilderList),
                                        dayRange(from, to)))
                    .addAggregation(
                            root1.subAggregation(sumAggs("pv", "pv"))
                                    .order(Terms.Order.aggregation("pv", false)))
                    .setSize(0);
            SearchResponse sr = srb.execute().actionGet();
            logger.info("查询聚合信息的pv排序=>" + srb.toString()
                    +"\r\n cost" + sr.getTook());

            Map<String, Aggregation> map = sr.getAggregations().asMap();
            StringTerms terms = (StringTerms) map.get("item_id");
            // 此处缓存部分结果，若查询为0条也缓存
            List<Terms.Bucket> resultList = terms.getBuckets();

            for (int i = 0; i < topNum && i < resultList.size(); i++) {
                Terms.Bucket bucket = resultList.get(i);
                String key = (String) bucket.getKey();
                InternalSum pv = bucket.getAggregations().get("pv");
                int pvValue = (int) pv.getValue();
                logger.info("item_id:" + key + "====>pv:" + pvValue );
                // 修改为先走缓存获取内容详情
                Map<String, Object> subMap = getItemById(key);
                if(null != subMap && null != subMap.get("uri") && null != subMap.get("tt")) {
                    resultJsonObj = new JSONObject();
                    resultJsonObj.put("url", (String) subMap.get("uri"));
                    resultJsonObj.put("title", (String) subMap.get("tt"));
                    //暂时不需要下面两个字段
//                    resultJsonObj.put("item_id", key);
//                    resultJsonObj.put("pv", pvValue);
                    resultJsonObj.put("index", i+1);
                    titleUrlMapList.add(resultJsonObj);
                }
            }
        } catch (Exception e) {
            logger.error("查询topN文章数发生未知错误=》", e);
            e.printStackTrace();
        } finally {
            return titleUrlMapList;
        }
    }

    /**
     * 通过itemid获取内容详情
     *
     * @param itemId
     * @return
     */
    public Map<String, Object> getItemById(String itemId) {
        if (itemId == null || "".equals(itemId)) {
            return null;
        }
        Map<String, Object> subMap = new HashMap<String, Object>();
        try {
            Client client = StatisticESClient.getClientIns();
            SearchRequestBuilder srb = client.prepareSearch(ESConfigConstants.ES_STATISTIC_INDEX_ITEM_STATISTIC)
                    .setTypes(ESConfigConstants.ES_STATISTIC_TYPE_ITEM_BASIC_STATISTIC)
                    .setQuery(filterQuery(termQuery("item_id", itemId)))
                    .addSort("pv", SortOrder.DESC)
                    .setSize(1);
            logger.info("根据item_id查询文章的title和url信息：" + srb.toString());

            SearchResponse res = srb.execute().actionGet();
            System.out.println(res.getTookInMillis());
            subMap = res.getHits().iterator().next()
                    .getSource();
            return subMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("itemId为【"+itemId+"】查询标题和链接失败");
            return subMap;
        }
    }

    private QueryBuilder filterQuery(QueryBuilder... querys) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (QueryBuilder query : querys) {
            boolQuery.filter(query);
        }
        return boolQuery;
    }

    private static MetricsAggregationBuilder sumAggs(String name, String field) {
        return AggregationBuilders.sum(name).field(field);
    }
    // 查询时间范围
    private QueryBuilder dayRange(long from, long to) {
        return QueryBuilders.rangeQuery("day").gte(from).lte(to);
    }

    private static QueryBuilder termQuery(String name, String value) {
        return QueryBuilders.termQuery(name, value);
    }

    /*
     * 或查询
     */
    private static QueryBuilder shouldQuery(List<QueryBuilder> queryBuilderList) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (QueryBuilder query : queryBuilderList) {
            boolQuery.should(query);
        }
        boolQuery.minimumNumberShouldMatch(1);
        return boolQuery;
    }
}
