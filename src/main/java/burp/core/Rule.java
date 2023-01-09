package burp.core;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class Rule implements Serializable {
    public String name = "";
    public String url = "";
    public String header = "";
    public String body = "";
    public ArrayList<String> method = new ArrayList<>();
    public boolean regex = false;
    public String command = "";


    public boolean inMethod(String m){
        return method.size()==0||method.contains(m);
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

}
