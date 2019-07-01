package bigscreen.hubpd.com.mapper.uar_basic;

import bigscreen.hubpd.com.bean.uar_basic.AppInfo;
import bigscreen.hubpd.com.dto.AppInfoAppIdAndAppNameDTO;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Appinfo;

import java.util.List;
import java.util.Map;

public interface AppInfoMapper {
    int deleteByPrimaryKey(Integer appid);

    int insert(AppInfo record);

    int insertSelective(AppInfo record);

    AppInfo selectByPrimaryKey(Integer appid);

    int updateByPrimaryKeySelective(AppInfo record);

    int updateByPrimaryKeyWithBLOBs(AppInfo record);

    int updateByPrimaryKey(AppInfo record);

    /**
     * 根据租户id查询租户下的所有网站的appkey
     * @param paramMap
     * @return
     */
    public List<AppInfo> getAllAppKeyByOrgIdAndAppType(Map<String, Object> paramMap);

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
     * 根据机构id，查询其下对应的所有网站和移动应用的appaccount
     *
     * @param orgId 机构id
     * @return
     */
    public List<AppInfo> findAppaccountListByOrgId(String orgId);
}