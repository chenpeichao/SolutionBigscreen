package bigscreen.hubpd.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 程序启动类
 *
 * @author cpc
 * @create 2019-04-08 10:34
 **/
@EnableAutoConfiguration
@SpringBootApplication
@EnableScheduling
@EnableAsync
@PropertySource(value = {"classpath:config/constant.properties", "classpath:config/errorCode.properties" , "classpath:config/esconstants.properties"},encoding="utf-8")
public class SolutionBigscreenApplication {
    public static void main(String[] args) {
        SpringApplication.run(SolutionBigscreenApplication.class, args);
    }
}