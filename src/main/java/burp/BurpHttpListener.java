package burp;


import burp.core.Process;
import burp.core.*;

import java.io.IOException;
import java.util.Objects;

public class BurpHttpListener implements IHttpListener, IProxyListener {

    HttpAgreement httpAgreement;
    BurpHttpListener() throws IOException {

    }
    /**
     * 检查是否需要拦截
     *
     */
    private boolean analyze(IHttpRequestResponse request, boolean messageIsRequest,String[] cmd,Process process) throws IOException {
        BurpExtender.print("分析请求中");
        if(!BurpExtender.rules.getAuto()){
            BurpExtender.print("根据设置不自动解密");
            return false;
        }
        String url = request.getHttpService().getProtocol() + "://" + request.getHttpService().getHost();
        if(!messageIsRequest){
            httpAgreement = new HttpAgreement(new String(request.getRequest()),new String(request.getResponse()),process);
        }else{
            httpAgreement = new HttpAgreement(new String(request.getRequest()),null,process);
        }
        url += httpAgreement.path;
        Rule rule = BurpExtender.rules.findRule(httpAgreement.method, url, httpAgreement.headers, httpAgreement.body);
        if(rule==null) {
            BurpExtender.print("没有合适的脚本允许解密");
            return false;
        }
        BurpExtender.print(String.format("脚本: %s 执行",rule.name));
        if(Objects.equals(rule.command, ""))return false;
        cmd[0] = rule.command;
        return true;
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
        BurpExtender.print("=================================================");
        //使用引用传递获取需要执行的命令
        //返回值标识是否需要拦截
        try {
            Process process = new Process();
            //不需要处理直接返回
            if (!analyze(messageInfo, messageIsRequest,cmd, process)){
                process.destroy();
                return;
            }
            if (messageIsRequest) {
                BurpExtender.print("======> 发送给服务端");
                requestOut(messageInfo, process, cmd[0]);//发送请求
            } else {
                BurpExtender.print("======> 收到服务端");
                responseIn(messageInfo, process, cmd[0]);//收到响应
            }

        } catch (IOException e) {
            e.printStackTrace();
            BurpExtender.print("错误信息："+e.getMessage(),1);
        }
        BurpExtender.print("=================================================");
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
        BurpExtender.print("=================================================");
        try {
            Process process = new Process();
            if (!analyze(message.getMessageInfo(), messageIsRequest, cmd,process)) {
                process.destroy();
                return;
            }

            if (messageIsRequest) {
                BurpExtender.print("======> 发送给客户端");
                requestIn(message, process, cmd[0]);//收到请求
            } else {
                BurpExtender.print("======> 收到客户端");
                responseOut(message,process, cmd[0]);//发送响应
            }

        } catch (IOException e) {
            e.printStackTrace();
            BurpExtender.print("错误信息："+e.getMessage(),1);
        }
        BurpExtender.print("=================================================");
    }

    /**
     * requestIn阶段，收到客户端发送的加密request，进行解密并替换requestBody，使得BurpSuite中显示明文request；
     *
     */
    private void requestIn(IInterceptedProxyMessage message,Process process, String cmd) {
        if(BurpExtender.rules.run(cmd, CommandType.RequestFromClient,process.getTemp())){
            message.getMessageInfo().setRequest(httpAgreement.toRequest(process).getBytes());
        }
    }
    //requestOut阶段，即将发送request到服务端，读取明文的request，重新进行加密（包括签名、编码、更新时间戳等），使得服务端正常解析；
    private void requestOut(IHttpRequestResponse messageInfo, Process process,String cmd) {
        if(BurpExtender.rules.run(cmd, CommandType.RequestToServer,process.getTemp())){
            messageInfo.setRequest(httpAgreement.toRequest(process).getBytes());
        }
    }

    //responseIn阶段，收到加密response，进行解密并替换responseBody，使得BurpSuite中显示明文response；
    private void responseIn(IHttpRequestResponse messageInfo, Process process, String cmd) {
        if(BurpExtender.rules.run(cmd, CommandType.ResponseFromServer,process.getTemp())){
            messageInfo.setResponse(httpAgreement.toResponse(process).getBytes());
        }
    }

    //responseOut阶段，即将发送response到客户端，读取明文的response，重新进行加密，使得客户端正常解析。
    private void responseOut(IInterceptedProxyMessage message, Process process,String cmd) {
        if(BurpExtender.rules.run(cmd, CommandType.ResponseToClient,process.getTemp())){
            message.getMessageInfo().setResponse(httpAgreement.toResponse(process).getBytes());
        }
    }
}
