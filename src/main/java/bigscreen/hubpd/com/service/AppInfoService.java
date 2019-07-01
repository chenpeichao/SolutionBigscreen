package bigscreen.hubpd.com.service;

import bigscreen.hubpd.com.dto.AppInfoAppIdAndAppNameDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * uar_basic库中t_appinfo表操作
 *
 * @author cpc
 * @create 2019-04-09 16:55
 **/
public interface AppInfoService {
    /**
     * 获取指定租户下网站或app的所有应用appkey
     * @param lesseeId      租户id
     * @param appType       应用标识1：网站；2：客户端
     * @return
     */
    public Set<String> getAppKeyByLesseeIdAndAppType(String lesseeId, Integer appType);

    /**
     * 获取指定appkey的应用appId
     * @param appAccount
     * @return
     */
    public Integer getAppIdByAppAccount1OrAppAccount2(String appAccount);

    /**
     * 获取指定appkey的应用appId和appName
     * @param appAccount
     * @return
     */
    public AppInfoAppIdAndAppNameDTO getAppIdAndAppNameByAppAccount1OrAppAccount2(String appAccount);

    /**
     * 根据机构id，查询其下对应的所有网站和移动应用的appaccount(即应用标识at)（返回结果为map<应用中文名，(应用appaccount，当为移动应用时为，Android和ios的appkey)>）
     *
     * @param orgId 机构id
     * @return
     */
    public Map<String, List<String>> findAppaccountListByOrgId(String orgId);
}
