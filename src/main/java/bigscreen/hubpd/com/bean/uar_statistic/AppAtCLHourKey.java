package bigscreen.hubpd.com.bean.uar_statistic;

public class AppAtCLHourKey {
    private String at;

    private String cl;

    private Integer day;

    private Integer hour;

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at == null ? null : at.trim();
    }

    public String getCl() {
        return cl;
    }

    public void setCl(String cl) {
        this.cl = cl == null ? null : cl.trim();
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