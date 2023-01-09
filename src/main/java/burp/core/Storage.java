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
    public static  ArrayList<Rule> read(String type){
        String string = BurpExtender.callbacks.loadExtensionSetting(type);
        return decode(string);
    }
    public static  boolean readBoolean(String type){
        return Objects.equals(BurpExtender.callbacks.loadExtensionSetting(type), "true");
    }

    public static  void writeBoolean(String type,boolean value){
        BurpExtender.callbacks.saveExtensionSetting(type,value?"true":"false");
    }
    public static  String readString(String type){
        return BurpExtender.callbacks.loadExtensionSetting(type);
    }

    public static  void writeString(String type,String value){
        BurpExtender.callbacks.saveExtensionSetting(type,value);
    }
    /**
     * 将rule数据写入文件
     * @param type String
     * @param rules ArrayList<Rule>
     */
    public static void write(String type, ArrayList<Rule> rules) {
        BurpExtender.callbacks.saveExtensionSetting(type, encode(rules));
    }

    public static String encode(ArrayList<Rule> rules){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(buffer);
            output.writeObject(rules);
            output.close();
            return Base64.getEncoder().encodeToString(buffer.toByteArray());
        } catch (IOException e) {
            BurpExtender.print("写入数据失败:"+e.getMessage());
            return "";
        }

    }
    @SuppressWarnings("unchecked")
    public static ArrayList<Rule> decode(String string){
        try{
            if(string==null|| string.equals("null")){
                return new ArrayList<>();
            }
            ByteArrayInputStream buffer = new ByteArrayInputStream(Base64.getDecoder().decode(string.getBytes()));
            ObjectInputStream input = new ObjectInputStream(buffer);
            ArrayList<Rule> rule = (ArrayList<Rule>) input.readObject();
            input.close();
            return rule;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
}
