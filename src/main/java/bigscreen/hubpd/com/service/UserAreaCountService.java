package bigscreen.hubpd.com.service;

import bigscreen.hubpd.com.dto.UserAreaCountDTO;

import java.util.List;
import java.util.Set;

/**
 * 用户地域service
 *
 * @author cpc
 * @create 2019-05-02 15:58
 **/
public interface UserAreaCountService {

    /**
     * 查询市级别地域用户数量集合
     * @param appKeySet     应用appKey集合
     * @return
     */
    public List<UserAreaCountDTO> findUserCountInAreaByAppKeySet(Set<String> appKeySet);

    /**
     * 查询指定appkey集合下的省或直辖市的用户数
     * @param appKeySet     应用appKey集合
     * @return
     */
    public List<UserAreaCountDTO> findUserCountInProvinceByAppKeySet(Set<String> appKeySet);
}
