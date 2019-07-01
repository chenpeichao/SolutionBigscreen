package bigscreen.hubpd.com.bean.uar_basic;

import java.util.Date;

public class AppColumn {
    private Integer columnid;

    private Integer appid;

    private Integer parentid;

    private Integer columnlevel;

    private String columnname;

    private String columntag;

    private String columntype;

    private Byte matchtype;

    private String columnpath;

    private Date atime;

    private String aid;

    private Date mtime;

    private String mid;

    public Integer getColumnid() {
        return columnid;
    }

    public void setColumnid(Integer columnid) {
        this.columnid = columnid;
    }

    public Integer getAppid() {
        return appid;
    }

    public void setAppid(Integer appid) {
        this.appid = appid;
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    public Integer getColumnlevel() {
        return columnlevel;
    }

    public void setColumnlevel(Integer columnlevel) {
        this.columnlevel = columnlevel;
    }

    public String getColumnname() {
        return columnname;
    }

    public void setColumnname(String columnname) {
        this.columnname = columnname == null ? null : columnname.trim();
    }

    public String getColumntag() {
        return columntag;
    }

    public void setColumntag(String columntag) {
        this.columntag = columntag == null ? null : columntag.trim();
    }

    public String getColumntype() {
        return columntype;
    }

    public void setColumntype(String columntype) {
        this.columntype = columntype == null ? null : columntype.trim();
    }

    public Byte getMatchtype() {
        return matchtype;
    }

    public void setMatchtype(Byte matchtype) {
        this.matchtype = matchtype;
    }

    public String getColumnpath() {
        return columnpath;
    }

    public void setColumnpath(String columnpath) {
        this.columnpath = columnpath == null ? null : columnpath.trim();
    }

    public Date getAtime() {
        return atime;
    }

    public void setAtime(Date atime) {
        this.atime = atime;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid == null ? null : aid.trim();
    }

    public Date getMtime() {
        return mtime;
    }

    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid == null ? null : mid.trim();
    }
}