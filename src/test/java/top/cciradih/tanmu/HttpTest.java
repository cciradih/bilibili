package top.cciradih.tanmu;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpTest {
    private static final Http HTTP = new Http();

    @Test
    void checkLogin() {
        JSONObject jsonObject = HTTP.checkLogin("SESSDATA=e50c3aba%2C1566049974%2Cad9a8571; Domain=.bilibili.com; Expires=Sat, 17-Aug-2019 13:52:54 GMT; Path=/; HttpOnly");
        // {"code":-101,"message":"账号未登录","ttl":1}
        // {"code":0,"data":{"user_charged":0,"user_intimacy":5063584,"is_level_top":0,"uname":"Cciradih","billCoin":50.9,"achieve":255,"user_next_level":22,"gold":0,"user_level_rank":">50000","uid":2078718,"face":"https://i0.hdslb.com/bfs/face/8e88d34fe348d528b21462301412dfa84ac221ac.jpg","user_level":21,"silver":67586,"vip":0,"svip":0,"user_next_intimacy":8000000},"message":"0","ttl":1}
        System.out.println(jsonObject);
    }

    @Test
    void getLoginUrl() {
        JSONObject jsonObject = HTTP.getLoginUrl();
        // {"code":0,"data":{"oauthKey":"7381670f4f89a683fa63cf41a4a308a8","url":"https://passport.bilibili.com/qrcode/h5/login?oauthKey=7381670f4f89a683fa63cf41a4a308a8"},"status":true,"ts":1563459110}
        System.out.println(jsonObject);
    }

    @Test
    void getLoginInfo() {
        JSONObject jsonObject = HTTP.getLoginInfo("7381670f4f89a683fa63cf41a4a308a8");
//        {"data":-4,"message":"Can't scan~","status":false}
//        {"data":-5,"message":"Can't confirm~","status":false}
//        {"code":0,"data":{"url":"https://passport.biligame.com/crossDomain?DedeUserID=2078718&DedeUserID__ckMd5=f2fb048ebaeb038f&Expires=2592000&SESSDATA=4718bfa4%2C1566051193%2C6a49d371&bili_jct=f8ec1400eae101f7eca579e2ff60967b&gourl=http%3A%2F%2Fwww.bilibili.com"},"status":true,"ts":1563459193}
        System.out.println(jsonObject);
    }
}