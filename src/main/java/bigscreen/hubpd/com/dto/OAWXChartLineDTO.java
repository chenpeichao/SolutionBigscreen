package bigscreen.hubpd.com.dto;

import java.io.Serializable;

/**
 * 运营分析-微信折线图数据库查询结果dto
 *
 * @author cpc
 * @create 2019-04-08 15:20
 **/
public class OAWXChartLineDTO implements Serializable {
    private Long readNum;
    private Long likeNum;
    private String publishDate;

    public OAWXChartLineDTO() {
    }

    public OAWXChartLineDTO(Long readNum, Long likeNum) {
        this.readNum = readNum;
        this.likeNum = likeNum;
    }

    public Long getReadNum() {
        return readNum;
    }

    public void setReadNum(Long readNum) {
        this.readNum = readNum;
    }

    public Long getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Long likeNum) {
        this.likeNum = likeNum;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OAWXChartLineDTO that = (OAWXChartLineDTO) o;

        if (readNum != null ? !readNum.equals(that.readNum) : that.readNum != null) return false;
        if (likeNum != null ? !likeNum.equals(that.likeNum) : that.likeNum != null) return false;
        return publishDate != null ? publishDate.equals(that.publishDate) : that.publishDate == null;

    }

    @Override
    public int hashCode() {
        int result = readNum != null ? readNum.hashCode() : 0;
        result = 31 * result + (likeNum != null ? likeNum.hashCode() : 0);
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OAWXChartLineDTO{" +
                "readNum=" + readNum +
                ", likeNum=" + likeNum +
                ", publishDate='" + publishDate + '\'' +
                '}';
    }
}
