package bigscreen.hubpd.com.dto;

import java.io.Serializable;

/**
 * 内容分析中的栏目排行DTO
 *
 * @author cpc
 * @create 2019-04-09 17:35
 **/
public class COAppColumnRankDTO implements Serializable {
    private Integer appId;
    private String appName;
    private String at;
    private String cl;      //栏目tag或app_at_cl_hour中的cl字段
    private Long pv;
    private String columnName;

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

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getCl() {
        return cl;
    }

    public void setCl(String cl) {
        this.cl = cl;
    }

    public Long getPv() {
        return pv;
    }

    public void setPv(Long pv) {
        this.pv = pv;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        COAppColumnRankDTO that = (COAppColumnRankDTO) o;

        if (appId != null ? !appId.equals(that.appId) : that.appId != null) return false;
        if (appName != null ? !appName.equals(that.appName) : that.appName != null) return false;
        if (at != null ? !at.equals(that.at) : that.at != null) return false;
        if (cl != null ? !cl.equals(that.cl) : that.cl != null) return false;
        if (pv != null ? !pv.equals(that.pv) : that.pv != null) return false;
        return columnName != null ? columnName.equals(that.columnName) : that.columnName == null;

    }

    @Override
    public int hashCode() {
        int result = appId != null ? appId.hashCode() : 0;
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + (at != null ? at.hashCode() : 0);
        result = 31 * result + (cl != null ? cl.hashCode() : 0);
        result = 31 * result + (pv != null ? pv.hashCode() : 0);
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "COAppColumnRankDTO{" +
                "appId=" + appId +
                ", appName=" + appName +
                ", at='" + at + '\'' +
                ", cl='" + cl + '\'' +
                ", pv=" + pv +
                ", columnName='" + columnName + '\'' +
                '}';
    }
}
