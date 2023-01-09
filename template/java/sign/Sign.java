import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

public class Sign {
    private static String data = "";

    private static final String REQUEST_RECEIVE = "0";// 收到请求
    private static final String REQUEST_SEND = "1";// 发出请求
    private static final String RESPONSE_RECEIVE = "2";// 收到响应
    private static final String RESPONSE_SEND = "3";// 发出响应

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("错误，至少需要两个参数！");
        }
        //解开base64
        data = new String(Base64.getDecoder().decode(args[1].getBytes()));
        //只有发出请求的时候才更新签名
        if(args[0].equals(REQUEST_SEND)){
            JSONObject jsonObject =JSONObject.parseObject(data);
            String body = jsonObject.getJSONObject("request").getString("body");
            try{
                JSONObject bodyJson = JSONObject.parseObject(body);
                String sign = bodyJson.getString("sign");
                //更新请求时间
                bodyJson.put("requestTime",getSecondTimestampTwo(new Date()));
                if(sign!=null){
                    //存在签名，重新签名
                    sign = md5_32_small(bodyJson.getString("requestTime") + bodyJson.getJSONObject("data") + "lgb6376681649560");
                    bodyJson.put("sign",sign);
                    //更新签名到json
                    jsonObject.getJSONObject("request").put("body",bodyJson.toString());
                    //更新签名后，输出json内容
                    System.out.println(jsonObject);
                    return;
                }
            }catch (Exception ignored){
                //报错就不改了
            }
        }
        // 输出原始json内容
        System.out.println(data);

    }
    //md5
    public static String md5_32_small(String str) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                int i = b & 255;
                if (i < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(i));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException("Huh, MD5 should be supported?", e2);
        }
    }
    //获取时间戳
    public static String getSecondTimestampTwo(Date date) {
        return date == null ? "0" : String.valueOf(date.getTime() / 1000);
    }
}