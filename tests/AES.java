import com.alibaba.fastjson.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;

class UByte  {
    public static final byte MAX_VALUE = -1;
}
class ContainerUtils {
    public static final String KEY_VALUE_DELIMITER = "=";
}
class Base64 {
    private static final char[] charTab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    public static String encode(byte[] data) {
        return encode(data, 0, data.length, null).toString();
    }

    public static StringBuffer encode(byte[] data, int start, int len, StringBuffer buf) {
        if (buf == null) {
            buf = new StringBuffer((data.length * 3) / 2);
        }
        int end = len - 3;
        int i = start;
        while (i <= end) {
            int d = ((data[i] & UByte.MAX_VALUE) << 16) | ((data[i + 1] & UByte.MAX_VALUE) << 8) | (data[i + 2] & UByte.MAX_VALUE);
            char[] cArr = charTab;
            buf.append(cArr[(d >> 18) & 63]);
            buf.append(cArr[(d >> 12) & 63]);
            buf.append(cArr[(d >> 6) & 63]);
            buf.append(cArr[d & 63]);
            i += 3;
        }
        if (i == (start + len) - 2) {
            int d2 = ((data[i] & UByte.MAX_VALUE) << 16) | ((data[i + 1] & UByte.MAX_VALUE) << 8);
            char[] cArr2 = charTab;
            buf.append(cArr2[(d2 >> 18) & 63]);
            buf.append(cArr2[(d2 >> 12) & 63]);
            buf.append(cArr2[(d2 >> 6) & 63]);
            buf.append(ContainerUtils.KEY_VALUE_DELIMITER);
        } else if (i == (start + len) - 1) {
            int d3 = (data[i] & UByte.MAX_VALUE) << 16;
            char[] cArr3 = charTab;
            buf.append(cArr3[(d3 >> 18) & 63]);
            buf.append(cArr3[(d3 >> 12) & 63]);
            buf.append("==");
        }
        return buf;
    }

    private static int decode(char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        }
        if (c >= 'a' && c <= 'z') {
            return (c - 'a') + 26;
        }
        if (c >= '0' && c <= '9') {
            return (c - '0') + 26 + 26;
        }
        if (c == '+') {
            return 62;
        }
        if (c == '/') {
            return 63;
        }
        if (c == '=') {
            return 0;
        }
        throw new RuntimeException("错误的数据格式引起decode出错: " + c);
    }

    private static ByteArrayOutputStream decodeToStream(String s) {
        int i = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len = s.length();
        while (true) {
            if (i < len && s.charAt(i) <= ' ') {
                i++;
            } else if (i == len) {
                break;
            } else {
                char ch0 = s.charAt(i);
                char ch1 = s.charAt(i + 1);
                char ch2 = s.charAt(i + 2);
                char ch3 = s.charAt(i + 3);
                int tri = (decode(ch0) << 18) + (decode(ch1) << 12) + (decode(ch2) << 6) + decode(ch3);
                bos.write((tri >> 16) & 255);
                if (s.charAt(i + 2) == '=') {
                    break;
                }
                bos.write((tri >> 8) & 255);
                if (s.charAt(i + 3) == '=') {
                    break;
                }
                bos.write(tri & 255);
                i += 4;
            }
        }
        return bos;
    }

    public static byte[] decodeToBytes(String s) {
        if (s == null) {
            return null;
        }
        return decodeToStream(s).toByteArray();
    }

    public static String decodeToString(String s) {
        if (s == null) {
            return null;
        }
        return decodeToStream(s).toString();
    }
}


public class AES {
    private static boolean isEncrypton = false;
    private static String data = "";

    private static final String REQUEST_RECEIVE = "0";// 收到请求
    private static final String REQUEST_SEND = "1";// 发出请求
    private static final String RESPONSE_RECEIVE = "2";// 收到响应
    private static final String RESPONSE_SEND = "3";// 发出响应

    public static void main(String[] args) throws Exception {




        AESCipher.clientKey_ = java.util.Base64.getDecoder().decode("mt8RcYbi4Yenxr99dYLPozDea4XJp2nl6oy9HEFDv6g");
        AESCipher.clientIv_ = java.util.Base64.getDecoder().decode("sA6ZakuAAeYVW+yzG8Vtdw");
        AESCipher.serverKey_ = java.util.Base64.getDecoder().decode("kS5fVErUbxCdW74tVw2mLP4s/+SDQVtzEnxp5zBo0xQ");
        AESCipher.serverIv_ = java.util.Base64.getDecoder().decode("0mdd0S4FjrSQERd0CcXJlg");


        if (args.length != 2) {
            throw new Exception("错误，至少需要两个参数！");
        }

        // 是否加密，一般只有发出请求才需要加密，收到请求都是解密
        isEncrypton = args[0].equals(REQUEST_SEND) || args[0].equals(RESPONSE_SEND);
        // 数据

        if(isEncrypton){
            System.out.println("123");
            return;
        }


        data = Base64.decodeToString(args[1]);

        String[] datas = data.split("||||");
        if(datas.length==2){
            data = datas[1];
        }

        try{
            JSONObject jsonObject = JSONObject.parseObject(data);
            String result = jsonObject.getString("=");
            if (isEncrypton) {
                result  = encode(result);
            } else {
                result = decode(result);
            }
            jsonObject.put("=",result);

            if (datas.length==2){
                System.out.println(datas[0]+"||||"+jsonObject.toString());
            }else{
                System.out.println(jsonObject.toString());
            }

            return;

        }catch (Exception e){

        }

        System.out.println(data);



    }

    public static String encode(String d) throws Exception {
        return AESAdapter.encrypt(d,AESCipher.clientKey_,AESCipher.clientIv_);
    }

    public static String decode(String d) throws Exception {
        return AESAdapter.decrypt(d,AESCipher.serverKey_,AESCipher.serverIv_);
    }
}

final class AESAdapter {

    public static byte[] encrypt(byte[] cipherByts, byte[] key, byte[] iv) throws Exception {

        return AESCipher.encrypt(cipherByts, key, iv);
    }

    public static byte[] decrypt(byte[] cipherByts, byte[] key, byte[] iv) throws Exception {

        return AESCipher.decrypt(cipherByts, key, iv);
    }

    public static String encrypt(String plantText, byte[] key, byte[] iv) throws Exception {
        return AESCipher.encrypt(plantText, key, iv);
    }

    public static String decrypt(String cipherText, byte[] key, byte[] iv) throws Exception {

        return AESCipher.decrypt(cipherText, key, iv);
    }
}
class AESCipher {
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS7Padding";
    public static byte[] clientIv_;
    public static byte[] clientKey_;
    public static byte[] customerIv_;
    public static byte[] customerKey_;
    public static byte[] serverIv_;
    public static byte[] serverKey_;

    public static String encrypt(String plantText, byte[] key, byte[] iv) throws Exception {
        byte[] plantBytes = plantText.getBytes("UTF-8");
        byte[] cipherBytes = encrypt(plantBytes, key, iv);
        return Base64.encode(cipherBytes);
    }

    public static byte[] encrypt(byte[] cipherByts, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = initCipher(key, iv, 1);
        byte[] plantByts = cipher.doFinal(cipherByts);
        return plantByts;
    }

    public static String decrypt(String cipherText, byte[] key, byte[] iv) throws Exception {
        byte[] cipherBytes = Base64.decodeToBytes(cipherText);
        byte[] plantBytes = decrypt(cipherBytes, key, iv);
        return new String(plantBytes, "UTF-8");
    }

    public static byte[] decrypt(byte[] cipherByts, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = initCipher(key, iv, 2);
        byte[] plantByts = cipher.doFinal(cipherByts);
        return plantByts;
    }

    private static Cipher initCipher(byte[] key, byte[] iv, int opMode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        AlgorithmParameterSpec spec = new IvParameterSpec(iv);
        SecretKey skey = new SecretKeySpec(key, "AES");
        cipher.init(opMode, skey, spec);
        return cipher;
    }
}



