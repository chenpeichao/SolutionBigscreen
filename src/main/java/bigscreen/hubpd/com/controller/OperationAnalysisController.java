package bigscreen.hubpd.com.controller;

import bigscreen.hubpd.com.service.OperationAnalysisService;
import bigscreen.hubpd.com.utils.Constants;
import bigscreen.hubpd.com.utils.ErrorCode;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 运营分析接口
 *
 * @author cpc
 * @create 2019-04-08 10:37
 **/
@Controller
@RequestMapping(value="/bigscreen/operationAnalysis")
public class OperationAnalysisController {
    private Logger logger = Logger.getLogger(OperationAnalysisController.class);

    @Autowired
    private OperationAnalysisService operationAnalysisService;

    /**
     * 获取微信7天、7周、7月的阅读数和点赞数
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/lineChartData/wechat")
    public Map<String, Object> getWXSevenDWMReadAndLikeData(HttpServletRequest request, HttpServletResponse response){
        // 解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> resultMap = new HashMap<String, Object>();

        logger.info("request param 【"+request.getQueryString()+"】");

        String lesseeIdStr = StringUtils.isNotBlank(request.getParameter("lesseeId"))?request.getParameter("lesseeId").trim():request.getParameter("lesseeId");
        String sysTypeStr = StringUtils.isNotBlank(request.getParameter("sysType"))?request.getParameter("sysType").trim():request.getParameter("sysType");
        String timePeriodFlagStr = StringUtils.isNotBlank(request.getParameter("timePeriodFlag"))?request.getParameter("timePeriodFlag").trim():request.getParameter("timePeriodFlag");

        Integer sysType = Constants.SYS_TYPE_DEFAULT;
        Integer timePeriodFlag = 0;

        if(StringUtils.isBlank(lesseeIdStr)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_NOT_FOUND);
            resultMap.put("message", "request param lesseeId lack");
            return resultMap;
        }
        //sysType为传递时，默认值设置为2(表示：甘肃)
        if(StringUtils.isNotBlank(sysTypeStr)) {
            try {
                sysType = Integer.parseInt(sysTypeStr);
            } catch (NumberFormatException e) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
                resultMap.put("message", "request param sysType value is wrong");
                return resultMap;
            }
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
            return operationAnalysisService.getWXSevenDWMReadAndLikeData(lesseeIdStr, sysType, timePeriodFlag);
        } catch (Exception e) {
            logger.error("getWXUserAnalyse微信运营接口调用失败-发生未知错误", e);
            resultMap.put("code", 1000);
            resultMap.put("message", "接口调用失败，请重试");
            return resultMap;
        }
    }

    /**
     * 获取网站7天、7周、7月的浏览量和访问量
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/lineChartData/web")
    public Map<String, Object> getWebSevenYMDPVAndUVData(HttpServletRequest request, HttpServletResponse response){
        // 解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> resultMap = new HashMap<String, Object>();

        logger.info("request param 【"+request.getQueryString()+"】");

        String lesseeIdStr = StringUtils.isNotBlank(request.getParameter("lesseeId"))?request.getParameter("lesseeId").trim():request.getParameter("lesseeId");
        String sysTypeStr = StringUtils.isNotBlank(request.getParameter("sysType"))?request.getParameter("sysType").trim():request.getParameter("sysType");
        String timePeriodFlagStr = StringUtils.isNotBlank(request.getParameter("timePeriodFlag"))?request.getParameter("timePeriodFlag").trim():request.getParameter("timePeriodFlag");

        Integer sysType = Constants.SYS_TYPE_DEFAULT;
        Integer timePeriodFlag = 0;

        if(StringUtils.isBlank(lesseeIdStr)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_NOT_FOUND);
            resultMap.put("message", "request param lesseeId lack");
            return resultMap;
        }
        //sysType为传递时，默认值设置为2(表示：甘肃)
        if(StringUtils.isNotBlank(sysTypeStr)) {
            try {
                sysType = Integer.parseInt(sysTypeStr);
            } catch (NumberFormatException e) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
                resultMap.put("message", "request param sysType value is wrong");
                return resultMap;
            }
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
            return operationAnalysisService.getWebSevenYMDPVAndUVData(lesseeIdStr, sysType, timePeriodFlag);
        } catch (Exception e) {
            logger.error("getWXUserAnalyse微信运营接口调用失败-发生未知错误", e);
            resultMap.put("code", 1000);
            resultMap.put("message", "接口调用失败，请重试");
            return resultMap;
        }
    }

    /**
     * 获取客户端7天、7周、7月的浏览量和访问量
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/lineChartData/app")
    public Map<String, Object> getAppSevenYMDPVAndUVData(HttpServletRequest request, HttpServletResponse response){
        // 解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> resultMap = new HashMap<String, Object>();

        logger.info("request param 【"+request.getQueryString()+"】");

        String lesseeIdStr = StringUtils.isNotBlank(request.getParameter("lesseeId"))?request.getParameter("lesseeId").trim():request.getParameter("lesseeId");
        String sysTypeStr = StringUtils.isNotBlank(request.getParameter("sysType"))?request.getParameter("sysType").trim():request.getParameter("sysType");
        String timePeriodFlagStr = StringUtils.isNotBlank(request.getParameter("timePeriodFlag"))?request.getParameter("timePeriodFlag").trim():request.getParameter("timePeriodFlag");

        Integer sysType = Constants.SYS_TYPE_DEFAULT;
        Integer timePeriodFlag = 0;

        if(StringUtils.isBlank(lesseeIdStr)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_NOT_FOUND);
            resultMap.put("message", "request param lesseeId lack");
            return resultMap;
        }
        //sysType为传递时，默认值设置为2(表示：甘肃)
        if(StringUtils.isNotBlank(sysTypeStr)) {
            try {
                sysType = Integer.parseInt(sysTypeStr);
            } catch (NumberFormatException e) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
                resultMap.put("message", "request param sysType value is wrong");
                return resultMap;
            }
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
            return operationAnalysisService.getAppSevenYMDPVAndUVData(lesseeIdStr, sysType, timePeriodFlag);
        } catch (Exception e) {
            logger.error("getWXUserAnalyse微信运营接口调用失败-发生未知错误", e);
            resultMap.put("code", 1000);
            resultMap.put("message", "接口调用失败，请重试");
            return resultMap;
        }
    }

    /**
     * 获取网站、客户端、微博、微信topN文章
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/topNArticle")
    public Map<String, Object> topNArticle(HttpServletRequest request, HttpServletResponse response){
        // 解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> resultMap = new HashMap<String, Object>();

        logger.info("request param 【"+request.getQueryString()+"】");

        String lesseeIdStr = StringUtils.isNotBlank(request.getParameter("lesseeId"))?request.getParameter("lesseeId").trim():request.getParameter("lesseeId");
        String sysTypeStr = StringUtils.isNotBlank(request.getParameter("sysType"))?request.getParameter("sysType").trim():request.getParameter("sysType");
        String timePeriodFlagStr = StringUtils.isNotBlank(request.getParameter("timePeriodFlag"))?request.getParameter("timePeriodFlag").trim():request.getParameter("timePeriodFlag");
        String appFlagStr = StringUtils.isNotBlank(request.getParameter("appFlag"))?request.getParameter("appFlag").trim():request.getParameter("appFlag");
        //topNum未传递默认为5，并且只能小于等于30
        String topNumStr = StringUtils.isNotBlank(request.getParameter("topNum"))?request.getParameter("topNum").trim():Constants.PARAM_TOPN_DEFAULT_NUM;

        Integer sysType = Constants.SYS_TYPE_DEFAULT;
        Integer appFlag = 1;
        Integer topNum = 30;
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
        //appFlag未传递时，以及格式不为1,2,3
        Pattern appFlagPattern = Pattern.compile("^["+ Constants.PARAM_APP_FLAG_ALL+"]$");
        if(StringUtils.isBlank(appFlagStr)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_NOT_FOUND);
            resultMap.put("message", "request param appFlag lack");
            return resultMap;
        } else if(!appFlagPattern.matcher(appFlagStr).matches()) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
            resultMap.put("message", "request param appFlag value is wrong");
            return resultMap;
        } else {
            try {
                appFlag = Integer.parseInt(appFlagStr);
            } catch (NumberFormatException e) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
                resultMap.put("message", "request param appFlag value is wrong");
                return resultMap;
            }
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
            return operationAnalysisService.getTopNArticleInAppFlag(lesseeIdStr, sysType, timePeriodFlag, appFlag, topNum);
        } catch (Exception e) {
            logger.error("getWXUserAnalyse微信运营接口调用失败-发生未知错误", e);
            resultMap.put("code", 1000);
            resultMap.put("message", "接口调用失败，请重试");
            return resultMap;
        }
    }
}
