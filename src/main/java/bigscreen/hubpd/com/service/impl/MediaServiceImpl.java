package bigscreen.hubpd.com.service.impl;

import bigscreen.hubpd.com.bean.uar_basic.Media;
import bigscreen.hubpd.com.mapper.uar_basic.MediaMapper;
import bigscreen.hubpd.com.service.MediaService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * uar机构service
 *
 * @author cpc
 * @create 2019-04-09 19:23
 **/
@Service
public class MediaServiceImpl implements MediaService {
    private Logger logger = Logger.getLogger(MediaServiceImpl.class);

    @Autowired
    private MediaMapper mediaMapper;

    /**
     * 查询指定系统类型的机构id集合
     * @param sysType       系统类型1：uar；2：甘肃
     * @return
     */
    public Set<String> findAllOriginIdListInBigscreen(Integer sysType) {
        return mediaMapper.findAllOriginIdListInBigscreen(sysType);
    }

    /**
     * 根据系统类型和机构id查询是否存在此机构
     * @param orgId
     * @param sysType
     * @return
     */
    public Media findMediaByOrgIdAndSystype(String orgId, Integer sysType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orgId", orgId);
        paramMap.put("sysType", sysType);
        return mediaMapper.findMediaByOrgIdAndSystype(paramMap);
    }
}
