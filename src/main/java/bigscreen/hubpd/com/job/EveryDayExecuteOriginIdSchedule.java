package bigscreen.hubpd.com.job;

import bigscreen.hubpd.com.service.MediaService;
import bigscreen.hubpd.com.service.UserPortraitService;
import bigscreen.hubpd.com.utils.Constants;
import bigscreen.hubpd.com.utils.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 每天5点定时缓存所有有效机构的用户分析信息到mysql数据库
 *
 * @author cpc
 * @create 2018-08-15 20:06
 **/
@Component
public class EveryDayExecuteOriginIdSchedule {
    private Logger logger = Logger.getLogger(EveryDayExecuteOriginIdSchedule.class);

    @Autowired
    private MediaService mediaService;
    @Autowired
    private UserPortraitService userPortraitService;

    //TODO:pcchen 由于数据造假，所以没有聚合任务查询画像es，只是查询totalHits，所以没必要用定时任务缓存数据
//    @Scheduled(fixedRate = 1000 * 60 * 50)
    @Scheduled(cron = "0 0 5 * * ?")
    public void addTask() {
        //查询系统中所有的甘肃机构
        Set<String> allOriginIdList = mediaService.findAllOriginIdListInBigscreen(Constants.SYS_TYPE_DEFAULT);

        logger.info("定时任务缓存根据机构id缓存用户分析指定机构当天数据");

        for (String originId : allOriginIdList) {
            // 对用户分析接口进行调用，目的为了缓存当天接口返回值到mysql数据库
            try {
                Map<String, Object> userAnalyse = userPortraitService.getAsyncUserAnalyse(originId, null);
                if (userAnalyse != null) {
                    logger.info("缓存根据机构id【" + originId + "】用户分析指定机构【" + DateUtils.getDateStrByDate(new Date(), "yyyy-MM-dd") + "】数据成功！");
                }
            } catch (Exception e) {
                logger.error("缓存根据机构id【" + originId + "】用户分析指定机构【" + DateUtils.getDateStrByDate(new Date(), "yyyy-MM-dd") + "】数据失败！", e);
            }
        }
    }
}
