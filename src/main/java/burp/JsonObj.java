package burp;

import com.alibaba.fastjson.JSONObject;

public class JsonObj {
    private final JSONObject jsonObject = new JSONObject();

    public JsonObj put(String k,Object v){
         jsonObject.put(k,v);
         return this;
    }

    public JSONObject toJson(){
        return jsonObject;
    }

}
