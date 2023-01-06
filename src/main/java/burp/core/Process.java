package burp.core;

import burp.BurpExtender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 交互
 */
public class Process {
    private String temp = "";
    public Process() throws IOException {
        //创建临时文件存储数据
        Path tmpCustomPrefix = Files.createTempDirectory("CustomCrypto");
        temp = tmpCustomPrefix.toString();
        BurpExtender.print(String.format("临时会话文件夹[ %s ]创建成功",temp));
    }

    public String getTemp() {
        return  temp;
    }

    /**
     * 设置值
     * @param key String
     * @param value String
     */
    public void set(String key,String value){
        try {
            Files.write(Paths.get(temp+"/"+key+".txt"), value.trim().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            BurpExtender.print(String.format("错误：%s",e.getMessage()),1);
            BurpExtender.print(String.format("写入文件失败：%s",temp+"/"+key+".txt"));
        }
    }

    /**
     * 获取值
     * @param key String
     */
    public String get(String key){
        try {
            return new String(Files.readAllBytes(Paths.get(temp + "/" + key + ".txt"))).trim();
        } catch (IOException e) {
            e.printStackTrace();
            BurpExtender.print(String.format("错误：%s",e.getMessage()),1);
            BurpExtender.print(String.format("读取文件失败：%s",temp+"/"+key+".txt"));
        }
        return "";
    }
}
