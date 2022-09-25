package burp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Shell {
    public static String exec(String commands)  {
        try{
            InputStream in = Runtime.getRuntime().exec(commands).getInputStream();
            byte[] bcache = new byte[1024];
            int readSize = 0;   //每次读取的字节长度
            ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
            while ((readSize = in.read(bcache)) > 0) {
                infoStream.write(bcache, 0, readSize);
            }
            return infoStream.toString();
        }catch (Exception e){
            e.printStackTrace();
            return e.toString();
        }

    }
}
