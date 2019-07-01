package bigscreen.hubpd.com.dto;

import java.io.Serializable;

/**
 * 运营分析-客户端折线图数据库查询结果dto
 *
 * @author cpc
 * @create 2019-04-08 21:48
 **/
public class OAAppChatLineDTO implements Serializable {
    private Long pv;
    private Long uv;
    private Long day;

    public OAAppChatLineDTO() {
    }

    public OAAppChatLineDTO(Long pv, Long uv) {
        this.pv = pv;
        this.uv = uv;
    }

    public Long getPv() {
        return pv;
    }

    public void setPv(Long pv) {
        this.pv = pv;
    }

    public Long getUv() {
        return uv;
    }

    public void setUv(Long uv) {
        this.uv = uv;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OAAppChatLineDTO that = (OAAppChatLineDTO) o;

        if (pv != null ? !pv.equals(that.pv) : that.pv != null) return false;
        if (uv != null ? !uv.equals(that.uv) : that.uv != null) return false;
        return day != null ? day.equals(that.day) : that.day == null;

    }

    @Override
    public int hashCode() {
        int result = pv != null ? pv.hashCode() : 0;
        result = 31 * result + (uv != null ? uv.hashCode() : 0);
        result = 31 * result + (day != null ? day.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OAAppChatLineDTO{" +
                "pv=" + pv +
                ", uv=" + uv +
                ", day=" + day +
                '}';
    }
}
