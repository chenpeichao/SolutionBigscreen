package bigscreen.hubpd.com.dto;

/**
 * 应用的appid和appName类
 *
 * @author cpc
 * @create 2019-05-01 15:57
 **/
public class AppInfoAppIdAndAppNameDTO {
    private Integer appId;
    private String appName;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppInfoAppIdAndAppNameDTO that = (AppInfoAppIdAndAppNameDTO) o;

        if (appId != null ? !appId.equals(that.appId) : that.appId != null) return false;
        return appName != null ? appName.equals(that.appName) : that.appName == null;

    }

    @Override
    public int hashCode() {
        int result = appId != null ? appId.hashCode() : 0;
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AppInfoAppIdAndAppNameDTO{" +
                "appId=" + appId +
                ", appName='" + appName + '\'' +
                '}';
    }
}
