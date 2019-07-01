package bigscreen.hubpd.com.service.impl;

import bigscreen.hubpd.com.dto.AppInfoAppIdAndAppNameDTO;
import bigscreen.hubpd.com.dto.COAppColumnRankDTO;
import bigscreen.hubpd.com.dto.COAppItemRankDTO;
import bigscreen.hubpd.com.mapper.uar_basic.AppColumnMapper;
import bigscreen.hubpd.com.mapper.uar_statistic.AppAtCLHourMapper;
import bigscreen.hubpd.com.mapper.uar_statistic.AppAtItemHotRankHourMapper;
import bigscreen.hubpd.com.service.AppInfoService;
import bigscreen.hubpd.com.service.ContentAnalysisService;
import bigscreen.hubpd.com.service.MediaService;
import bigscreen.hubpd.com.utils.Constants;
import bigscreen.hubpd.com.utils.DateUtils;
import bigscreen.hubpd.com.utils.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 内容分析
 *
 * @author cpc
 * @create 2019-04-09 15:50
 **/
@Service
public class ContentAnalysisServiceImpl implements ContentAnalysisService {
    private Logger logger = Logger.getLogger(ContentAnalysisServiceImpl.class);

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private AppAtCLHourMapper appAtCLHourMapper;
    @Autowired
    private AppAtItemHotRankHourMapper appAtItemHotRankHourMapper;
    @Autowired
    private AppColumnMapper appColumnMapper;
    @Autowired
    private MediaService mediaService;

    /**
     * 根据机构id以及系统标识获取指定机构的天、周、月的频道排行榜topN数据------先根据栏目tag区分，不区分应用和tagName
     * @param lesseeId
     * @param sysType
     * @param timePeriodFlag
     * @param topNum
     * @return
     */
    public Map<String, Object> getChannelRank(String lesseeId, Integer sysType, Integer timePeriodFlag, Integer topNum) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        if(null == mediaService.findMediaByOrgIdAndSystype(lesseeId, sysType)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_NOT_FOUND_MEDIA_ID);
            resultMap.put("message", "系统中不存在指定机构");
            return resultMap;
        }

        List<Map<String, Object>> resultListData = new ArrayList<Map<String, Object>>();
        //首先根据租户id获取应用的appKey集合
        Set<String> appKeyByLesseeIdAndAppType = appInfoService.getAppKeyByLesseeIdAndAppType(lesseeId, 2);

        //TODO:pcchen 频道排行测试环境数据写死
//        if("62521361b706427493611ea51e55ca2d".equals(lesseeId)) {
//            appKeyByLesseeIdAndAppType.add("UAR-000251_967");
//        }
        //查询指定时间间隔的栏目的pv排行
        if(null != appKeyByLesseeIdAndAppType && appKeyByLesseeIdAndAppType.size() > 0) {
            //TODO:pcchen 频道排行中暂时不区分应用内频道，只针对机构内所有频道
            switch (timePeriodFlag) {
                case 1:
                    resultListData = getChannelRankResultMap(appKeyByLesseeIdAndAppType, -24, topNum);
                    break;
                case 2:
                    resultListData = getChannelRankResultMap(appKeyByLesseeIdAndAppType, -168, topNum);
                    break;
                case 3:
                    resultListData = getChannelRankResultMap(appKeyByLesseeIdAndAppType, -720, topNum);
                    break;
                default:
                    break;
            }
        }
        resultMap.put("code", 0);
        resultMap.put("data", resultListData);
        return resultMap;
    }

    /**
     * 根据机构id以及系统标识获取指定机构的天、周、月的热门内容排行榜topN数据
     * @param lesseeId
     * @param sysType
     * @param timePeriodFlag
     * @param topNum
     * @return
     */
    public Map<String, Object> getHotRank(String lesseeId, Integer sysType, Integer timePeriodFlag, Integer topNum) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        if(null == mediaService.findMediaByOrgIdAndSystype(lesseeId, sysType)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_NOT_FOUND_MEDIA_ID);
            resultMap.put("message", "系统中不存在指定机构");
            return resultMap;
        }

        List<Map<String, Object>> resultListData = new ArrayList<Map<String, Object>>();
        //首先根据租户id获取应用的appKey集合
        Set<String> appKeyByLesseeIdAndAppType = appInfoService.getAppKeyByLesseeIdAndAppType(lesseeId, 2);

        //TODO:pcchen 热门内容排行测试环境数据写死
//        if("62521361b706427493611ea51e55ca2d".equals(lesseeId)) {
//            appKeyByLesseeIdAndAppType.add("UAR-000251_651");
//            appKeyByLesseeIdAndAppType.add("UAR-000480_826");
//        }
        //查询指定时间间隔的栏目的pv排行
        if(null != appKeyByLesseeIdAndAppType && appKeyByLesseeIdAndAppType.size() > 0) {
            switch (timePeriodFlag) {
                case 1:
                    resultListData = getHotRankResultMap(appKeyByLesseeIdAndAppType, -24, topNum);
                    break;
                case 2:
                    resultListData = getHotRankResultMap(appKeyByLesseeIdAndAppType, -168, topNum);
                    break;
                case 3:
                    resultListData = getHotRankResultMap(appKeyByLesseeIdAndAppType, -720, topNum);
                    break;
                default:
                    break;
            }
        }
        resultMap.put("code", 0);
        resultMap.put("data", resultListData);
        return resultMap;
    }

    private List<Map<String, Object>> getChannelRankResultMap(Set<String> appKeySet, Integer dayAndHourCount, Integer topNum) {

        String[] startDayAndHourTime = DateUtils.getDayAndHourTimeOffset(dayAndHourCount, "yyyyMMdd HH").split(" ");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startDay", startDayAndHourTime[0]);
        paramMap.put("startHour", startDayAndHourTime[1]);
        paramMap.put("appKeySet", appKeySet);
        //默认查询总记录数的3倍，用于避免有columntag没有对应的columnname的数据进行过滤
        paramMap.put("size", Integer.valueOf(Constants.PARAM_TOPN_MAX_NUM) * 4);

        //接口返回中json中data的封装串
        List<Map<String, Object>> channelNameAndHotNumMapList = new ArrayList<Map<String, Object>>();
        List<COAppColumnRankDTO> pvByAppKeyAndCloumn = appAtCLHourMapper.getPVByAppKeyAndCloumn(paramMap);
        List<COAppColumnRankDTO> pvByAppKeyAndAppIdCloumnList = new ArrayList<COAppColumnRankDTO>();
        //遍历查询应用的appid用于查询栏目的中文标识
        for(COAppColumnRankDTO coAppColumnRankDTO : pvByAppKeyAndCloumn) {
            if(StringUtils.isNotBlank(coAppColumnRankDTO.getAt())) {
                //根据应用appkey查询应用appid
                AppInfoAppIdAndAppNameDTO appInfoAppIdAndAppNameDTO = appInfoService.getAppIdAndAppNameByAppAccount1OrAppAccount2(coAppColumnRankDTO.getAt());
                if(null != appInfoAppIdAndAppNameDTO && StringUtils.isNotBlank(appInfoAppIdAndAppNameDTO.getAppName())) {
                    coAppColumnRankDTO.setAppId(appInfoAppIdAndAppNameDTO.getAppId());
                    coAppColumnRankDTO.setAppName(appInfoAppIdAndAppNameDTO.getAppName());

                    //根据应用appId和栏目tag关联栏目表进行栏目中文名查询
                    Map<String, Object> paramFindColumnNameByAppIdAndColumnTag = new HashMap<String, Object>();

                    paramFindColumnNameByAppIdAndColumnTag.put("columntag", coAppColumnRankDTO.getCl());
                    paramFindColumnNameByAppIdAndColumnTag.put("appid", coAppColumnRankDTO.getAppId());

                    String columnName = appColumnMapper.findColumnNameByAppIdAndColumnTag(paramFindColumnNameByAppIdAndColumnTag);
                    if(StringUtils.isNotBlank(columnName)) {
                        coAppColumnRankDTO.setColumnName(columnName);
                        pvByAppKeyAndAppIdCloumnList.add(coAppColumnRankDTO);
                    }
                }
            }
        }

        //key为appId+ &&& +cl的连接串
        //用于过滤同一应用下多个appkey有多个相同tagId进行pv聚合
        Map<String, COAppColumnRankDTO> groupApp = new HashMap<String, COAppColumnRankDTO>();
        for(COAppColumnRankDTO coAppColumnRankDTO : pvByAppKeyAndAppIdCloumnList) {
            COAppColumnRankDTO coAppColumnRankDTOFromMap = groupApp.get(coAppColumnRankDTO.getAppId() + "&&&" + coAppColumnRankDTO.getCl());
            if(coAppColumnRankDTOFromMap != null) {
                coAppColumnRankDTOFromMap.setPv(coAppColumnRankDTOFromMap.getPv() + coAppColumnRankDTO.getPv());
            } else {
                groupApp.put(coAppColumnRankDTO.getAppId() + "&&&" + coAppColumnRankDTO.getCl(), coAppColumnRankDTO);
            }
        }

        //对不同应用有相同名称的栏目，进行新栏目名(栏目名+应用)的封装
        //栏目名称==》appId的集合--------用于过滤相同栏目名，存在不同应用appId
        Map<String, Set<Integer>> countColumnNameMap = new HashMap<String, Set<Integer>>();
        //遍历上步从数据库中查询的集合数据
        for(Map.Entry entry : groupApp.entrySet()) {
            COAppColumnRankDTO tmp = (COAppColumnRankDTO)entry.getValue();

            if(countColumnNameMap.get(tmp.getColumnName()) != null) {
                if(!countColumnNameMap.get(tmp.getColumnName()).contains(tmp.getAppId())) {
                    countColumnNameMap.get(tmp.getColumnName()).add(tmp.getAppId());
                }
            } else {
                Set<Integer> appIdSet = new HashSet<Integer>();
                appIdSet.add(tmp.getAppId());
                countColumnNameMap.put(tmp.getColumnName(), appIdSet);
            }
        }

        List<COAppColumnRankDTO> newColumnNameCOAppColumnRankDTO = new ArrayList<COAppColumnRankDTO>();
        //遍历groupApp中的从数据库中查询的数据集合，看其中的栏目对应上步中的appId的集合是否有多个
        //有多个，则给其栏目后面添加应用名称，没有多个，则栏目原样显示
        for(Map.Entry entry : groupApp.entrySet()) {
            COAppColumnRankDTO tmp = (COAppColumnRankDTO)entry.getValue();

            Set<Integer> appIds = countColumnNameMap.get(tmp.getColumnName());
            //当此栏目名没有在上步中存在时，则为垃圾数据，按理说此步不可能存在
            if(null == appIds) {
                continue;
            } else {
                if(appIds.size() > 1) {
                    tmp.setColumnName(tmp.getColumnName() + "." + tmp.getAppName());
                }
                newColumnNameCOAppColumnRankDTO.add(tmp);
            }
        }

        Collections.sort(newColumnNameCOAppColumnRankDTO, new Comparator<COAppColumnRankDTO>() {
            @Override
            public int compare(COAppColumnRankDTO o1, COAppColumnRankDTO o2) {
                return o2.getPv().intValue() - o1.getPv().intValue();
            }
        });

        int count = 1;
        for(COAppColumnRankDTO coAppColumnRankDTO : newColumnNameCOAppColumnRankDTO) {
            if(StringUtils.isNotBlank(coAppColumnRankDTO.getColumnName())) {
                Map<String, Object> listMap = new TreeMap<String, Object>();
                listMap.put("channelName", coAppColumnRankDTO.getColumnName());
                listMap.put("hotNum", coAppColumnRankDTO.getPv());
                channelNameAndHotNumMapList.add(listMap);
                if(++count > topNum) {
                    break;
                }
            }
        }
        return channelNameAndHotNumMapList;
    }

    private List<Map<String, Object>> getHotRankResultMap(Set<String> appKeySet, Integer dayAndHourCount, Integer topNum) {

        String[] startDayAndHourTime = DateUtils.getDayAndHourTimeOffset(dayAndHourCount, "yyyyMMdd HH").split(" ");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startDay", startDayAndHourTime[0]);
        paramMap.put("startHour", startDayAndHourTime[1]);
        paramMap.put("appKeySet", appKeySet);
        paramMap.put("size", topNum);

        //接口返回中json中data的封装串
        List<Map<String, Object>> hotRankTitleAndUrlAndPVMapList = new ArrayList<Map<String, Object>>();
        List<COAppItemRankDTO> pvByAppKeyAndUrl = appAtItemHotRankHourMapper.getPVAndUrlAndTitleByAppKeyAndDate(paramMap);
        for(COAppItemRankDTO coAppItemRankDTO : pvByAppKeyAndUrl) {
            if(StringUtils.isNotBlank(coAppItemRankDTO.getUrl()) && StringUtils.isNotBlank(coAppItemRankDTO.getTitle())) {
                Map<String, Object> listMap = new HashMap<String, Object>();
                listMap.put("title", coAppItemRankDTO.getTitle());
                listMap.put("url", coAppItemRankDTO.getUrl());
                listMap.put("hotNum", coAppItemRankDTO.getPv());
                hotRankTitleAndUrlAndPVMapList.add(listMap);
            }
        }
        return hotRankTitleAndUrlAndPVMapList;
    }
}
