package bigscreen.hubpd.com.dto;

import java.io.Serializable;

/**
 * 运营分析微信文章topN文章url和title
 *
 * @author cpc
 * @create 2019-04-09 12:44
 **/
public class OAWXTopNArticleDTO implements Serializable {
    private Long readNum;
    private Long likeNum;
    private String title;
    private String url;
    private String publishDate;
    private Integer articleId;

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

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OAWXTopNArticleDTO that = (OAWXTopNArticleDTO) o;

        if (readNum != null ? !readNum.equals(that.readNum) : that.readNum != null) return false;
        if (likeNum != null ? !likeNum.equals(that.likeNum) : that.likeNum != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (publishDate != null ? !publishDate.equals(that.publishDate) : that.publishDate != null) return false;
        return articleId != null ? articleId.equals(that.articleId) : that.articleId == null;

    }

    @Override
    public int hashCode() {
        int result = readNum != null ? readNum.hashCode() : 0;
        result = 31 * result + (likeNum != null ? likeNum.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        result = 31 * result + (articleId != null ? articleId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OAWXTopNArticleDTO{" +
                "readNum=" + readNum +
                ", likeNum=" + likeNum +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", articleId=" + articleId +
                '}';
    }
}
