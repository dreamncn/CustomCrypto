package burp.core;

import burp.BurpExtender;

import java.io.Serializable;
import java.util.ArrayList;

public class Rule implements Serializable {
    public String name = "";
    public String url = "";
    public String header = "";
    public String body = "";
    public ArrayList<String> method = new ArrayList<>();
    public boolean regex = false;
    public String command = "";


    public boolean inMethod(String m){
        return method.size()==0||method.contains(m.toLowerCase());
    }

    public boolean inUrl(String u){
        return compare(u,url);
    }
    public boolean inBody(String b){
        return compare(b,body);
    }
    public boolean inHeader(String h){
        return compare(h,header);
    }

    private boolean compare(String a,String b){
        if(b.equals(""))return true;
        if(regex){
            return  a.toLowerCase().matches(b);
        }else{
            return  a.toLowerCase().contains(b);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
