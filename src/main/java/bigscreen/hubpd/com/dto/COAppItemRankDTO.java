package bigscreen.hubpd.com.dto;

import java.io.Serializable;

/**
 * 内容分析中的热门内容排行DTO
 *
 * @author cpc
 * @create 2019-04-11 10:46
 **/
public class COAppItemRankDTO implements Serializable {
//    title, url, SUM(pv) pv
    private String title;
    private String url;
    private Long pv;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getPv() {
        return pv;
    }

    public void setPv(Long pv) {
        this.pv = pv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        COAppItemRankDTO that = (COAppItemRankDTO) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return pv != null ? pv.equals(that.pv) : that.pv == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (pv != null ? pv.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "COAppItemRankDTO{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", pv=" + pv +
                '}';
    }
}

