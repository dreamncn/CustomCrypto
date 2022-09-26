/*
 * Created by JFormDesigner on Mon Sep 19 12:24:48 CST 2022
 */

package burp;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Base64;

/**
 * 插件的GUI实现
 * @author ankio
 */
public class MainGUI extends JPanel {
    
    /**
     * 构造函数
     */
    public MainGUI() {
        //初始化UI
        initComponents();
        //初始化数据
        initData();

    }
    private JSONArray runListData = new JSONArray();
    
    private int selected = -1;

    /**
     * 初始化数据
     */
    private void initData(){
        emptyWatch();
        //监控配置
        runListData = new JSONArray();
      
        //加载正在运行的
        JSONArray jsonArray = Storage.read(Storage.RUN_LIST);

        ArrayList<String> arrayList = new ArrayList<>();

        
        for (int i = 0; i < jsonArray.size() ; i++) {
            JSONObject jsonObject =  jsonArray.getJSONObject(i);
            if(jsonObject.getBoolean("use")){
                arrayList.add("已激活 • "+jsonObject.getString("title"));
            }else{
                arrayList.add("禁用 • "+jsonObject.getString("title"));
            }
            runListData.add(jsonObject);
        }
        

        watchList.setListData(arrayList.toArray(new String[0]));
    }

   

    /**
     * 获取根View
     * @return
     */
    public JTabbedPane getRoot(){
        return tabbedPane1;
    }

    private void watchListValueChanged(ListSelectionEvent e) {
        if (watchList.getSelectedIndex() == -1) return;
        int index = watchList.getSelectedIndex();
        if(index>runListData.size())return;
        selected = index;
        JSONObject data =  runListData.getJSONObject(index);
        watchName.setText(data.getString("title"));
        watchUrlInclude.setText(data.getString("watchUrlInclude"));

        watchReqHeadInclude.setText(data.getString("watchReqHeadInclude"));
        watchReqBodyInclude.setText(data.getString("watchReqBodyInclude"));

        watchRespHeadInclude.setText(data.getString("watchRespHeadInclude"));
        watchRespBodyInclude.setText(data.getString("watchRespBodyInclude"));

        watchCustom.setText(data.getString("watchCustom"));


        watchUrlIncludeSelect.setSelected(data.getBoolean("watchUrlIncludeSelect"));
        watchReqHeadIncludeSelect.setSelected(data.getBoolean("watchReqHeadIncludeSelect"));
        watchReqBodyIncludeSelect.setSelected(data.getBoolean("watchReqBodyIncludeSelect"));
        watchRespHeadIncludeSelect.setSelected(data.getBoolean("watchRespHeadIncludeSelect"));
        watchRespBodyIncludeSelect.setSelected(data.getBoolean("watchRespBodyIncludeSelect"));

        watchUseRegex.setSelected(data.getBoolean("watchUseRegex"));

        enableScript.setSelected(data.getBoolean("enableScript"));
        disableScript.setSelected(data.getBoolean("disableScript"));


        watchGET.setSelected(data.getBoolean("watchGET"));
        watchPOST.setSelected(data.getBoolean("watchPOST"));
        watchOPTIONS.setSelected(data.getBoolean("watchOPTIONS"));
        watchHEAD.setSelected(data.getBoolean("watchHEAD"));
        watchPUT.setSelected(data.getBoolean("watchPUT"));
        watchDELETE.setSelected(data.getBoolean("watchDELETE"));
        
    }

    private void emptyWatch(){
        selected = -1;
        watchName.setText("");
        watchUrlInclude.setText("");

        watchReqHeadInclude.setText("");
        watchReqBodyInclude.setText("");

        watchRespHeadInclude.setText("");
        watchRespBodyInclude.setText("");

        watchCustom.setText("");


        watchUrlIncludeSelect.setSelected(false);
        watchReqHeadIncludeSelect.setSelected(false);
        watchReqBodyIncludeSelect.setSelected(false);
        watchRespHeadIncludeSelect.setSelected(false);
        watchRespBodyIncludeSelect.setSelected(false);

        watchUseRegex.setSelected(false);
        enableScript.setSelected(true);
        disableScript.setSelected(false);


        watchGET.setSelected(false);
        watchPOST.setSelected(false);
        watchOPTIONS.setSelected(false);
        watchHEAD.setSelected(false);
        watchPUT.setSelected(false);
        watchDELETE.setSelected(false);

    }

    /**
     * 显示错误信息
     * @param msg
     */
    private void showMsg(String msg){
        JOptionPane.showMessageDialog(null, msg, "错误",
                JOptionPane.ERROR_MESSAGE);
    }
    private JSONObject save(){
        JSONObject jsonObject = new JSONObject();
        String title = watchName.getText();
        if("".equals(title)){
            showMsg("配置名称不允许为空！");
            return null;
        }

        if(!watchUrlIncludeSelect.isSelected()&&
                !watchReqHeadIncludeSelect.isSelected()&&
                !watchReqBodyIncludeSelect.isSelected()&&
                !watchRespHeadIncludeSelect.isSelected()&&
                !watchRespBodyIncludeSelect.isSelected()){
            showMsg("至少要选择一项监控参数！");
            return null;
        }

        if((watchUrlIncludeSelect.isSelected()&&"".equals(watchUrlInclude.getText()))||
                (watchReqHeadIncludeSelect.isSelected()&&"".equals(watchReqHeadInclude.getText()))||
                (watchReqBodyIncludeSelect.isSelected()&&"".equals(watchReqBodyInclude.getText()))||
                (watchRespHeadIncludeSelect.isSelected()&&"".equals(watchRespHeadInclude.getText()))||
                (watchRespBodyIncludeSelect.isSelected()&&"".equals(watchRespBodyInclude.getText()))){
            showMsg("监控参数不允许为空！");
            return null;
        }

        if("".equals(watchCustom.getText())){
            showMsg("需要填写具体的执行命令！");
            return null;
        }
        jsonObject.put("title",title);
        jsonObject.put("watchUrlInclude",watchUrlInclude.getText());
        jsonObject.put("watchReqHeadInclude",watchReqHeadInclude.getText());
        jsonObject.put("watchReqBodyInclude",watchReqBodyInclude.getText());
        jsonObject.put("watchRespHeadInclude",watchRespHeadInclude.getText());
        jsonObject.put("watchRespBodyInclude",watchRespBodyInclude.getText());
        jsonObject.put("watchCustom",watchCustom.getText());


        jsonObject.put("watchUrlIncludeSelect",watchUrlIncludeSelect.isSelected());
        jsonObject.put("watchReqHeadIncludeSelect",watchReqHeadIncludeSelect.isSelected());
        jsonObject.put("watchReqBodyIncludeSelect",watchReqBodyIncludeSelect.isSelected());
        jsonObject.put("watchRespHeadIncludeSelect",watchRespHeadIncludeSelect.isSelected());
        jsonObject.put("watchRespBodyIncludeSelect",watchRespBodyIncludeSelect.isSelected());
        jsonObject.put("watchUseRegex",watchUseRegex.isSelected());
        
        
        jsonObject.put("command",watchCustom.getText());

        jsonObject.put("enableScript",enableScript.isSelected());
        jsonObject.put("disableScript",disableScript.isSelected());


        jsonObject.put("watchGET",watchGET.isSelected());
        jsonObject.put("watchPOST",watchPOST.isSelected());
        jsonObject.put("watchOPTIONS",watchOPTIONS.isSelected());
        jsonObject.put("watchHEAD",watchHEAD.isSelected());
        jsonObject.put("watchPUT",watchPUT.isSelected());
        jsonObject.put("watchDELETE",watchDELETE.isSelected());
        jsonObject.put("use",enableScript.isSelected());
        return jsonObject;
    }
    private void watchSave(ActionEvent e) {
        JSONObject jsonObject = save();
        if(jsonObject!=null){

            if(selected==-1){
                //新增
                runListData.add(jsonObject);
            }else{
                //更新
                runListData.set(selected,jsonObject);
            }
            
            Storage.write(Storage.RUN_LIST,runListData);
            initData();
        }
       

    }

    private void button3ItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private String testCommand = "";
    private void watchTest(ActionEvent e) {
        JSONObject jsonObject = save();
        if(jsonObject!=null){
            dialog1.setVisible(true);
            testCommand = jsonObject.getString("command");
        }
        
        
    }

    private void watchDel(ActionEvent e) {
        if(selected!=-1){
            //新增
            runListData.remove(selected);
        }
        Storage.write(Storage.RUN_LIST,runListData);
        initData();
        
    }
    
    
    private String getSendData(boolean needResponse ){

        String req = sendRequestBody.getText();
        if("".equals(req)){
            showMsg("请先填写请求包内容");
            return null;
        }
        String[] reqs = req.split("\n");
        if(reqs.length<=1){
            showMsg("请求包必须包含请求方法和请求体");
            return null;
        }
        JSONObject jsonObject = new JSONObject();
       String[] path =  reqs[0].split(" ");
       if(path.length!=3){
           showMsg("请求头存在错误，请求头应该由 请求方法、路径、使用协议组成。");
           return null;
       }
        JSONObject jsonObjectHeaders = new JSONObject();
        int i = 0;
        for ( i = 1; i < reqs.length; i++) {
            String data = reqs[i];
            String[] kv = data.split(": ");
            if("".equals(data)){
                break;//到了导数第二行
            }
            if(kv.length!=2){
                showMsg("Headers信息存在错误");
                return null;
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


        if(needResponse){

            String res = sendResponseBody.getText();
            if("".equals(res)){
                showMsg("请先填写响应包内容");
                return null;
            }

            String[] resp = res.split("\n");
            if(resp.length<=1){
                showMsg("响应包必须包含响应头和响应体");
                return null;
            }

            String[] path2 =  resp[0].split(" ");
            if(path2.length!=3){
                showMsg("响应头存在错误，响应头应该由 响应协议、响应码、响应状态。");
                return null;
            }
            jsonObjectHeaders = new JSONObject();
             i = 0;
            for ( i = 1; i < resp.length; i++) {
                String data = resp[i];
                String[] kv = data.split(": ");
                if("".equals(data)){
                    break;//到了导数第二行
                }
                if(kv.length!=2){
                    showMsg("响应包Headers信息存在错误");
                    return null;
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

        return jsonObject.toJSONString();
    }

    String convert(JSONObject jsonObject, boolean response){
        
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject request = null;
        if(!response){
             request = jsonObject.getJSONObject("request");
            stringBuilder.append(request.getString("method")).append(" ").append(request.getString("path")).append(" ").append(request.getString("http_version").trim()).append("\r\n");
            
        }else{
             request = jsonObject.getJSONObject("response");
            stringBuilder.append(request.getString("http_version")).append(" ").append(request.getString("state")).append(" ").append(request.getString("state_msg").trim()).append("\r\n");
        }
        JSONObject headers =  request.getJSONObject("headers");
        for (String key : headers.keySet()) {
            // 替换headers之后请求异常RST_STREAM
            stringBuilder.append(key).append(": ").append(headers.getString(key).trim()).append("\r\n");
        }
        stringBuilder.append("\r\n");
        stringBuilder.append(request.getString("body").trim());
        return stringBuilder.toString();
    }

    private void sendRequestBurp(ActionEvent e) {

        String data = getSendData(false);
        if(data == null)return;
        String result = "";
        try {
            String out  = testCommand+" 0 " + Base64.getEncoder().encodeToString(data.getBytes());
             result =  Shell.exec(out);
             JSONObject jsonObject = JSONObject.parseObject(result);
            result = convert(jsonObject,false);

        } catch (Exception ex) {
            ex.printStackTrace();
             result = ex.getMessage();
        } finally {
            sendRequestBurpData.setText(result);
            tab.setSelectedIndex(2);
        }
        

    }

    private void sendRequestServer(ActionEvent e) {
        String data = getSendData(false);
        if(data == null)return;
        String result = "";
        try {
            String out  = testCommand+" 1 " + Base64.getEncoder().encodeToString(data.getBytes());
            result =  Shell.exec(out);
            JSONObject jsonObject = JSONObject.parseObject(result);
            result = convert(jsonObject,false);

        } catch (Exception ex) {
            ex.printStackTrace();
            result = ex.getMessage();
        } finally {
            sendRequestServerData.setText(result);
            tab.setSelectedIndex(3);
        }
    }

    private void sendResponseBurp(ActionEvent e) {
        String data = getSendData(true);
        if(data == null)return;
        String result = "";
        try {
            String out  = testCommand+" 2 " + Base64.getEncoder().encodeToString(data.getBytes());
            result =  Shell.exec(out);
            JSONObject jsonObject = JSONObject.parseObject(result);
            result = convert(jsonObject,true);

        } catch (Exception ex) {
            ex.printStackTrace();
            result = ex.getMessage();
        } finally {
            sendResponseBurpData.setText(result);
            tab.setSelectedIndex(4);
        }
    }

    private void sendResponseClient(ActionEvent e) {
        String data = getSendData(true);
        if(data == null)return;
        String result = "";
        try {
            String out  = testCommand+" 3 " + Base64.getEncoder().encodeToString(data.getBytes());
            result =  Shell.exec(out);
            JSONObject jsonObject = JSONObject.parseObject(result);
            result = convert(jsonObject,true);

        } catch (Exception ex) {
            ex.printStackTrace();
            result = ex.getMessage();
        } finally {
            sendResponseClientData.setText(result);
            tab.setSelectedIndex(5);
        }
    }


    public JSONArray getUsageList() {
        return runListData;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        tabbedPane1 = new JTabbedPane();
        watch = new JPanel();
        scrollPane1 = new JScrollPane();
        watchList = new JList<>();
        panel2 = new JPanel();
        panel3 = new JPanel();
        watchUrlIncludeSelect = new JCheckBox();
        watchUrlInclude = new JTextField();
        watchReqHeadIncludeSelect = new JCheckBox();
        watchReqHeadInclude = new JTextField();
        watchReqBodyIncludeSelect = new JCheckBox();
        watchReqBodyInclude = new JTextField();
        watchRespHeadIncludeSelect = new JCheckBox();
        watchRespHeadInclude = new JTextField();
        watchRespBodyIncludeSelect = new JCheckBox();
        watchRespBodyInclude = new JTextField();
        watchUseRegex = new JCheckBox();
        panel5 = new JPanel();
        watchGET = new JCheckBox();
        watchPOST = new JCheckBox();
        watchOPTIONS = new JCheckBox();
        watchHEAD = new JCheckBox();
        watchPUT = new JCheckBox();
        watchDELETE = new JCheckBox();
        panel4 = new JPanel();
        watchCustom = new JTextField();
        label8 = new JLabel();
        label7 = new JLabel();
        watchName = new JTextField();
        watchSave = new JButton();
        watchTest = new JButton();
        watchDel = new JButton();
        panel6 = new JPanel();
        enableScript = new JRadioButton();
        disableScript = new JRadioButton();
        Setting = new JPanel();
        label1 = new JLabel();
        dialog1 = new JDialog();
        tab = new JTabbedPane();
        panel7 = new JPanel();
        scrollPane4 = new JScrollPane();
        sendRequestBody = new JEditorPane();
        panel8 = new JPanel();
        scrollPane5 = new JScrollPane();
        sendResponseBody = new JEditorPane();
        panel9 = new JPanel();
        scrollPane6 = new JScrollPane();
        sendRequestBurpData = new JEditorPane();
        panel10 = new JPanel();
        scrollPane7 = new JScrollPane();
        sendRequestServerData = new JEditorPane();
        panel11 = new JPanel();
        sendResponseBurpData = new JEditorPane();
        panel12 = new JPanel();
        sendResponseClientData = new JEditorPane();
        panel1 = new JPanel();
        sendRequestBurp = new JButton();
        sendRequestServer = new JButton();
        sendResponseBurp = new JButton();
        sendResponseClient = new JButton();

        //======== tabbedPane1 ========
        {

            //======== watch ========
            {
                watch.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border. EmptyBorder(
                0, 0, 0, 0) , "", javax. swing. border. TitledBorder. CENTER, javax. swing. border. TitledBorder
                . BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .awt .Font .BOLD ,12 ), java. awt. Color.
                red) ,watch. getBorder( )) ); watch. addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java .
                beans .PropertyChangeEvent e) {if ("\u0062ord\u0065r" .equals (e .getPropertyName () )) throw new RuntimeException( ); }} );
                watch.setLayout(new BorderLayout());

                //======== scrollPane1 ========
                {
                    scrollPane1.setMaximumSize(new Dimension(200, 32767));

                    //---- watchList ----
                    watchList.setMaximumSize(new Dimension(200, 62));
                    watchList.setFixedCellWidth(200);
                    watchList.addListSelectionListener(e -> watchListValueChanged(e));
                    scrollPane1.setViewportView(watchList);
                }
                watch.add(scrollPane1, BorderLayout.WEST);

                //======== panel2 ========
                {
                    panel2.setBorder(new EmptyBorder(20, 20, 20, 20));

                    //======== panel3 ========
                    {
                        panel3.setBorder(new TitledBorder("\u76d1\u63a7\u53c2\u6570"));

                        //---- watchUrlIncludeSelect ----
                        watchUrlIncludeSelect.setText("URL\u5305\u542b");

                        //---- watchReqHeadIncludeSelect ----
                        watchReqHeadIncludeSelect.setText("\u8bf7\u6c42\u5934\u5305\u542b");

                        //---- watchReqBodyIncludeSelect ----
                        watchReqBodyIncludeSelect.setText("\u8bf7\u6c42\u5305\u5305\u542b");

                        //---- watchRespHeadIncludeSelect ----
                        watchRespHeadIncludeSelect.setText("\u54cd\u5e94\u5934\u5305\u542b");

                        //---- watchRespBodyIncludeSelect ----
                        watchRespBodyIncludeSelect.setText("\u54cd\u5e94\u5305\u5305\u542b");

                        //---- watchUseRegex ----
                        watchUseRegex.setText("\u6240\u6709\u5339\u914d\u89c4\u5219\u91c7\u7528\u6b63\u5219\u6a21\u5f0f");

                        //======== panel5 ========
                        {
                            panel5.setBorder(new TitledBorder("\u8bf7\u6c42\u7c7b\u578b"));

                            //---- watchGET ----
                            watchGET.setText("GET");

                            //---- watchPOST ----
                            watchPOST.setText("POST");

                            //---- watchOPTIONS ----
                            watchOPTIONS.setText("OPTIONS");

                            //---- watchHEAD ----
                            watchHEAD.setText("HEAD");

                            //---- watchPUT ----
                            watchPUT.setText("PUT");

                            //---- watchDELETE ----
                            watchDELETE.setText("DELETE");

                            GroupLayout panel5Layout = new GroupLayout(panel5);
                            panel5.setLayout(panel5Layout);
                            panel5Layout.setHorizontalGroup(
                                panel5Layout.createParallelGroup()
                                    .addGroup(panel5Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(watchGET)
                                        .addGap(18, 18, 18)
                                        .addComponent(watchPOST)
                                        .addGap(18, 18, 18)
                                        .addComponent(watchOPTIONS)
                                        .addGap(18, 18, 18)
                                        .addComponent(watchHEAD)
                                        .addGap(18, 18, 18)
                                        .addComponent(watchPUT)
                                        .addGap(18, 18, 18)
                                        .addComponent(watchDELETE)
                                        .addContainerGap(55, Short.MAX_VALUE))
                            );
                            panel5Layout.setVerticalGroup(
                                panel5Layout.createParallelGroup()
                                    .addGroup(panel5Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(watchGET)
                                            .addComponent(watchPOST)
                                            .addComponent(watchOPTIONS)
                                            .addComponent(watchHEAD)
                                            .addComponent(watchPUT)
                                            .addComponent(watchDELETE))
                                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            );
                        }

                        GroupLayout panel3Layout = new GroupLayout(panel3);
                        panel3.setLayout(panel3Layout);
                        panel3Layout.setHorizontalGroup(
                            panel3Layout.createParallelGroup()
                                .addGroup(panel3Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel3Layout.createParallelGroup()
                                        .addGroup(panel3Layout.createSequentialGroup()
                                            .addComponent(watchUrlIncludeSelect)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(watchUrlInclude))
                                        .addGroup(panel3Layout.createSequentialGroup()
                                            .addComponent(watchReqHeadIncludeSelect)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(watchReqHeadInclude))
                                        .addGroup(panel3Layout.createSequentialGroup()
                                            .addComponent(watchReqBodyIncludeSelect)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(watchReqBodyInclude))
                                        .addGroup(panel3Layout.createSequentialGroup()
                                            .addComponent(watchRespHeadIncludeSelect)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(watchRespHeadInclude))
                                        .addGroup(panel3Layout.createSequentialGroup()
                                            .addComponent(watchRespBodyIncludeSelect)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(watchRespBodyInclude))
                                        .addComponent(watchUseRegex, GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                                        .addComponent(panel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap())
                        );
                        panel3Layout.setVerticalGroup(
                            panel3Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(panel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                                    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(watchUrlIncludeSelect)
                                        .addComponent(watchUrlInclude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(watchReqHeadIncludeSelect)
                                        .addComponent(watchReqHeadInclude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(watchReqBodyIncludeSelect)
                                        .addComponent(watchReqBodyInclude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(watchRespHeadIncludeSelect)
                                        .addComponent(watchRespHeadInclude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(watchRespBodyIncludeSelect)
                                        .addComponent(watchRespBodyInclude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(watchUseRegex)
                                    .addContainerGap())
                        );
                    }

                    //======== panel4 ========
                    {
                        panel4.setBorder(new TitledBorder("\u811a\u672c\u914d\u7f6e"));

                        //---- label8 ----
                        label8.setText("\u6267\u884c\u547d\u4ee4\uff1a");

                        //---- label7 ----
                        label7.setText("\u914d\u7f6e\u540d\u79f0\uff1a");

                        GroupLayout panel4Layout = new GroupLayout(panel4);
                        panel4.setLayout(panel4Layout);
                        panel4Layout.setHorizontalGroup(
                            panel4Layout.createParallelGroup()
                                .addGroup(panel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel4Layout.createParallelGroup()
                                        .addGroup(panel4Layout.createSequentialGroup()
                                            .addComponent(label8)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(watchCustom, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE))
                                        .addGroup(panel4Layout.createSequentialGroup()
                                            .addComponent(label7)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(watchName, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)))
                                    .addContainerGap())
                        );
                        panel4Layout.setVerticalGroup(
                            panel4Layout.createParallelGroup()
                                .addGroup(panel4Layout.createSequentialGroup()
                                    .addGap(8, 8, 8)
                                    .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label7)
                                        .addComponent(watchName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label8)
                                        .addComponent(watchCustom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(0, 4, Short.MAX_VALUE))
                        );
                    }

                    //---- watchSave ----
                    watchSave.setText("\u4fdd\u5b58");
                    watchSave.addActionListener(e -> watchSave(e));

                    //---- watchTest ----
                    watchTest.setText("\u6d4b\u8bd5");
                    watchTest.addItemListener(e -> button3ItemStateChanged(e));
                    watchTest.addActionListener(e -> watchTest(e));

                    //---- watchDel ----
                    watchDel.setText("\u5220\u9664");
                    watchDel.addActionListener(e -> watchDel(e));

                    //======== panel6 ========
                    {
                        panel6.setBorder(new TitledBorder("\u811a\u672c\u72b6\u6001"));

                        //---- enableScript ----
                        enableScript.setText("\u542f\u7528");
                        enableScript.setSelected(true);

                        //---- disableScript ----
                        disableScript.setText("\u7981\u7528");

                        GroupLayout panel6Layout = new GroupLayout(panel6);
                        panel6.setLayout(panel6Layout);
                        panel6Layout.setHorizontalGroup(
                            panel6Layout.createParallelGroup()
                                .addGroup(panel6Layout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(enableScript)
                                    .addGap(18, 18, 18)
                                    .addComponent(disableScript)
                                    .addContainerGap(396, Short.MAX_VALUE))
                        );
                        panel6Layout.setVerticalGroup(
                            panel6Layout.createParallelGroup()
                                .addGroup(panel6Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(disableScript)
                                        .addComponent(enableScript))
                                    .addContainerGap(7, Short.MAX_VALUE))
                        );
                    }

                    GroupLayout panel2Layout = new GroupLayout(panel2);
                    panel2.setLayout(panel2Layout);
                    panel2Layout.setHorizontalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addComponent(watchSave)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(watchTest)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(watchDel)
                                .addGap(0, 298, Short.MAX_VALUE))
                            .addComponent(panel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                    );
                    panel2Layout.setVerticalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                .addComponent(panel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(watchSave)
                                    .addComponent(watchTest)
                                    .addComponent(watchDel))
                                .addGap(41, 41, 41))
                    );
                }
                watch.add(panel2, BorderLayout.CENTER);
            }
            tabbedPane1.addTab("\u76d1\u63a7\u914d\u7f6e", watch);

            //======== Setting ========
            {
                Setting.setBorder(new EmptyBorder(20, 20, 20, 20));
                Setting.setLayout(new BorderLayout());

                //---- label1 ----
                label1.setText("<Html><h1>CustomCrypto</h1><h2>burp\u81ea\u5b9a\u4e49\u52a0\u89e3\u5bc6\u63d2\u4ef6</h2><h3>Github: https://github.com/dreamncn/CustomCrypto</h3><h3>Site: https://ankio.net</h3><h3>Powered by Ankio 2022.</h3>");
                Setting.add(label1, BorderLayout.NORTH);
            }
            tabbedPane1.addTab("\u5173\u4e8e", Setting);
        }

        //======== dialog1 ========
        {
            dialog1.setTitle("\u6d4b\u8bd5\u811a\u672c");
            Container dialog1ContentPane = dialog1.getContentPane();
            dialog1ContentPane.setLayout(new BorderLayout());

            //======== tab ========
            {

                //======== panel7 ========
                {
                    panel7.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new
                    javax. swing. border. EmptyBorder( 0, 0, 0, 0) , "", javax
                    . swing. border. TitledBorder. CENTER, javax. swing. border. TitledBorder. BOTTOM, new java
                    .awt .Font ("Dia\u006cog" ,java .awt .Font .BOLD ,12 ), java. awt
                    . Color. red) ,panel7. getBorder( )) ); panel7. addPropertyChangeListener (new java. beans.
                    PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("bord\u0065r" .
                    equals (e .getPropertyName () )) throw new RuntimeException( ); }} );
                    panel7.setLayout(new BorderLayout());

                    //======== scrollPane4 ========
                    {
                        scrollPane4.setViewportView(sendRequestBody);
                    }
                    panel7.add(scrollPane4, BorderLayout.CENTER);
                }
                tab.addTab("\u539f\u59cb\u8bf7\u6c42\u5305", panel7);

                //======== panel8 ========
                {
                    panel8.setLayout(new BorderLayout());

                    //======== scrollPane5 ========
                    {
                        scrollPane5.setViewportView(sendResponseBody);
                    }
                    panel8.add(scrollPane5, BorderLayout.CENTER);
                }
                tab.addTab("\u539f\u59cb\u54cd\u5e94\u5305", panel8);

                //======== panel9 ========
                {
                    panel9.setLayout(new BorderLayout());

                    //======== scrollPane6 ========
                    {
                        scrollPane6.setViewportView(sendRequestBurpData);
                    }
                    panel9.add(scrollPane6, BorderLayout.CENTER);
                }
                tab.addTab("\u65e5\u5fd7/Interrupt\u6536\u5230\u8bf7\u6c42", panel9);

                //======== panel10 ========
                {
                    panel10.setLayout(new BorderLayout());

                    //======== scrollPane7 ========
                    {
                        scrollPane7.setViewportView(sendRequestServerData);
                    }
                    panel10.add(scrollPane7, BorderLayout.CENTER);
                }
                tab.addTab("Repeater/Interrupt\u53d1\u51fa\u8bf7\u6c42", panel10);

                //======== panel11 ========
                {

                    GroupLayout panel11Layout = new GroupLayout(panel11);
                    panel11.setLayout(panel11Layout);
                    panel11Layout.setHorizontalGroup(
                        panel11Layout.createParallelGroup()
                            .addGroup(panel11Layout.createParallelGroup()
                                .addGroup(panel11Layout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(sendResponseBurpData, GroupLayout.PREFERRED_SIZE, 1219, GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE)))
                            .addGap(0, 1223, Short.MAX_VALUE)
                    );
                    panel11Layout.setVerticalGroup(
                        panel11Layout.createParallelGroup()
                            .addGroup(panel11Layout.createParallelGroup()
                                .addGroup(panel11Layout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(sendResponseBurpData, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE)))
                            .addGap(0, 299, Short.MAX_VALUE)
                    );
                }
                tab.addTab("\u65e5\u5fd7/Repeater/Interrupt\u6536\u5230\u54cd\u5e94", panel11);

                //======== panel12 ========
                {

                    GroupLayout panel12Layout = new GroupLayout(panel12);
                    panel12.setLayout(panel12Layout);
                    panel12Layout.setHorizontalGroup(
                        panel12Layout.createParallelGroup()
                            .addGroup(panel12Layout.createParallelGroup()
                                .addGroup(panel12Layout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(sendResponseClientData, GroupLayout.PREFERRED_SIZE, 1219, GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE)))
                            .addGap(0, 1223, Short.MAX_VALUE)
                    );
                    panel12Layout.setVerticalGroup(
                        panel12Layout.createParallelGroup()
                            .addGroup(panel12Layout.createParallelGroup()
                                .addGroup(panel12Layout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(sendResponseClientData, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE)))
                            .addGap(0, 299, Short.MAX_VALUE)
                    );
                }
                tab.addTab("Repeater/Interrupt\u53d1\u51fa\u54cd\u5e94", panel12);
            }
            dialog1ContentPane.add(tab, BorderLayout.CENTER);

            //======== panel1 ========
            {
                panel1.setLayout(new FlowLayout());

                //---- sendRequestBurp ----
                sendRequestBurp.setText("\u65e5\u5fd7/Interrupt\u6536\u5230\u8bf7\u6c42\uff08Burp\uff09");
                sendRequestBurp.addActionListener(e -> sendRequestBurp(e));
                panel1.add(sendRequestBurp);

                //---- sendRequestServer ----
                sendRequestServer.setText("Repeater/Interrupt\u53d1\u51fa\u8bf7\u6c42\uff08\u670d\u52a1\u5668\uff09");
                sendRequestServer.addActionListener(e -> sendRequestServer(e));
                panel1.add(sendRequestServer);

                //---- sendResponseBurp ----
                sendResponseBurp.setText("\u65e5\u5fd7/Repeater/Interrupt\u6536\u5230\u54cd\u5e94\uff08Burp\uff09");
                sendResponseBurp.addActionListener(e -> sendResponseBurp(e));
                panel1.add(sendResponseBurp);

                //---- sendResponseClient ----
                sendResponseClient.setText("Repeater/Interrupt\u53d1\u51fa\u54cd\u5e94\uff08\u5ba2\u6237\u7aef\uff09");
                sendResponseClient.addActionListener(e -> sendResponseClient(e));
                panel1.add(sendResponseClient);
            }
            dialog1ContentPane.add(panel1, BorderLayout.NORTH);
            dialog1.pack();
            dialog1.setLocationRelativeTo(dialog1.getOwner());
        }

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(enableScript);
        buttonGroup1.add(disableScript);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JTabbedPane tabbedPane1;
    private JPanel watch;
    private JScrollPane scrollPane1;
    private JList<String> watchList;
    private JPanel panel2;
    private JPanel panel3;
    private JCheckBox watchUrlIncludeSelect;
    private JTextField watchUrlInclude;
    private JCheckBox watchReqHeadIncludeSelect;
    private JTextField watchReqHeadInclude;
    private JCheckBox watchReqBodyIncludeSelect;
    private JTextField watchReqBodyInclude;
    private JCheckBox watchRespHeadIncludeSelect;
    private JTextField watchRespHeadInclude;
    private JCheckBox watchRespBodyIncludeSelect;
    private JTextField watchRespBodyInclude;
    private JCheckBox watchUseRegex;
    private JPanel panel5;
    private JCheckBox watchGET;
    private JCheckBox watchPOST;
    private JCheckBox watchOPTIONS;
    private JCheckBox watchHEAD;
    private JCheckBox watchPUT;
    private JCheckBox watchDELETE;
    private JPanel panel4;
    private JTextField watchCustom;
    private JLabel label8;
    private JLabel label7;
    private JTextField watchName;
    private JButton watchSave;
    private JButton watchTest;
    private JButton watchDel;
    private JPanel panel6;
    private JRadioButton enableScript;
    private JRadioButton disableScript;
    private JPanel Setting;
    private JLabel label1;
    private JDialog dialog1;
    private JTabbedPane tab;
    private JPanel panel7;
    private JScrollPane scrollPane4;
    private JEditorPane sendRequestBody;
    private JPanel panel8;
    private JScrollPane scrollPane5;
    private JEditorPane sendResponseBody;
    private JPanel panel9;
    private JScrollPane scrollPane6;
    private JEditorPane sendRequestBurpData;
    private JPanel panel10;
    private JScrollPane scrollPane7;
    private JEditorPane sendRequestServerData;
    private JPanel panel11;
    private JEditorPane sendResponseBurpData;
    private JPanel panel12;
    private JEditorPane sendResponseClientData;
    private JPanel panel1;
    private JButton sendRequestBurp;
    private JButton sendRequestServer;
    private JButton sendResponseBurp;
    private JButton sendResponseClient;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
