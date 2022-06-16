package burp;


import java.util.Arrays;

public class BurpHttpListener implements  IHttpListener, IProxyListener  {


    /**
     * 检查是否需要拦截
     * @param request
     * @param cmd
     * @return
     */
   public boolean analyze(IHttpRequestResponse request,String[] cmd){
       IRequestInfo requestInfo = BurpExtender.helpers.analyzeRequest(request);
       String url = "";
       String testUrl = request.getHttpService().getProtocol()+"://"+request.getHttpService().getHost();

       if (requestInfo.getHeaders().size() > 0){
           String firstLine = requestInfo.getHeaders().get(0); //first line
           String[] tmp = firstLine.split(" ");
           if (tmp.length == 3){
               url = tmp[1];
               url = testUrl+url;
           }

       }

       if (!url.isEmpty()){
           if(!requestInfo.getMethod().equalsIgnoreCase("post") && !requestInfo.getMethod().equalsIgnoreCase("get"))return false;
           //使用引用传递获取需要执行的命令
           //返回值标识是否需要拦截
           return BurpExtender.gui.find(url, cmd);
       }

       return false;

   }

    /**
     * 当即将发出HTTP请求以及收到HTTP响应时，会调用此方法。
     *
     * @param toolFlag 指示发出请求的Burp工具的flag。
     * Burp工具 flags 定义在 <code>IBurpExtenderCallbacks</code> 接口.
     * @param messageIsRequest 是否为请求。
     * @param messageInfo 要处理的请求/回复的详细信息。 扩展可以调用此对象上的setter方法来更新当前消息，从而修改Burp的行为。
     */
    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        String[] cmd = new String[1];
        //使用引用传递获取需要执行的命令
        //返回值标识是否需要拦截
        if(!analyze(messageInfo, cmd))return;
        if (messageIsRequest) {
            requestOut(messageInfo,toolFlag,cmd[0]);//发送请求
        } else {
            responseIn(messageInfo,toolFlag,cmd[0]);//收到响应
        }
    }
    /**
     * 当代理处理HTTP消息时，会调用此方法。
     *
     * @param messageIsRequest 是否为请求。
     * @param message 扩展可用于查询和更新消息的详细信息，并控制消息是否应拦截并显示给用户进行手动审查或修改。
     */
    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        String[] cmd = new String[1];
        //使用引用传递获取需要执行的命令
        //返回值标识是否需要拦截
        if(!analyze(message.getMessageInfo(), cmd))return;
        if (messageIsRequest) {
            requestIn(message,cmd[0]);//收到请求
        } else {
            responseOut(message,cmd[0]);//发送响应
        }
    }
    /**
     * requestIn阶段，收到客户端发送的加密request，进行解密并替换requestBody，使得BurpSuite中显示明文request；
     * @param message
     */
    private void requestIn(IInterceptedProxyMessage message,String cmd) {

        IHttpRequestResponse messageInfo = message.getMessageInfo();
        IRequestInfo requestInfo = BurpExtender.helpers.analyzeRequest(messageInfo.getRequest());
        String requestData = new String(messageInfo.getRequest());
        if(requestInfo.getMethod().equalsIgnoreCase("get")) {
            //只处理get请求中的参数
            int index = requestData.indexOf("?");
            if(index<0)return;//没有参数，滚
            String query = requestData.substring(index);
            query = query.substring(0,query.indexOf(" "));//从第一个参数到http尾部有一个空格
            //获取到完整的参数
            String queryEncrypt = BurpExtender.gui.run(cmd,ReqRep.REQUEST_RECEIVE,query);
            requestData = requestData.replace(query,queryEncrypt);//替换掉
            messageInfo.setRequest(requestData.getBytes());
        }else{
            int index = requestData.indexOf("?");
            String query = "";
            if(index>0){
                query = requestData.substring(index);
                query = query.substring(0,query.indexOf(" "));//从第一个参数到http尾部有一个空格
            }
            String body = requestData.substring(requestInfo.getBodyOffset());
            String sendData = query+" "+body;
            String result = BurpExtender.gui.run(cmd,ReqRep.REQUEST_RECEIVE,sendData);

            String[] resultArray = result.split(" ");
            BurpExtender.stdout.println(Arrays.toString(resultArray));
            String replaceBody;
            if(resultArray.length==2){
                replaceBody = resultArray[1];
                if(!query.equals(resultArray[0]))
                requestData = requestData.replace(query,resultArray[0]);//替换掉
            }else{
                replaceBody = resultArray[0];
            }
            if(!body.equals(replaceBody)) requestData = requestData.replace(body,replaceBody);//替换掉
            messageInfo.setRequest(requestData.getBytes());


        }

    }
    //requestOut阶段，即将发送request到服务端，读取明文的request，重新进行加密（包括签名、编码、更新时间戳等），使得服务端正常解析；
    private void requestOut(IHttpRequestResponse messageInfo,int toolFlag,String cmd) {
        IRequestInfo requestInfo = BurpExtender.callbacks.getHelpers().analyzeRequest(messageInfo);
        String requestData = new String(messageInfo.getRequest());
        if(requestInfo.getMethod().equalsIgnoreCase("get")){
            //只处理get请求中的参数
            int index = requestData.indexOf("?");
            if(index<0)return;//没有参数，滚
            String query = requestData.substring(index);
            query = query.substring(0,query.indexOf(" "));//从第一个参数到http尾部有一个空格
            //获取到完整的参数
            String queryEncrypt = BurpExtender.gui.run(cmd,ReqRep.REQUEST_SEND,query);
            requestData = requestData.replace(query,queryEncrypt);//替换掉
            messageInfo.setRequest(requestData.getBytes());
        }else{//处理post请求
            int index = requestData.indexOf("?");
            String query = "";
            if(index>0){
                query = requestData.substring(index);
                query = query.substring(0,query.indexOf(" "));//从第一个参数到http尾部有一个空格
            }
            String body = requestData.substring(requestInfo.getBodyOffset());
            String sendData = query+" "+body;
            String result = BurpExtender.gui.run(cmd,ReqRep.REQUEST_SEND,sendData);



            String[] resultArray = result.split(" ");
            String replaceBody;
            if(resultArray.length==2){
                replaceBody = resultArray[1];
                if(!query.equals(resultArray[0]))
                    requestData = requestData.replace(query,resultArray[0]);//替换掉
            }else{
                replaceBody = resultArray[0];
            }

            if(!body.equals(replaceBody)){
                int length = replaceBody.length();
                String rawLength="";
                for (String header : requestInfo.getHeaders()) {
                    if (header.startsWith("Content-Length:")) {
                        rawLength = header;
                        break;
                    }
                }
                requestData = requestData.replace(body,replaceBody);
                requestData = requestData.replace(rawLength,"Content-Length:"+length);
            }

            messageInfo.setRequest(requestData.getBytes());
        }
    }
    //responseIn阶段，收到加密response，进行解密并替换responseBody，使得BurpSuite中显示明文response；
    private void responseIn(IHttpRequestResponse messageInfo,int toolFlag,String cmd) {
        IResponseInfo responseInfo = BurpExtender.callbacks.getHelpers().analyzeResponse(messageInfo.getResponse());
        String responseData = new String(messageInfo.getResponse());
        //获取body
        String body = responseData.substring(responseInfo.getBodyOffset());

        //获取header部分
        String header = responseData.substring(0,responseInfo.getBodyOffset());

        //body为空不处理
        if(body.equals("")){
            return;
        }
        //原始的body进行解密
        body = BurpExtender.gui.run(cmd,ReqRep.RESPONSE_RECEIVE,body);

        //更新解密后的body
        messageInfo.setResponse((header+body).getBytes());
    }
    //responseOut阶段，即将发送response到客户端，读取明文的response，重新进行加密，使得客户端正常解析。
    private void responseOut(IInterceptedProxyMessage message,String cmd) {

        IHttpRequestResponse messageInfo  =  message.getMessageInfo();
        IResponseInfo responseInfo = BurpExtender.callbacks.getHelpers().analyzeResponse(messageInfo.getResponse());
        String responseData = new String(messageInfo.getResponse());
        //获取body
        String body = responseData.substring(responseInfo.getBodyOffset());

        //获取header部分
        String header = responseData.substring(0,responseInfo.getBodyOffset());

        //body为空不处理
        if(body.equals("")){
            return;
        }
        //原始的body进行解密
        body = BurpExtender.gui.run(cmd,ReqRep.RESPONSE_SEND,body);

        //更新解密后的body
        messageInfo.setResponse((header+body).getBytes());
    }
}
