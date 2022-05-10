package burp;

import com.sun.nio.sctp.MessageInfo;

import java.util.Arrays;
import java.util.List;

public class BurpHttpListener implements  IHttpListener, IProxyListener  {
    private static byte[] rawData = null;
    private static String url = "";
    @Override
    public void processHttpMessage(
            int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        if (messageIsRequest) {
            requestOut(messageInfo,toolFlag);//发送请求
        } else {
            responseIn(messageInfo,toolFlag);//收到响应
        }
    }
    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        if (messageIsRequest) {
            requestIn(message);//收到请求
        } else {
            responseOut(message);//发送响应
        }
    }
    //requestIn阶段，收到客户端发送的加密request，进行解密并替换requestBody，使得BurpSuite中显示明文request；
    private void requestIn(IInterceptedProxyMessage message) {
        BurpExtender.stdout.println("requestIn：");
        IHttpRequestResponse messageInfo = message.getMessageInfo();
        rawData = messageInfo.getRequest();//提前放好rawData
        IRequestInfo requestInfo = BurpExtender.callbacks.getHelpers().analyzeRequest(messageInfo);
        url = requestInfo.getUrl().toString();
        String requestData = new String(rawData);
        String body = requestData.substring( requestInfo.getBodyOffset());
        //原始的body进行解密
        body = BurpExtender.gui.findAndRun(url,body, ReqRep.REQUEST_RECEIVE);
        //获取header部分
        String header = requestData.substring(0,requestInfo.getBodyOffset());
        messageInfo.setRequest((header+body).getBytes());
    }
    //requestOut阶段，即将发送request到服务端，读取明文的request，重新进行加密（包括签名、编码、更新时间戳等），使得服务端正常解析；
    private void requestOut(IHttpRequestResponse messageInfo,int toolFlag) {
        BurpExtender.stdout.println("requestOut：");
        IRequestInfo requestInfo = BurpExtender.callbacks.getHelpers().analyzeRequest(messageInfo);
        url = requestInfo.getUrl().toString();
        rawData = messageInfo.getRequest();
        String requestData = new String(rawData);
        String body = requestData.substring( requestInfo.getBodyOffset());
        //原始的body进行解密
        String bodyt = BurpExtender.gui.findAndRun(url,body, ReqRep.REQUEST_SEND);
        List<String> headerslist=requestInfo.getHeaders();
        //repeater发送不会自动修改Content-Length长度。
        String Length="";
        for (int i=0;i< headerslist.size();i++){
            if (headerslist.get(i).startsWith("Content-Length:")){
                Length=headerslist.get(i);
            }
        }
        //获取header部分
        String header = requestData.substring(0,requestInfo.getBodyOffset());
        if (bodyt!=body){
            header=header.replaceAll(Length,"Content-Length:"+String.valueOf(bodyt.length()));
        }
        BurpExtender.stdout.println("requestMaxBody："+header+bodyt);
        messageInfo.setRequest((header+bodyt).getBytes());



    }
    //responseIn阶段，收到加密response，进行解密并替换responseBody，使得BurpSuite中显示明文response；
    private void responseIn(IHttpRequestResponse messageInfo,int toolFlag) {
        rawData = messageInfo.getResponse();//提前放好rawData
        IResponseInfo responseInfo = BurpExtender.callbacks.getHelpers().analyzeResponse(rawData);
        IRequestInfo requestInfo = BurpExtender.callbacks.getHelpers().analyzeRequest(messageInfo);
        url=requestInfo.getUrl().toString();
        String responseData = new String(rawData);
        String body = responseData.substring( responseInfo.getBodyOffset());
        BurpExtender.stdout.println("responsebody："+body);
        //原始的body进行解密
        body = BurpExtender.gui.findAndRun(url,body, ReqRep.RESPONSE_RECEIVE);
        //获取header部分
        String header = responseData.substring(0,responseInfo.getBodyOffset());
        messageInfo.setResponse((header+body).getBytes());
    }
    //responseOut阶段，即将发送response到客户端，读取明文的response，重新进行加密，使得客户端正常解析。
    private void responseOut(IInterceptedProxyMessage message) {
        BurpExtender.stdout.println("responseOut：");
        if(rawData!=null){
            message.getMessageInfo().setResponse(rawData);
            rawData = null;
        }else {
            byte[] response = message.getMessageInfo().getResponse();
            IResponseInfo responseInfo = BurpExtender.callbacks.getHelpers().analyzeResponse(response);
            String responseData = new String(response);
            String body = responseData.substring( responseInfo.getBodyOffset());
            //原始的body进行解密
            body = BurpExtender.gui.findAndRun(url,body, ReqRep.RESPONSE_SEND);
            //获取header部分
            String header = responseData.substring(0,responseInfo.getBodyOffset());
            message.getMessageInfo().setResponse((header+body).getBytes());
        }
    }
}
