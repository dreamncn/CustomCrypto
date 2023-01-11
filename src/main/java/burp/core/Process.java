package burp.core;

import burp.BurpExtender;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

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
            BurpExtender.print("读取："+temp + "/" + key + ".txt");
            return new String(Files.readAllBytes(Paths.get(temp + "/" + key + ".txt"))).trim();
        } catch (IOException e) {
            e.printStackTrace();
            BurpExtender.print(String.format("错误：%s",e.getMessage()),1);
            BurpExtender.print(String.format("读取文件失败：%s",temp+"/"+key+".txt"));
        }
        return "";
    }

    public void destroy(){
        try {
            Path path = Paths.get(temp);

            Files.walkFileTree(path,
                    new SimpleFileVisitor<Path>() {
                        // 先去遍历删除文件
                        @Override
                        public FileVisitResult visitFile(Path file,
                                                         BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            System.out.printf("文件被删除 : %s%n", file);
                            return FileVisitResult.CONTINUE;
                        }
                        // 再去遍历删除目录
                        @Override
                        public FileVisitResult postVisitDirectory(Path dir,
                                                                  IOException exc) throws IOException {
                            Files.delete(dir);
                            System.out.printf("文件夹被删除: %s%n", dir);
                            return FileVisitResult.CONTINUE;
                        }

                    }
            );
            BurpExtender.print(String.format("临时文件夹[ %s ]已删除",temp));
        } catch (IOException ignored) {

        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.destroy();
    }
}
