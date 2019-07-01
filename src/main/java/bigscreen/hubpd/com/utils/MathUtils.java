package bigscreen.hubpd.com.utils;

/**
 * 算法工具类
 *
 * @author cpc
 * @create 2019-05-01 11:43
 **/
public class MathUtils {
    /**
     * 随机获取两个整型之间的随机数，不区分先后顺序大小（参数小于0时，返回-1）
     * @param x
     * @param y
     * @return
     */
    public static int getRandomMinTwoInt(int x, int y) {
        int num = -1;
        //说明：两个数在合法范围内，并不限制输入的数哪个更大一些
        if(x<0||y<0) {
            return num;
        }else {
            int max = x>y?x:y;
            int min = x<y?x:y;
            int mid = max -min;//求差
            //产生随机数
            num = (int) (Math.random()*(mid+1))+min;
        }
        return num;
    }
}
