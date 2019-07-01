package bigscreen.hubpd.com.bean.uar_statistic;

public class AppAtItemHotRankHourKey {
    private String at;

    private String title;

    private Integer day;

    private Integer hour;

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at == null ? null : at.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }
}