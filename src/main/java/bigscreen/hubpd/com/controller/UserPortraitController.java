package bigscreen.hubpd.com.controller;

import bigscreen.hubpd.com.service.UserPortraitService;
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

/**
 * 用户画像controller
 *
 * @author cpc
 * @create 2019-04-09 21:46
 **/
@Controller
@RequestMapping(value="/bigscreen")
public class UserPortraitController {
    private Logger logger = Logger.getLogger(UserPortraitController.class);

    @Autowired
    private UserPortraitService userPortraitService;

    /**
     * 用户地域接口，获取各省（topN）分布用户数
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/findUserCountInProvince")
    public Map<String, Object> findUserCountInProvince(HttpServletRequest request, HttpServletResponse response) {
        // 解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> resultMap = new HashMap<String, Object>();

        logger.info("request param 【"+request.getQueryString()+"】");

        String lesseeIdStr = StringUtils.isNotBlank(request.getParameter("lesseeId"))?request.getParameter("lesseeId").trim():request.getParameter("lesseeId");
        String sysTypeStr = StringUtils.isNotBlank(request.getParameter("sysType"))?request.getParameter("sysType").trim():request.getParameter("sysType");
        String topNStr = StringUtils.isNotBlank(request.getParameter("topN"))?request.getParameter("topN").trim():"30";

        if(StringUtils.isBlank(lesseeIdStr)) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_NOT_FOUND);
            resultMap.put("message", "request param lesseeId lack");
            return resultMap;
        }

        Integer sysType = 1;
        //sysType未传递时，默认值设置为1(表示：非甘肃省平台)
        if(StringUtils.isNotBlank(sysTypeStr)) {
            try {
                sysType = Integer.parseInt(sysTypeStr);
            } catch (NumberFormatException e) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
                resultMap.put("message", "request param sysType value is wrong");
                return resultMap;
            }
        }

        //当不传递topN参数时，默认为-1，获取所有记录
        Integer topN = 0;
        //获取topN--默认获取所有
        if(StringUtils.isNotBlank(topNStr)) {
            try {
                topN = Integer.parseInt(topNStr);
            } catch (NumberFormatException e) {
                resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
                resultMap.put("message", "request param topN pattern is wrong");
                return resultMap;
            }
        }

        if(topN < 0) {
            resultMap.put("code", ErrorCode.ERROR_CODE_PARAM_ERROR_PATTERN);
            resultMap.put("message", "request param topN must more than 0");
            return resultMap;
        }
        if(topN > 30) {
            topN = 30;
        }

        return userPortraitService.findUserCountInProvince(lesseeIdStr, sysType, topN);
    }

    /**
     * 用户画像
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/userPortrait")
    public Map<String, Object> userPortrait(HttpServletRequest request, HttpServletResponse response){
        // 解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> resultMap = new HashMap<String, Object>();

        logger.info("request param 【"+request.getQueryString()+"】");

        String lesseeIdStr = StringUtils.isNotBlank(request.getParameter("lesseeId"))?request.getParameter("lesseeId").trim():request.getParameter("lesseeId");
        String sysTypeStr = StringUtils.isNotBlank(request.getParameter("sysType"))?request.getParameter("sysType").trim():request.getParameter("sysType");

        Integer sysType = Constants.SYS_TYPE_DEFAULT;

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

        try {
            return userPortraitService.getUserAnalyse(lesseeIdStr, sysType);
        } catch (Exception e) {
            logger.error("channelRank频道排行接口调用失败-发生未知错误", e);
            resultMap.put("code", 1000);
            resultMap.put("message", "接口调用失败，请重试");
            return resultMap;
        }
    }
}
