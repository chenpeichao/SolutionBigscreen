package bigscreen.hubpd.com.dto;

import java.io.Serializable;

/**
 * 应用appkeyDTO
 *
 * @author cpc
 * @create 2019-04-08 22:46
 **/
public class AppKeyDTO implements Serializable {
    private String appaccount;
    private String appaccount2;

    public String getAppaccount() {
        return appaccount;
    }

    public void setAppaccount(String appaccount) {
        this.appaccount = appaccount;
    }

    public String getAppaccount2() {
        return appaccount2;
    }

    public void setAppaccount2(String appaccount2) {
        this.appaccount2 = appaccount2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppKeyDTO appKeyDTO = (AppKeyDTO) o;

        if (appaccount != null ? !appaccount.equals(appKeyDTO.appaccount) : appKeyDTO.appaccount != null) return false;
        return appaccount2 != null ? appaccount2.equals(appKeyDTO.appaccount2) : appKeyDTO.appaccount2 == null;

    }

    @Override
    public int hashCode() {
        int result = appaccount != null ? appaccount.hashCode() : 0;
        result = 31 * result + (appaccount2 != null ? appaccount2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AppKeyDTO{" +
                "appaccount='" + appaccount + '\'' +
                ", appaccount2='" + appaccount2 + '\'' +
                '}';
    }
}
