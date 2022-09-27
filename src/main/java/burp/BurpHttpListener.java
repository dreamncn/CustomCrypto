package burp;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BurpHttpListener implements IHttpListener, IProxyListener {


    /**
     * 检查是否需要拦截
     *
     * @param request
     * @param cmd
     * @return
     */
    public boolean analyze(IHttpRequestResponse request, boolean messageIsRequest, String[] cmd) {
        IRequestInfo requestInfo = BurpExtender.helpers.analyzeRequest(request);
        IResponseInfo responseInfo = null;
        if(!messageIsRequest){
            responseInfo = BurpExtender.helpers.analyzeResponse(request.getResponse());
        }
        String url = "";
        String testUrl = request.getHttpService().getProtocol() + "://" + request.getHttpService().getHost();

        if (requestInfo.getHeaders().size() > 0) {
            //获取URL部分
            String firstLine = requestInfo.getHeaders().get(0); //first line
            String[] tmp = firstLine.split(" ");
            String method = "get";//访问的方法
            if (tmp.length == 3) {
                url = tmp[1];
                url = testUrl + url;
                method = tmp[0].toLowerCase();


                //获取Request headers
                StringBuilder requestHeaders = new StringBuilder();
                for (int i = 1; i < requestInfo.getHeaders().size(); i++) {
                    requestHeaders.append(requestInfo.getHeaders().get(i)).append("\n");
                }
                //获取Request Bodys
                String RequestBody = new String(request.getRequest()).substring(requestInfo.getBodyOffset());
                StringBuilder ResponseHeaders = new StringBuilder();
                String ResponseBody = "";
                if (!messageIsRequest) {
                    //获取Response headers

                    for (int i = 1; i < responseInfo.getHeaders().size(); i++) {
                        ResponseHeaders.append(responseInfo.getHeaders().get(i)).append("\n");
                    }
                    //获取Response Bodys
                    ResponseBody = new String(request.getResponse()).substring(responseInfo.getBodyOffset());
                }

                JSONArray jsonArray = BurpExtender.gui.getUsageList();
                for (int j = 0; j < jsonArray.size(); j++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    if(!jsonObject.getBoolean("use"))continue;
                    boolean useRegex = jsonObject.getBoolean("watchUseRegex");

                    int check = 0;
                    //增加判断拦截get或者post请求
                    if(jsonObject.getBoolean("watchGET")){
                        check++;
                        if(method.equals("get")){
                            check--;
                        }
                    }
                    if(jsonObject.getBoolean("watchPOST")){
                        check++;
                        if(method.equals("post")){
                            check--;
                        }
                    }
                    if(jsonObject.getBoolean("watchOPTIONS")){
                        check++;
                        if(method.equals("options")){
                            check--;
                        }
                    }
                    if(jsonObject.getBoolean("watchHEAD")){
                        check++;
                        if(method.equals("head")){
                            check--;
                        }
                    }
                    if(jsonObject.getBoolean("watchPUT")){
                        check++;
                        if(method.equals("put")){
                            check--;
                        }
                    }
                    if(jsonObject.getBoolean("watchDELETE")){
                        check++;
                        if(method.equals("delete")){
                            check--;
                        }
                    }


                    if (jsonObject.getBoolean("watchUrlIncludeSelect")) {
                        //不为空
                        check++;
                        String watchUrlInclude = jsonObject.getString("watchUrlInclude");
                        if ((useRegex && url.matches(watchUrlInclude))
                                || url.contains(watchUrlInclude)) {
                            check--;
                        }
                    }

                    if (jsonObject.getBoolean("watchReqHeadIncludeSelect")) {
                        check++;
                        if ((useRegex && url.matches(requestHeaders.toString()))
                                || url.contains(requestHeaders.toString())) {
                            check--;
                        }
                        //不为空

                    }
                    if (jsonObject.getBoolean("watchReqBodyIncludeSelect")) {
                        //不为空
                        check++;
                        if ((useRegex && url.matches(RequestBody))
                                || url.contains(RequestBody)) {
                            check--;
                        }
                    }
                    if (!messageIsRequest) {
                        if (jsonObject.getBoolean("watchRespHeadIncludeSelect")) {
                            //不为空
                            check++;

                            if ((useRegex && url.matches(ResponseHeaders.toString()))
                                    || url.contains(ResponseHeaders.toString())) {
                                check--;
                            }
                        }

                        if (jsonObject.getBoolean("watchRespBodyIncludeSelect")) {
                            //不为空
                            check++;
                            if ((useRegex && url.matches(ResponseBody))
                                    || url.contains(ResponseBody)) {
                                check--;
                            }
                        }
                    }

                    if (check == 0) {//所有条件都满足就可以进行下一步操作
                        cmd[0] = jsonObject.getString("command");//需要执行的命令
                        return true;
                    }



                }
                return false;


            }


        }


        return false;

    }

    /**
     * 当即将发出HTTP请求以及收到HTTP响应时，会调用此方法。
     *
     * @param toolFlag         指示发出请求的Burp工具的flag。
     *                         Burp工具 flags 定义在 <code>IBurpExtenderCallbacks</code> 接口.
     * @param messageIsRequest 是否为请求。
     * @param messageInfo      要处理的请求/回复的详细信息。 扩展可以调用此对象上的setter方法来更新当前消息，从而修改Burp的行为。
     */
    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        String[] cmd = new String[1];
        //使用引用传递获取需要执行的命令
        //返回值标识是否需要拦截
        if (!analyze(messageInfo, messageIsRequest, cmd)) return;
        BurpExtender.stdout.println("=================================================");
        if (messageIsRequest) {
            requestOut(messageInfo, toolFlag, cmd[0]);//发送请求
        } else {
            responseIn(messageInfo, toolFlag, cmd[0]);//收到响应
        }
        BurpExtender.stdout.println("=================================================");
    }

    /**
     * 当代理处理HTTP消息时，会调用此方法。
     *
     * @param messageIsRequest 是否为请求。
     * @param message          扩展可用于查询和更新消息的详细信息，并控制消息是否应拦截并显示给用户进行手动审查或修改。
     */
    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        String[] cmd = new String[1];
        //使用引用传递获取需要执行的命令
        //返回值标识是否需要拦截
        if (!analyze(message.getMessageInfo(), messageIsRequest, cmd)) return;
        BurpExtender.stdout.println("=================================================");
        if (messageIsRequest) {
            requestIn(message,  cmd[0]);//收到请求
        } else {
            responseOut(message, cmd[0]);//发送响应
        }
        BurpExtender.stdout.println("=================================================");
    }


    private String analyzeRequestResponse(String requestData,String responseData,boolean isResponse){
        String[] reqs = requestData.trim().split("\n");
        JSONObject jsonObject = new JSONObject();
        String[] path =  reqs[0].split(" ");
        JSONObject jsonObjectHeaders = new JSONObject();
        int i = 0;
        for ( i = 1; i < reqs.length; i++) {
            String data = reqs[i];

            String[] kv = data.split(": ");
            if("".equals(data)||kv.length!=2){
                break;
            }

            jsonObjectHeaders.put(kv[0],kv[1]);

        }
        StringBuilder stringBuilder = new StringBuilder();
        for (; i <reqs.length ; i++) {
            stringBuilder.append("\n").append(reqs[i]);
        }
        jsonObject.put("request",
                new JsonObj()
                        .put("method",path[0])
                        .put("path",path[1])
                        .put("http_version",path[2])
                        .put("headers",jsonObjectHeaders)
                        .put("body",stringBuilder.toString().trim())
                        .toJson()
        );


        if(isResponse&&responseData!=null){

            String[] resp = responseData.trim().split("\n");
            String[] path2 =  resp[0].split(" ");

            jsonObjectHeaders = new JSONObject();
            i = 0;
            for ( i = 1; i < resp.length; i++) {
                String data = resp[i];
                String[] kv = data.split(": ");
                if("".equals(data)||kv.length!=2){
                    break;//到了导数第二行
                }

                jsonObjectHeaders.put(kv[0],kv[1]);
            }
            stringBuilder = new StringBuilder();

            for (; i <resp.length ; i++) {
                stringBuilder.append("\n").append(resp[i]);
            }
            jsonObject.put("response",
                    new JsonObj()
                            .put("state_msg",path2[2])
                            .put("state",path2[1])
                            .put("http_version",path2[0])
                            .put("headers",jsonObjectHeaders)
                            .put("body",stringBuilder.toString().trim())
                            .toJson()
            );
        }
        return Base64.getEncoder().encodeToString(jsonObject.toJSONString().getBytes());
    }
    private String convertRequestResponse(JSONObject jsonObject,IHttpRequestResponse messageInfo,boolean isResponse){
        return BurpExtender.gui.convert(jsonObject,isResponse);

     //   return BurpExtender.gui.convert(jsonObject,isResponse);
    }
    /**
     * requestIn阶段，收到客户端发送的加密request，进行解密并替换requestBody，使得BurpSuite中显示明文request；
     *
     * @param message
     */
    private void requestIn(IInterceptedProxyMessage message, String cmd) {

        IHttpRequestResponse messageInfo = message.getMessageInfo();
       // IRequestInfo requestInfo = BurpExtender.helpers.analyzeRequest(messageInfo.getRequest());
        String requestData = new String(messageInfo.getRequest());
        String cmdData = analyzeRequestResponse(requestData, null,false);
        String  result = Shell.exec(cmd+" "+ReqRep.REQUEST_RECEIVE+" "+cmdData);
        BurpExtender.stdout.println("执行的命令："+cmd+" "+ReqRep.REQUEST_RECEIVE+" "+cmdData);
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String resultData = convertRequestResponse(jsonObject,messageInfo,false);
            BurpExtender.stdout.println("收到请求后的数据包（Edited）："+resultData);
            messageInfo.setRequest(resultData.getBytes());
        }catch (Exception exception){
            exception.printStackTrace();
            BurpExtender.stdout.println("数据解析失败，可能由于解密脚本返回的数据格式不正确。"+exception.getMessage());
        }


    }

    //requestOut阶段，即将发送request到服务端，读取明文的request，重新进行加密（包括签名、编码、更新时间戳等），使得服务端正常解析；
    private void requestOut(IHttpRequestResponse messageInfo, int toolFlag, String cmd) {
        //IRequestInfo requestInfo = BurpExtender.callbacks.getHelpers().analyzeRequest(messageInfo);
        String requestData = new String(messageInfo.getRequest());
        BurpExtender.stdout.println("收到的请求包："+requestData);
        String cmdData = analyzeRequestResponse(requestData,null,false);
        String  result = Shell.exec(cmd+" "+ReqRep.REQUEST_SEND+" "+cmdData);
        BurpExtender.stdout.println("执行的命令："+cmd+" "+ReqRep.REQUEST_SEND+" "+cmdData);
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            int length = jsonObject.getJSONObject("request").getString("body").trim().getBytes(StandardCharsets.UTF_8).length;
            if(length!=0&&jsonObject.getJSONObject("request").getJSONObject("headers").getString("Content-Length")!=null){
               jsonObject.getJSONObject("request").getJSONObject("headers").put("Content-Length",length);//更新数据长度
            }
            String sendData = convertRequestResponse(jsonObject,messageInfo,false);

            BurpExtender.stdout.println("即将发送的请求包(Edited)："+sendData);

            messageInfo.setRequest(sendData.getBytes());
        }catch (Exception exception){
            exception.printStackTrace();
            BurpExtender.stdout.println("数据解析失败，可能由于解密脚本返回的数据格式不正确。"+exception.getMessage());
        }


    }

    //responseIn阶段，收到加密response，进行解密并替换responseBody，使得BurpSuite中显示明文response；
    private void responseIn(IHttpRequestResponse messageInfo, int toolFlag, String cmd) {
        String requestData = new String(messageInfo.getRequest());
        String responseData = new String(messageInfo.getResponse());
        String cmdData = analyzeRequestResponse(requestData,responseData,true);
        String  result = Shell.exec(cmd+" "+ReqRep.RESPONSE_RECEIVE+" "+cmdData);
        BurpExtender.stdout.println("执行的命令："+cmd+" "+ReqRep.RESPONSE_RECEIVE+" "+cmdData);
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String sendData = convertRequestResponse(jsonObject,messageInfo,true);
            BurpExtender.stdout.println("收到的响应包(Edited)："+sendData);
            messageInfo.setResponse(sendData.getBytes());
        }catch (Exception exception){
            exception.printStackTrace();
            BurpExtender.stdout.println("数据解析失败，可能由于解密脚本返回的数据格式不正确。"+exception.getMessage());
        }


    }

    //responseOut阶段，即将发送response到客户端，读取明文的response，重新进行加密，使得客户端正常解析。
    private void responseOut(IInterceptedProxyMessage message, String cmd) {
        IHttpRequestResponse messageInfo = message.getMessageInfo();
        String requestData = new String(messageInfo.getRequest());
        String responseData = new String(messageInfo.getResponse());
        String cmdData = analyzeRequestResponse(requestData,responseData,true);
        String  result = Shell.exec(cmd+" "+ReqRep.RESPONSE_SEND+" "+cmdData);
        BurpExtender.stdout.println("执行的命令："+cmd+" "+ReqRep.RESPONSE_SEND+" "+cmdData);
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String sendData = convertRequestResponse(jsonObject,messageInfo,true);
            BurpExtender.stdout.println("即将发出的响应包(Edited)："+sendData);
            messageInfo.setResponse(sendData.getBytes());
        }catch (Exception exception){
            exception.printStackTrace();
            BurpExtender.stdout.println("数据解析失败，可能由于解密脚本返回的数据格式不正确。"+exception.getMessage());
        }

    }
}
