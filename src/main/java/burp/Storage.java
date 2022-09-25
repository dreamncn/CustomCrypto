package burp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 存储类，控制插件数据存储部分
 */
public class Storage {
    public static String RUN_LIST = "CustomCryptoRunList";
    public static String SAVE_LIST = "CustomCryptoSaveList";
    public static JSONArray read(String type) {
        if(Main.debug){
            String fileName= type+".ankio";
            Path path = Paths.get(fileName);
            try {
                 byte[] bytes = Files.readAllBytes(path);
                String all = new String(bytes);
                return JSONArray.parseArray(all);
            }catch (Exception ioException){
                return new JSONArray();
            }

        }else{
            String all = BurpExtender.callbacks.loadExtensionSetting(type);
            try{
                JSONArray jsonArray = JSONArray.parseArray(all);
                if(jsonArray == null)jsonArray = new JSONArray();
                return jsonArray;
            }catch (Exception e){
                return new JSONArray();
            }
        }
    }

    public static void write(String type,JSONArray data){
        if(Main.debug){
            String fileName= type+".ankio";
            Path path = Paths.get(fileName);
            try{
                Files.write(path,data.toJSONString().getBytes());
            }catch (Exception ignored){

            }

        }else{
             BurpExtender.callbacks.saveExtensionSetting(type,data.toJSONString());
        }
    }
}
