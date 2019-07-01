package bigscreen.hubpd.com.service.impl;

import bigscreen.hubpd.com.bean.uar_profile.OriginReturnRecord;
import bigscreen.hubpd.com.bean.uar_profile.UarProfileBigscreenAreaDic;
import bigscreen.hubpd.com.dto.UserAreaCountDTO;
import bigscreen.hubpd.com.service.*;
import bigscreen.hubpd.com.utils.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

/**
 * 用户画像service
 *
 * @author cpc
 * @create 2019-04-09 19:29
 **/
@Service
public class UserPortraitServiceImpl implements UserPortraitService {
    private Logger logger = Logger.getLogger(UserPortraitServiceImpl.class);

    @Autowired
    private AppInfoService appInfoService;
    @Autowired
    private OriginReturnRecordService originReturnRecordService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private UarProfileBigscreenAreaDicService uarProfileBigscreenAreaDicService;
    @Autowired
    private UserAreaCountService userAreaCountService;

    /**
     * 用户地域接口，获取各省（topN）分布用户数
     *
     * @param orginId 机构id
     * @return
     */
    public Map<String, Object> findUserCountInProvince(String orginId, Integer sysType, Integer topN) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if(null == mediaService.findMediaByOrgIdAndSystype(orginId, sysType)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_NOT_FOUND_MEDIA_ID);
            resultMap.put("message", "系统中不存在指定机构");
            return resultMap;
        }

        Set<String> appKeyByLesseeIdAndAppType = appInfoService.getAppKeyByLesseeIdAndAppType(orginId, null);

        List<UserAreaCountDTO> userCountInProvinceByAppKeySet = userAreaCountService.findUserCountInProvinceByAppKeySet(appKeyByLesseeIdAndAppType);

        //查询用户画像地域字典表
        Map<String, String> allProfileProvinceAndShouNameDic = uarProfileBigscreenAreaDicService.getAllProfileProvinceAndShouNameDic();
        for(UserAreaCountDTO userAreaCountDTO : userCountInProvinceByAppKeySet) {
            userAreaCountDTO.setRegionName(StringUtils.isNotBlank(allProfileProvinceAndShouNameDic.get(userAreaCountDTO.getRegionName()))?allProfileProvinceAndShouNameDic.get(userAreaCountDTO.getRegionName()) : userAreaCountDTO.getRegionName());
        }

        if(topN >= 0 && topN <= userCountInProvinceByAppKeySet.size()) {
            resultMap.put("data", userCountInProvinceByAppKeySet.subList(0, topN));
            resultMap.put("code", 0);
            resultMap.put("totalRecordNum", topN);
            return resultMap;
        } else {
            resultMap.put("data", userCountInProvinceByAppKeySet);
            resultMap.put("code", 0);
            resultMap.put("totalRecordNum", userCountInProvinceByAppKeySet.size());
            return resultMap;
        }
    }

    /**
     * 用户分析接口，计算性别，青老中，前5地域
     *
     * @param orginId 机构id
     * @return
     */
    public Map<String, Object> getUserAnalyse(String orginId, Integer sysType) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        //null值为了匹配定时任务
        if(null != sysType) {
            //用来过滤非法机构id
            if(null == mediaService.findMediaByOrgIdAndSystype(orginId, sysType)) {
                resultMap.put("code", ErrorCode.ERROR_CODE_NOT_FOUND_MEDIA_ID);
                resultMap.put("message", "系统中不存在指定机构");
                return resultMap;
            }
        }

        String currentDateStr = DateUtils.getDateStrByDate(new Date(), "yyyy-MM-dd");

        // 首先从mysql数据库中查询数据，如果有数据，则直接返回，没有则进行es计算返回，并保存查询天和机构的数据到mysql数据库缓存
        String originReturnRecordStr = originReturnRecordService.findOriginReturnRecordByOriginId(orginId, currentDateStr);
        if (StringUtils.isNotBlank(originReturnRecordStr)) {
            resultMap.put("code", 0);
            resultMap.put("data", JSON.parse(originReturnRecordStr));
            return resultMap;
        }

        Set<String> appKeyByLesseeIdAndAppType = appInfoService.getAppKeyByLesseeIdAndAppType(orginId, null);

        logger.info("画像接口查询机构id为：" + orginId);

        List<Map<String, Object>> dataListMap = new ArrayList<Map<String, Object>>();
        //生产真实数据
//        getGenderAndAgeAndAreaData(appKeyByLesseeIdAndAppType, dataListMap);
        //根据生产上男和中年的数据造假数据
        getGenderAndAgeAndAreaFakeData(appKeyByLesseeIdAndAppType, dataListMap);

        // 对于机构对应的用户以及公众号进行打印
        logger.info("在【" + DateUtils.getDateStrByDate(new Date(), "yyyy-MM-dd HH:mm:ss") + "】查询机构id为【" + orginId + "】，对应应用信息为【" + appKeyByLesseeIdAndAppType.toString() + "】");


        logger.info("机构id为【" + orginId + "】的数据打印完成【"+dataListMap+"】");
        // 对于指定机构在指定查询日期的返回数据，进行mysql数据库的缓存
        String returnDate = originReturnRecordService.findOriginReturnRecordByOriginId(orginId, currentDateStr);
        if (StringUtils.isBlank(returnDate)) {
            OriginReturnRecord originReturnRecord = new OriginReturnRecord();
            originReturnRecord.setOriginId(orginId);
            originReturnRecord.setReturnDate(currentDateStr);
            originReturnRecord.setReturnJson(JSON.toJSONString(dataListMap));

            //接口返回记录保存mysql，缓存
            originReturnRecordService.insert(originReturnRecord);
        }

        resultMap.put("code", 0);
        resultMap.put("data", dataListMap);
        return resultMap;
    }

    /**
     * 用户分析接口，计算性别，青老中，前5地域
     *
     * @param orginId 机构id
     * @return
     */
    public Map<String, Object> getUserAnalyse_bak(String orginId, Integer sysType) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        //null值为了匹配定时任务
        if(null != sysType) {
            //用来过滤非法机构id
            if(null == mediaService.findMediaByOrgIdAndSystype(orginId, sysType)) {
                resultMap.put("code", ErrorCode.ERROR_CODE_NOT_FOUND_MEDIA_ID);
                resultMap.put("message", "系统中不存在指定机构");
                return resultMap;
            }
        }

        String currentDateStr = DateUtils.getDateStrByDate(new Date(), "yyyy-MM-dd");

        // 首先从mysql数据库中查询数据，如果有数据，则直接返回，没有则进行es计算返回，并保存查询天和机构的数据到mysql数据库缓存
        String originReturnRecordStr = originReturnRecordService.findOriginReturnRecordByOriginId(orginId, currentDateStr);
        if (StringUtils.isNotBlank(originReturnRecordStr)) {
            resultMap.put("code", 0);
            resultMap.put("data", JSON.parse(originReturnRecordStr));
            return resultMap;
        }

        Set<String> appKeyByLesseeIdAndAppType = appInfoService.getAppKeyByLesseeIdAndAppType(orginId, null);

        logger.info("画像接口查询机构id为：" + orginId);
        //TODO:pcchen 用于测试环境有数据
//        if(orginId.equals("5b36564f0cc74273be115e6e2faa1b68")) {
//            appKeyByLesseeIdAndAppType.add("UAR-000201_728");
//        }

        List<Map<String, Object>> dataListMap = new ArrayList<Map<String, Object>>();
        //生产真实数据
//        getGenderAndAgeAndAreaData(appKeyByLesseeIdAndAppType, dataListMap);
        //根据生产上男和中年的数据造假数据
        getGenderAndAgeAndAreaFakeData(appKeyByLesseeIdAndAppType, dataListMap);

        // 对于机构对应的用户以及公众号进行打印
        logger.info("在【" + DateUtils.getDateStrByDate(new Date(), "yyyy-MM-dd HH:mm:ss") + "】查询机构id为【" + orginId + "】，对应应用信息为【" + appKeyByLesseeIdAndAppType.toString() + "】");


        logger.info("机构id为【" + orginId + "】的数据打印完成");
        // 对于指定机构在指定查询日期的返回数据，进行mysql数据库的缓存
        String returnDate = originReturnRecordService.findOriginReturnRecordByOriginId(orginId, currentDateStr);
        if (StringUtils.isBlank(returnDate)) {
            OriginReturnRecord originReturnRecord = new OriginReturnRecord();
            originReturnRecord.setOriginId(orginId);
            originReturnRecord.setReturnDate(currentDateStr);
            originReturnRecord.setReturnJson(JSON.toJSONString(dataListMap));

            //接口返回记录保存mysql，缓存
            originReturnRecordService.insert(originReturnRecord);
        }

        resultMap.put("code", 0);
        resultMap.put("data", dataListMap);
        return resultMap;
    }

    /**
     * 对于appkey集合下的性别、年龄和地域的数据进行封装----假数据
     * @param appKeyByLesseeIdAndAppType        appkey的集合
     * @param dataListMap                       数据封装结果
     */
    private void getGenderAndAgeAndAreaFakeData(Set<String> appKeyByLesseeIdAndAppType, List<Map<String, Object>> dataListMap) {
        if(null != appKeyByLesseeIdAndAppType && appKeyByLesseeIdAndAppType.size() > 0) {
            Map<String, Object> genderMap = new HashMap<String, Object>();
            Map<String, Object> resultGenderMap = new HashMap<String, Object>();
            Map<String, Object> ageMap = new HashMap<String, Object>();
            Long totalUser = getTotalElements(appKeyByLesseeIdAndAppType, null, null);
            Long maleNum = 0l;
            Long femaleNum = 0l;
            if(null != totalUser && totalUser > 0) {
                maleNum = getTotalElements(appKeyByLesseeIdAndAppType, "sex", "男");
                if(maleNum.floatValue() / totalUser.floatValue() >= 0.7f || maleNum.floatValue() / totalUser.floatValue() <= 0.5f) {
                    maleNum = (long)(totalUser * 0.006 * MathUtils.getRandomMinTwoInt(90,105));
                    femaleNum = totalUser - maleNum;
                } else {
                    femaleNum =  totalUser - maleNum;
                }
            }

            List<Map<String, Object>> genderTmpListMap = new ArrayList<Map<String, Object>>();
            genderMap.put("femaleNum", femaleNum);
            genderMap.put("maleNum", maleNum);
            genderTmpListMap.add(genderMap);
            resultGenderMap.put("gender", genderTmpListMap);
            Long youngNum = 0l;
            Long middleNum = 0l;
            Long oldNum = 0l;
            if(null != totalUser && totalUser > 0) {
                youngNum = getTotalElements(appKeyByLesseeIdAndAppType, "age", "青年");
                if(youngNum.floatValue() / totalUser.floatValue() >= 0.7f || youngNum.floatValue() / totalUser.floatValue() <= 0.5f) {
                    youngNum = (long)(totalUser * 0.006 * MathUtils.getRandomMinTwoInt(95,105));
                    middleNum = (long)(totalUser * 0.006 * MathUtils.getRandomMinTwoInt(45,50));
                    if((totalUser - middleNum - youngNum) >= 0) {
                        oldNum = totalUser - middleNum - youngNum;
                    }
                } else {
                    middleNum = (long)(totalUser * 0.006 * MathUtils.getRandomMinTwoInt(45,50));
                    if((totalUser - middleNum - youngNum) >= 0) {
                        oldNum = totalUser - middleNum - youngNum;
                    }
                }
            }
            ageMap.put("youngNum", youngNum);
            ageMap.put("middleNum", middleNum);
            ageMap.put("oldNum", oldNum);
            List<Map<String, Object>> ageTmpListMap = new ArrayList<Map<String, Object>>();
            ageTmpListMap.add(ageMap);
            resultGenderMap.put("age", ageTmpListMap);

            //地域聚合
            //TODO:pcchen 从数据库中读取
//            resultGenderMap.put("region", userAreaCountService.findUserCountInAreaByAppKeySet(appKeyByLesseeIdAndAppType));
            //TODO:pcchen 从es中读取
            List<Map<String, Object>> resultCityRegion = getAggregationsCity(appKeyByLesseeIdAndAppType);
            resultGenderMap.put("region", resultCityRegion);
            dataListMap.add(resultGenderMap);
        }
    }

    /**
     * 对于appkey集合下的性别、年龄和地域的数据进行封装----生产数据
     * @param appKeyByLesseeIdAndAppType        appkey的集合
     * @param dataListMap                       数据封装结果
     */
    private void getGenderAndAgeAndAreaData(Set<String> appKeyByLesseeIdAndAppType, List<Map<String, Object>> dataListMap) {
        if(null != appKeyByLesseeIdAndAppType && appKeyByLesseeIdAndAppType.size() > 0) {
            Map<String, Object> genderMap = new HashMap<String, Object>();
            Map<String, Object> resultGenderMap = new HashMap<String, Object>();
            Map<String, Object> ageMap = new HashMap<String, Object>();
            Long maleNum = getTotalElements(appKeyByLesseeIdAndAppType, "sex", "男");
            Long female = getTotalElements(appKeyByLesseeIdAndAppType, "sex", "女");
            List<Map<String, Object>> genderTmpListMap = new ArrayList<Map<String, Object>>();
            genderMap.put("femaleNum", female);
            genderMap.put("maleNum", maleNum);
            genderTmpListMap.add(genderMap);
            resultGenderMap.put("gender", genderTmpListMap);
            Long young = getTotalElements(appKeyByLesseeIdAndAppType, "age", "青年");
            Long middleNum = getTotalElements(appKeyByLesseeIdAndAppType, "age", "中年");
            Long oldNum = getTotalElements(appKeyByLesseeIdAndAppType, "age", "老年");
            ageMap.put("youngNum", young);
            ageMap.put("middleNum", middleNum);
            ageMap.put("oldNum", oldNum);
            List<Map<String, Object>> ageTmpListMap = new ArrayList<Map<String, Object>>();
            ageTmpListMap.add(ageMap);
            resultGenderMap.put("age", ageTmpListMap);

            //地域聚合
            List<Map<String, Object>> resultCityRegion = getAggregationsCity(appKeyByLesseeIdAndAppType);
            resultGenderMap.put("region", resultCityRegion);
            dataListMap.add(resultGenderMap);
        }
    }

    /**
     * 用户分析接口，计算性别，青老中，前5地域
     *
     * @param orginId 机构id
     * @return
     */
    /**
     * 这里进行标注为异步任务，在执行此方法的时候，会单独开启线程来执行---但是此方法不能再本类调用
     */
    //TODO:pcchen 暂时废弃，由于没有聚合查询从es，而且造假数据，地域数据从mysql中查询，没必要使用定时任务封装数据
    @Async
    public Map<String, Object> getAsyncUserAnalyse(String orginId, Integer sysType) {
        return getUserAnalyse(orginId, sysType);
    }

    /**
     * 对城市进行聚合查询
     *
     * @param atSet      appkey应用标识
     * @return
     */
    public List<Map<String, Object>> getAggregationsCity(Set<String> atSet) {
        List<Map<String, Object>> resultCityRegion = new ArrayList<Map<String, Object>>();

        TransportClient client = null;
        try {
            client = ProfileESClient.getClientIns();
            BoolQueryBuilder builder = QueryBuilders.boolQuery();
            builder.must(atQuery(atSet));
            builder.must(QueryBuilders.rangeQuery("tag_count").gt(0));

            AggregationBuilder agg = AggregationBuilders.terms("City")
                    .field("city").size(0);
            SearchResponse sr = client
                    .prepareSearch(ESConfigConstants.ES_PROFILE_INDEX_OFFLINE_USER_PROFILE)
                    .setTypes(ESConfigConstants.ES_PROFILE_TYPE_USER_TAGS)
                    .setQuery(
                            QueryBuilders.boolQuery()
                                    .filter(builder)
                    ).setSize(0).addAggregation(agg)
                    .execute().actionGet();
            Terms aggTerms = sr.getAggregations().get("City");
            List<Terms.Bucket> tag = aggTerms.getBuckets();


            for (int i = 0; i < tag.size(); i++) {
                Map<String, Object> tmpMap = new HashMap<String, Object>();
                tmpMap.put("regionName", tag.get(i).getKey().toString());
                tmpMap.put("num", tag.get(i).getDocCount());
                resultCityRegion.add(tmpMap);
            }
        } catch (IOException e) {
            logger.error("es配置信息错误");
            e.printStackTrace();
        }
        return resultCityRegion;
    }

    // 同时符合条件
    private QueryBuilder mustQuery(QueryBuilder... querys) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (QueryBuilder query : querys) {
            boolQuery.must(query);
        }
        return boolQuery;
    }
    /**
     * 根据指定appkey以及指定字段查询es中的总数据量
     *
     * @param atSet      appkey应用标识
     * @param queryField 待查询es标识
     * @param queryParam 查询参数值
     * @return
     */
    public Long getTotalElements(Set<String> atSet, String queryField, String queryParam) {
        TransportClient client = null;
        try {
            client = ProfileESClient.getClientIns();
        } catch (IOException e) {
            logger.error("es配置信息错误");
            e.printStackTrace();
            return 0l;
        }
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if(StringUtils.isNotBlank(queryField) && StringUtils.isNotBlank(queryParam)) {
            builder.must(QueryBuilders.termQuery(queryField, queryParam));
        }
        builder.must(atQuery(atSet));
        builder.must(QueryBuilders.rangeQuery("tag_count").gt(0));

        SearchResponse sr = null;
        try {
            sr = client
                    .prepareSearch(ESConfigConstants.ES_PROFILE_INDEX_OFFLINE_USER_PROFILE)
                    .setTypes(ESConfigConstants.ES_PROFILE_TYPE_USER_TAGS)
                    .setQuery(builder)
                    .setSize(0).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return sr.getHits().getTotalHits();
    }

    private QueryBuilder atQuery(Set<String> allAppKeyByMediaId) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        String queryString = StringUtils.join(allAppKeyByMediaId, ";");
        boolQuery.should(QueryBuilders.queryStringQuery("at" + ":" + queryString));
        return boolQuery;
    }

}
