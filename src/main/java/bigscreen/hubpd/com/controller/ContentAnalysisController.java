package bigscreen.hubpd.com.controller;

import bigscreen.hubpd.com.service.ContentAnalysisService;
import bigscreen.hubpd.com.utils.Constants;
import bigscreen.hubpd.com.utils.ErrorCode;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 内容分析
 *
 * @author cpc
 * @create 2019-04-09 15:46
 **/
@Controller
@RequestMapping(value="/bigscreen/appContentAnalysis")
public class ContentAnalysisController {
    private Logger logger = Logger.getLogger(ContentAnalysisController.class);

    @Autowired
    private ContentAnalysisService contentAnalysisService;

    /**
     * 获取客户端频道排行
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/channelRank")
    public Map<String, Object> channelRank(HttpServletRequest request, HttpServletResponse response){
        // 解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> resultMap = new HashMap<String, Object>();

        logger.info("request param 【"+request.getQueryString()+"】");

        String lesseeIdStr = StringUtils.isNotBlank(request.getParameter("lesseeId"))?request.getParameter("lesseeId").trim():request.getParameter("lesseeId");
        String sysTypeStr = StringUtils.isNotBlank(request.getParameter("sysType"))?request.getParameter("sysType").trim():request.getParameter("sysType");
        String timePeriodFlagStr = StringUtils.isNotBlank(request.getParameter("timePeriodFlag"))?request.getParameter("timePeriodFlag").trim():request.getParameter("timePeriodFlag");
        //topNum未传递默认为5，并且只能小于等于30
        String topNumStr = StringUtils.isNotBlank(request.getParameter("topNum"))?request.getParameter("topNum").trim(): Constants.PARAM_TOPN_DEFAULT_NUM;

        Integer sysType = Constants.SYS_TYPE_DEFAULT;
        Integer topNum = 0;
        Integer timePeriodFlag = 0;

        if(StringUtils.isBlank(lesseeIdStr)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_NOT_FOUND);
            resultMap.put("message", "request param lesseeId lack");
            return resultMap;
        }
        //sysType未传递时，默认值设置为2(表示：甘肃)
        if(StringUtils.isNotBlank(sysTypeStr)) {
            try {
                sysType = Integer.parseInt(sysTypeStr);
            } catch (NumberFormatException e) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
                resultMap.put("message", "request param sysType value is wrong");
                return resultMap;
            }
        }

        //topNum处理
        try {
            if(Integer.parseInt(Constants.PARAM_TOPN_MAX_NUM) < Integer.parseInt(topNumStr) || Integer.parseInt(topNumStr) < 1) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_MORE_THAN_DEFAULT);
                resultMap.put("message", "request param topNum value more than default ");
                return resultMap;
            } else {
                topNum = Integer.parseInt(topNumStr);
            }
        } catch (NumberFormatException e) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
            resultMap.put("message", "request param topNum value is wrong");
            return resultMap;
        }

        //对时间维度周期进行过滤
        Pattern timePeriodFlagPattern = Pattern.compile("^["+ Constants.PARAM_TIME_PERIOD_ALL+"]$");
        if(StringUtils.isBlank(timePeriodFlagStr)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_NOT_FOUND);
            resultMap.put("message", "request param timePeriodFlag lack");
            return resultMap;
        } else if(!timePeriodFlagPattern.matcher(timePeriodFlagStr).matches()) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
            resultMap.put("message", "request param timePeriodFlag value is wrong");
            return resultMap;
        } else {
            try {
                timePeriodFlag = Integer.parseInt(timePeriodFlagStr);
            } catch (NumberFormatException e) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
                resultMap.put("message", "request param timePeriodFlag value is wrong");
                return resultMap;
            }
        }

        try {
            return contentAnalysisService.getChannelRank(lesseeIdStr, sysType, timePeriodFlag, topNum);
        } catch (Exception e) {
            logger.error("channelRank频道排行接口调用失败-发生未知错误", e);
            resultMap.put("code", 1000);
            resultMap.put("message", "接口调用失败，请重试");
            return resultMap;
        }
    }

    /**
     * 获取内容热度排行
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/hotRank")
    public Map<String, Object> hotRank(HttpServletRequest request, HttpServletResponse response){
        // 解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> resultMap = new HashMap<String, Object>();

        logger.info("request param 【"+request.getQueryString()+"】");

        String lesseeIdStr = StringUtils.isNotBlank(request.getParameter("lesseeId"))?request.getParameter("lesseeId").trim():request.getParameter("lesseeId");
        String sysTypeStr = StringUtils.isNotBlank(request.getParameter("sysType"))?request.getParameter("sysType").trim():request.getParameter("sysType");
        String timePeriodFlagStr = StringUtils.isNotBlank(request.getParameter("timePeriodFlag"))?request.getParameter("timePeriodFlag").trim():request.getParameter("timePeriodFlag");
        //topNum未传递默认为5，并且只能小于等于30
        String topNumStr = StringUtils.isNotBlank(request.getParameter("topNum"))?request.getParameter("topNum").trim(): Constants.PARAM_TOPN_DEFAULT_NUM;

        Integer sysType = Constants.SYS_TYPE_DEFAULT;
        Integer topNum = 0;
        Integer timePeriodFlag = 0;

        if(StringUtils.isBlank(lesseeIdStr)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_NOT_FOUND);
            resultMap.put("message", "request param lesseeId lack");
            return resultMap;
        }
        //sysType未传递时，默认值设置为2(表示：甘肃)
        if(StringUtils.isNotBlank(sysTypeStr)) {
            try {
                sysType = Integer.parseInt(sysTypeStr);
            } catch (NumberFormatException e) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
                resultMap.put("message", "request param sysType value is wrong");
                return resultMap;
            }
        }

        //topNum处理
        try {
            if(Integer.parseInt(Constants.PARAM_TOPN_MAX_NUM) < Integer.parseInt(topNumStr) || Integer.parseInt(topNumStr) < 1) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_MORE_THAN_DEFAULT);
                resultMap.put("message", "request param topNum value more than default ");
                return resultMap;
            } else {
                topNum = Integer.parseInt(topNumStr);
            }
        } catch (NumberFormatException e) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
            resultMap.put("message", "request param topNum value is wrong");
            return resultMap;
        }

        //对时间维度周期进行过滤
        Pattern timePeriodFlagPattern = Pattern.compile("^["+ Constants.PARAM_TIME_PERIOD_ALL+"]$");
        if(StringUtils.isBlank(timePeriodFlagStr)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_NOT_FOUND);
            resultMap.put("message", "request param timePeriodFlag lack");
            return resultMap;
        } else if(!timePeriodFlagPattern.matcher(timePeriodFlagStr).matches()) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
            resultMap.put("message", "request param timePeriodFlag value is wrong");
            return resultMap;
        } else {
            try {
                timePeriodFlag = Integer.parseInt(timePeriodFlagStr);
            } catch (NumberFormatException e) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
                resultMap.put("message", "request param timePeriodFlag value is wrong");
                return resultMap;
            }
        }

        try {
            return contentAnalysisService.getHotRank(lesseeIdStr, sysType, timePeriodFlag, topNum);
        } catch (Exception e) {
            logger.error("hotRank热门内容接口调用失败-发生未知错误", e);
            resultMap.put("code", 1000);
            resultMap.put("message", "接口调用失败，请重试");
            return resultMap;
        }
    }
}