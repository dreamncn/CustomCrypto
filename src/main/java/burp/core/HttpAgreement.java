package burp.core;

import burp.BurpExtender;
import burp.IRequestInfo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

// http协议封装
public class HttpAgreement {
    public String method = "";
    public String path = "";
    public String version = "";
    public String headers = "";
    public byte[]  body;

    public String response_version = "";
    public String state = "";
    public String state_msg = "";
    public String response_headers = "";
    public byte[] response_body;
    public HttpAgreement() throws IOException {
        new HttpAgreement(null);
    }
    public HttpAgreement(byte[] requestData) throws IOException {
        new HttpAgreement(requestData,null);
    }
    public HttpAgreement(byte[] requestData,byte[] responseData) throws IOException {
        new HttpAgreement(requestData,responseData,null);
    }
    public byte[] subByte(byte[] b,int off,int length){
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }
    public HttpAgreement(byte[] requestData,byte[] responseData,Process process) throws IOException {
        //从请求解析http协议
       if(requestData!=null){
           IRequestInfo requestInfo = BurpExtender.helpers.analyzeRequest(requestData);
           Iterable<String> split = Splitter.on("\r\n").split(new String(requestData));
           ArrayList<String> strings = Lists.newArrayList(split);
           String[] path =  strings.get(0).split(" "); //首行是请求头
           int i;
           method = path[0];
           this.path = path[1];
           version = path[2];
           headers = "";
           StringBuilder s = new StringBuilder();
           for ( i = 1; i < strings.size()-2; i++) {
               s.append(strings.get(i)).append("\r\n");
           }
           headers = s.toString().trim();

           body = subByte(requestData,requestInfo.getBodyOffset(), requestData.length - requestInfo.getBodyOffset());
           if(process!=null){
               process.set("method",method);
               process.set("path",this.path);
               process.set("version",version);
               process.set("headers",headers);
               process.setRaw("body",body);
           }
       }
       if(responseData!=null){
           Iterable<String> split = Splitter.on("\r\n").split(new String(responseData));
           ArrayList<String> strings = Lists.newArrayList(split);
           String[] path =  strings.get(0).split(" "); //首行是请求头
           int i;
           state = path[1];
           state_msg = path[2];
           response_version = path[0];
           response_headers = "";
           StringBuilder s = new StringBuilder();
           for ( i = 1; i < strings.size()-2; i++) {
               s.append(strings.get(i)).append("\r\n");
           }
           response_headers = s.toString().trim();
           IRequestInfo requestInfo = BurpExtender.helpers.analyzeRequest(responseData);
           response_body = subByte(requestData,requestInfo.getBodyOffset(), responseData.length - requestInfo.getBodyOffset());
           if(process!=null){
               process.set("state",state);
               process.set("state_msg",this.state_msg);
               process.set("response_version",response_version);
               process.set("response_headers",response_headers);
               process.setRaw("response_body",response_body);
           }
       }
    }
    byte[] toRequest() {
        return toRequest(null);
    }
    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }
    public byte[] toRequest(Process process) {
        StringBuilder stringBuilder = new StringBuilder();
        if(process!=null){
            method = process.get("method");
            path = process.get("path");
            version = process.get("version");
            headers = process.get("headers");
            body = process.getRaw("body");

        }
        int length = body.length;
        headers = headers.replaceAll("Content-Length: \\d+","Content-Length: "+length);
        //更新headers
        stringBuilder.append(method).append(" ").append(path).append(" ").append(version).append("\r\n");
        stringBuilder.append(headers).append("\r\n");
        stringBuilder.append("\r\n");
        byte[] bytes = stringBuilder.toString().getBytes();

        return byteMerger(bytes,body);
    }
    byte[] toResponse()  {
        return toResponse(null);
    }
    public byte[] toResponse(Process process) {
        StringBuilder stringBuilder = new StringBuilder();
        if(process!=null){
            state = process.get("state");
            state_msg = process.get("state_msg");
            response_version = process.get("response_version");
            response_headers = process.get("response_headers");
            response_body = process.getRaw("response_body");
        }
        stringBuilder.append(response_version).append(" ").append(state).append(" ").append(state_msg).append("\r\n");
        stringBuilder.append(response_headers).append("\r\n");
        stringBuilder.append("\r\n");
        byte[] bytes = stringBuilder.toString().getBytes();

        return byteMerger(bytes,response_body);
    }

}
