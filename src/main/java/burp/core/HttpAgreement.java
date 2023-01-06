package burp.core;

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
    public String body = "";

    public String response_version = "";
    public String state = "";
    public String state_msg = "";
    public String response_headers = "";
    public String response_body = "";
    public HttpAgreement() throws IOException {
        new HttpAgreement(null);
    }
    public HttpAgreement(String requestData) throws IOException {
        new HttpAgreement(requestData,null,null);
    }
    public HttpAgreement(String requestData,String responseData) throws IOException {
        new HttpAgreement(requestData,responseData,null);
    }
    public HttpAgreement(String requestData,String responseData,Process process) throws IOException {
        //从请求解析http协议
       if(requestData!=null){
           Iterable<String> split = Splitter.on("\r\n").split(requestData);
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
           body = strings.get(strings.size()-1);
           if(process!=null){
               process.set("method",method);
               process.set("path",this.path);
               process.set("version",version);
               process.set("headers",headers);
               process.set("body",body);
           }
       }
       if(responseData!=null){
           Iterable<String> split = Splitter.on("\r\n").split(responseData);
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
           response_body = strings.get(strings.size()-1);
           if(process!=null){
               process.set("state",state);
               process.set("state_msg",this.state_msg);
               process.set("response_version",response_version);
               process.set("response_headers",response_headers);
               process.set("response_body",response_body);
           }
       }
    }
    String toRequest() {
        return toRequest(null);
    }
    public String toRequest(Process process) {
        StringBuilder stringBuilder = new StringBuilder();
        if(process!=null){
            method = process.get("method");
            path = process.get("path");
            version = process.get("version");
            headers = process.get("headers");
            body = process.get("body");

        }
        int length = body.getBytes(StandardCharsets.UTF_8).length;
        headers = headers.replaceAll("Content-Length: \\d+","Content-Length: "+length);
        //更新headers
        stringBuilder.append(method).append(" ").append(path).append(" ").append(version).append("\r\n");
        stringBuilder.append(headers).append("\r\n");
        stringBuilder.append("\r\n");
        stringBuilder.append(body);
        return stringBuilder.toString();
    }
    String toResponse()  {
        return toResponse(null);
    }
    public String toResponse(Process process) {
        StringBuilder stringBuilder = new StringBuilder();
        if(process!=null){
            state = process.get("state");
            state_msg = process.get("state_msg");
            response_version = process.get("response_version");
            response_headers = process.get("response_headers");
            response_body = process.get("response_body");
        }
        stringBuilder.append(response_version).append(" ").append(state).append(" ").append(state_msg).append("\r\n");
        stringBuilder.append(headers).append("\r\n");
        stringBuilder.append("\r\n");
        stringBuilder.append(body);
        return stringBuilder.toString();
    }

}
