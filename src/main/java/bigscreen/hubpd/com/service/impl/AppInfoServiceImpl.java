package bigscreen.hubpd.com.service.impl;

import bigscreen.hubpd.com.bean.uar_basic.AppInfo;
import bigscreen.hubpd.com.dto.AppInfoAppIdAndAppNameDTO;
import bigscreen.hubpd.com.mapper.uar_basic.AppInfoMapper;
import bigscreen.hubpd.com.service.AppInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * uar_basic库中t_appinfo表操作
 * @author cpc
 * @create 2019-04-09 16:56
 **/
@Service
public class AppInfoServiceImpl implements AppInfoService {
    private Logger logger = Logger.getLogger(AppInfoServiceImpl.class);

    @Autowired
    private AppInfoMapper appInfoMapper;

    /**
     * 获取指定租户下网站或app的所有应用appkey
     * @param lesseeId      租户id
     * @param appType       应用标识1：网站；2：客户端;当为null或""时查询网站和app的所有
     * @return
     */
    public Set<String> getAppKeyByLesseeIdAndAppType(String lesseeId, Integer appType) {
        Set<String> appKeySet = new HashSet<String>();

        //1、根据租户id查询租户下的所有网站的appkey
        Map<String, Object> getAllAppKeyByOrgIdAndAppTypeParamMap = new HashMap<String, Object>();
        getAllAppKeyByOrgIdAndAppTypeParamMap.put("lesseeId", lesseeId);
        getAllAppKeyByOrgIdAndAppTypeParamMap.put("appType", appType);
        List<AppInfo> allAppKeyByOrgIdAndAppType = appInfoMapper.getAllAppKeyByOrgIdAndAppType(getAllAppKeyByOrgIdAndAppTypeParamMap);

        for(AppInfo appInfo : allAppKeyByOrgIdAndAppType) {
            if(StringUtils.isNotBlank(appInfo.getAppaccount())) {
                appKeySet.add(appInfo.getAppaccount());
            }
            if(StringUtils.isNotBlank(appInfo.getAppaccount2())) {
                appKeySet.add(appInfo.getAppaccount2());
            }
        }

        return appKeySet;
    }

    /**
     * 获取指定appkey的应用appId
     * @param appAccount
     * @return
     */
    public Integer getAppIdByAppAccount1OrAppAccount2(String appAccount) {
        return appInfoMapper.getAppIdByAppAccount1OrAppAccount2(appAccount);
    }
    /**
     * 获取指定appkey的应用appId和appName
     * @param appAccount
     * @return
     */
    public AppInfoAppIdAndAppNameDTO getAppIdAndAppNameByAppAccount1OrAppAccount2(String appAccount) {
        return appInfoMapper.getAppIdAndAppNameByAppAccount1OrAppAccount2(appAccount);
    }

    /**
     * 根据机构id，查询其下对应的所有网站和移动应用的appaccount(即应用标识at)（返回结果为map<应用中文名，(应用appaccount，当为移动应用时为，Android和ios的appkey)>）
     *
     * @param orgId 机构id
     * @return
     */
    public Map<String, List<String>> findAppaccountListByOrgId(String orgId) {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        List<AppInfo> appaccountListByUserBasicUserIdList = appInfoMapper.findAppaccountListByOrgId(orgId);
        for (AppInfo appInfo : appaccountListByUserBasicUserIdList) {
            List<String> resultAppaccountList = new ArrayList<String>();
            //因为当为移动应用时，可能存在Android和ios的区别，有两个appaccount，所以用list封装
            if (StringUtils.isNotBlank(appInfo.getAppaccount())) {
                resultAppaccountList.add(appInfo.getAppaccount());
            }
            if (StringUtils.isNotBlank(appInfo.getAppaccount2())) {
                resultAppaccountList.add(appInfo.getAppaccount2());
            }
            result.put(appInfo.getAppname(), resultAppaccountList);
        }
        return result;
    }
}
