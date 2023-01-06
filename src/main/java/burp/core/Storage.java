package burp.core;

import burp.BurpExtender;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;

/**
 * 存储类，控制插件数据存储部分
 */
public class Storage {
    /**
     * 读取rule数据
     * @param type String
     * @return JSONArray
     */
    @SuppressWarnings("unchecked")
    public static  ArrayList<Rule> read(String type){
        try{
            String string = BurpExtender.callbacks.loadExtensionSetting(type);
            if(string==null|| string.equals("null")){
                return new ArrayList<>();
            }
            ByteArrayInputStream buffer = new ByteArrayInputStream(Base64.getDecoder().decode(string.getBytes()));
            ObjectInputStream input = new ObjectInputStream(buffer);
            ArrayList<Rule> rule = (ArrayList<Rule>)input.readObject();
            input.close();
            return rule;
        }catch (Exception e){
            BurpExtender.print("读取异常："+e);
            return new ArrayList<>();
        }
    }
    public static  boolean readBoolean(String type){
        return Objects.equals(BurpExtender.callbacks.loadExtensionSetting(type), "true");
    }

    public static  void writeBoolean(String type,boolean value){
        BurpExtender.callbacks.saveExtensionSetting(type,value?"true":"false");
    }
    /**
     * 将rule数据写入文件
     * @param type String
     * @param rules ArrayList<Rule>
     */
    public static void write(String type, ArrayList<Rule> rules) {
        try{
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(buffer);
            output.writeObject(rules);
            output.close();
            BurpExtender.callbacks.saveExtensionSetting(type, Base64.getEncoder().encodeToString(buffer.toByteArray()));
        }catch (Exception e){
            BurpExtender.print("写入数据失败:"+e.getMessage());
        }

    }
}
