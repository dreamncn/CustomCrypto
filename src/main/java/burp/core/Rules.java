package burp.core;

import burp.BurpExtender;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class Rules {
    private ArrayList<Rule> rule = null;
    private boolean auto = false;
    public Rules(){
        readRule();
    }
    public void readRule(){
        rule = Storage.read("rules");
        auto = Storage.readBoolean("auto");
    }
    public boolean getAuto(){
        return auto;
    }
    public void setAuto(boolean isAuto){
         auto = isAuto;
         Storage.writeBoolean("auto",isAuto);
    }
    public ArrayList<Rule> getAll(){
        rule = Storage.read("rules");
        return rule;
    }

    /**
     * 根据条件查找规则
     */
    public Rule findRule(String method,String url,String headers,String body){
        for (Rule r : getAll()) {
            if (r.inMethod(method) && r.inBody(body) && r.inHeader(headers) && r.inUrl(url)) {
                if(r.command.isEmpty())continue;
                return r;
            }
        }
        return null;
    }

    /**
     * 根据指定id获取执行的命令
     * @param id int
     */
    public String getRuleCommand(int id){
        if(id<rule.size()&&id>=0){
            return rule.get(id).command;
        }
        return null;
    }
    /**
     * 根据指定id获取执行的命令
     * @param id int
     */
    public Rule getRule(int id){
        if(id<rule.size()&&id>=0){
            return rule.get(id);
        }
        return null;
    }
    public void update(int id,Rule rule){
        if(id<this.rule.size()&&id>=0){
            this.rule.set(id,rule);
        }
        saveRule();

    }
    public void del(int id){
        if(id<this.rule.size()&&id>=0){
            this.rule.remove(id);
        }
        saveRule();
    }
    public void add(Rule rule){
        this.rule.add(rule);
        saveRule();
    }

    public boolean run(String command,CommandType type,String file){
        StringBuilder stringBuilder = new StringBuilder();
        String root = Storage.readString("root");
        command = command.replace("${root}",root);
        stringBuilder.append(command).append(" ");
        switch (type){
            case RequestFromClient:stringBuilder.append(0);break;
            case RequestToServer:stringBuilder.append(1);break;
            case ResponseToClient:stringBuilder.append(3);break;
            default:stringBuilder.append(2);break;
        }
        stringBuilder.append(" ").append(file);
        String result = exec(stringBuilder.toString()).trim();
        if("success".equals(result)){
            return true;
        }else{
            BurpExtender.print("命令执行异常："+result,1);
            return false;
        }
    }

    private String exec(String commands)  {
        BurpExtender.print("执行命令："+commands);
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

            return e.getMessage();
        }

    }

    private void saveRule(){

        Storage.write("rules",rule);
    }
    @Override
    protected void finalize() {
        saveRule();
    }
}
