package bigscreen.hubpd.com.service.impl;

import bigscreen.hubpd.com.dto.UserAreaCountDTO;
import bigscreen.hubpd.com.mapper.uar_profile.UarAtAreaMapper;
import bigscreen.hubpd.com.service.UserAreaCountService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 用户地域service
 * @author cpc
 * @create 2019-05-02 16:00
 **/
@Service
@Transactional
public class UserAreaCountServiceImpl implements UserAreaCountService{
    private Logger logger = Logger.getLogger(UserAreaCountServiceImpl.class);

    @Autowired
    private UarAtAreaMapper uarAtAreaMapper;

    /**
     * 查询市级别地域用户数量集合
     * @param appKeySet     应用appKey集合
     * @return
     */
    public List<UserAreaCountDTO> findUserCountInAreaByAppKeySet(Set<String> appKeySet) {
        List<UserAreaCountDTO> userAreaCountDTOList = new ArrayList<UserAreaCountDTO>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("appKeySet", appKeySet);
        userAreaCountDTOList = uarAtAreaMapper.findUserCountInAreaByAppKeySet(paramMap);
        return userAreaCountDTOList;
    }

    /**
     * 查询指定appkey集合下的省或直辖市的用户数
     * @param appKeySet     应用appKey集合
     * @return
     */
    public List<UserAreaCountDTO> findUserCountInProvinceByAppKeySet(Set<String> appKeySet) {
        List<UserAreaCountDTO> userAreaCountDTOList = new ArrayList<UserAreaCountDTO>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("appKeySet", appKeySet);
        userAreaCountDTOList = uarAtAreaMapper.findUserCountInProvinceByAppKeySet(paramMap);
        //对地域信息进行过滤-去掉省、市、自治区、
        return userAreaCountDTOList;
    }
}
