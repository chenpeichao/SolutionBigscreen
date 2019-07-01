package bigscreen.hubpd.com.bean.uar_statistic;

public class AppAtItemHotRankHour extends AppAtItemHotRankHourKey {
    private Long pv;

    private String url;

    public Long getPv() {
        return pv;
    }

    public void setPv(Long pv) {
        this.pv = pv;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }
}