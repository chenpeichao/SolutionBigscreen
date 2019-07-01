package bigscreen.hubpd.com.service;

import java.util.Map;

/**
 * 用户画像service
 *
 * @author cpc
 * @create 2019-04-09 19:29
 **/
public interface UserPortraitService {

    /**
     * 用户地域接口，获取各省（topN）分布用户数
     *
     * @param orginId 机构id
     * @return
     */
    public Map<String, Object> findUserCountInProvince(String orginId, Integer sysType, Integer topN);

    /**
     * 用户分析接口，计算性别，青老中，前5地域
     *
     * @param orginId 机构id
     * @return
     */
    public Map<String, Object> getUserAnalyse(String orginId, Integer sysType);
    /**
     * 用户分析接口，计算性别，青老中，前5地域
     *
     * @param orginId 机构id
     * @return
     */
    /**
     * 这里进行标注为异步任务，在执行此方法的时候，会单独开启线程来执行---但是此方法不能再本类调用
     */
    public Map<String, Object> getAsyncUserAnalyse(String orginId, Integer sysType);
}
