/*
 * Created by JFormDesigner on Mon Sep 19 12:24:48 CST 2022
 */

package burp;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import javax.swing.event.*;

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
    private JSONArray saveListData = new JSONArray();

    private int selected = -1;

    /**
     * 初始化数据
     */
    private void initData(){
        emptyScriptList();
        emptyWatch();
        //监控配置
        runListData = new JSONArray();
        saveListData = new JSONArray();
        //加载正在运行的
        JSONArray jsonArray = Storage.read(Storage.RUN_LIST);

        ArrayList<String> arrayList = new ArrayList<>();


        for (int i = 0; i < jsonArray.size() ; i++) {
            JSONObject jsonObject =  jsonArray.getJSONObject(i);
            arrayList.add(jsonObject.getString("title"));
            jsonObject.put("id",i);
            runListData.add(jsonObject);
        }
        watchList.setListData(arrayList.toArray(new String[0]));
        jsonArray = Storage.read(Storage.SAVE_LIST);
        arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size() ; i++) {
            JSONObject jsonObject =  jsonArray.getJSONObject(i);
            arrayList.add(jsonObject.getString("title"));
            jsonObject.put("id",i);
            saveListData.add(jsonObject);
        }
        scriptList.setListData(arrayList.toArray(new String[0]));

        class BoxModel implements ComboBoxModel<String>{
            String item=null;
            public void setSelectedItem(Object anItem){
                item=(String)anItem;
            }
            public Object getSelectedItem(){
                return item;
            }

            @Override
            public int getSize() {
                return saveListData.size();
            }

            @Override
            public String getElementAt(int index) {
                if(index+1 > saveListData.size()) return "";
                return saveListData.getJSONObject(index).getString("title");
            }

            @Override
            public void addListDataListener(ListDataListener l) {

            }

            @Override
            public void removeListDataListener(ListDataListener l) {

            }
        }

        //加载保存的
        ComboBoxModel<String> comboBoxModel = new BoxModel();
        watchScriptList.setModel(comboBoxModel);

        //脚本编辑

    }

    private void emptyScriptList() {
        saveSelect = -1;
        scriptName.setText("");
        scriptProjectMain.setText("");

        scriptCmdCustom.setText("");

        scriptCmdSelect.setSelected(false);
        scriptCmdCustomSelect.setSelected(false);

        scriptCmd.setSelectedIndex(-1);
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
        
        watchScriptSelect.setSelected(data.getBoolean("watchScriptSelect"));
        watchScriptCustomSelect.setSelected(data.getBoolean("watchScriptCustomSelect"));

        if(data.getInteger("id")+1<saveListData.size())
        watchScriptList.setSelectedIndex(data.getInteger("id"));
        
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

        watchScriptSelect.setSelected(false);
        watchScriptCustomSelect.setSelected(false);

        watchScriptList.setSelectedIndex(-1);

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

        if(!watchScriptSelect.isSelected()&&!watchScriptCustomSelect.isSelected()){
            showMsg("请选择脚本执行命令！");
            return null;
        }
        if(watchScriptSelect.isSelected()&&watchScriptList.getSelectedIndex()==-1){
            showMsg("请选择一个脚本执行！");
            return null;
        }
        if(watchScriptCustomSelect.isSelected()&&"".equals(watchCustom.getText())){
            showMsg("自定义脚本需要填写具体的执行命令！");
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
        jsonObject.put("watchScriptSelect",watchScriptSelect.isSelected());
        jsonObject.put("watchScriptCustomSelect",watchScriptCustomSelect.isSelected());
       
        String command = "";
        if(watchScriptCustomSelect.isSelected()){
            command = watchCustom.getText();
        }else{
          JSONObject select =  saveListData.getJSONObject(watchScriptList.getSelectedIndex());
          command = select.getString("command");
        }
        
        jsonObject.put("command",command);

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
            stringBuilder.append(request.getString("method")).append(" ").append(request.getString("path")).append(" ").append(request.getString("http_version")).append("\n");
            
        }else{
             request = jsonObject.getJSONObject("response");
            stringBuilder.append(request.getString("http_version")).append(" ").append(request.getString("state")).append(" ").append(request.getString("state_msg")).append("\n");
        }
        JSONObject headers =  request.getJSONObject("headers");
        for (String key : headers.keySet()) {
            stringBuilder.append(key).append(": ").append(headers.getString(key)).append("\n");
        }
        stringBuilder.append("\n");
        stringBuilder.append(request.getString("body"));
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

    private int saveSelect = -1;
    private void scriptListValueChanged(ListSelectionEvent e) {
        if (scriptList.getSelectedIndex() == -1) return;
        int index = scriptList.getSelectedIndex();
        if(index>saveListData.size())return;
        saveSelect = index;
        JSONObject data =  saveListData.getJSONObject(index);
        scriptName.setText(data.getString("title"));
        scriptProjectMain.setText(data.getString("scriptProjectMain"));
        scriptCmdCustom.setText(data.getString("scriptCmdCustom"));
        scriptCmdSelect.setSelected(data.getBoolean("scriptCmdSelect"));
        scriptCmdCustomSelect.setSelected(data.getBoolean("scriptCmdCustomSelect"));
        scriptCmd.setSelectedIndex(data.getInteger("scriptCmd"));
    }

    private void scriptDel(ActionEvent e) {
        if(saveSelect!=-1){
            //新增
            saveListData.remove(saveSelect);
        }
        Storage.write(Storage.SAVE_LIST,saveListData);
        initData();
    }

    private void scriptSave(ActionEvent e) {
        JSONObject jsonObject = new JSONObject();
        String title = scriptName.getText();
        if("".equals(title)){
            showMsg("配置名称不允许为空！");
            return;
        }
        if(!scriptCmdSelect.isSelected()&&!scriptCmdCustomSelect.isSelected()){
            showMsg("请选择执行命令！");
            return ;
        }
        if(scriptCmdSelect.isSelected()&&scriptCmd.getSelectedIndex()==-1){
            showMsg("请选择一个执行命令！");
            return ;
        }
        if(scriptCmdCustomSelect.isSelected()&&"".equals(scriptCmdCustom.getText())){
            showMsg("自定义执行命令需要填写具体的命令行路径！");
            return ;
        }

        jsonObject.put("title",scriptName.getText());

        jsonObject.put("scriptProjectMain",scriptProjectMain.getText());

        jsonObject.put("scriptCmdCustom",scriptCmdCustom.getText());
        jsonObject.put("scriptCmdSelect",scriptCmdSelect.isSelected());
        jsonObject.put("scriptCmdCustomSelect",scriptCmdCustomSelect.isSelected());
        jsonObject.put("scriptCmd",scriptCmd.getSelectedIndex());


        String command = "";
        if(scriptCmdSelect.isSelected()){
            String[] strings = new String[]{"python","java","node" ,"php"};
            command = strings[scriptCmd.getSelectedIndex()];
        }else{
            command = scriptCmdCustom.getText();
        }

        jsonObject.put("command",command+" "+scriptProjectMain.getText());
        if(saveSelect==-1){
            //新增
            saveListData.add(jsonObject);
        }else{
            //更新
            saveListData.set(saveSelect,jsonObject);
        }

        Storage.write(Storage.SAVE_LIST,saveListData);
        initData();

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
        panel4 = new JPanel();
        watchScriptSelect = new JRadioButton();
        watchScriptList = new JComboBox<>();
        watchScriptCustomSelect = new JRadioButton();
        watchCustom = new JTextField();
        watchSave = new JButton();
        watchTest = new JButton();
        watchDel = new JButton();
        label7 = new JLabel();
        watchName = new JTextField();
        main = new JPanel();
        panel5 = new JPanel();
        panel6 = new JPanel();
        scriptCmd = new JComboBox<>();
        scriptCmdSelect = new JRadioButton();
        scriptCmdCustomSelect = new JRadioButton();
        scriptCmdCustom = new JTextField();
        label3 = new JLabel();
        scriptName = new JTextField();
        scriptSave = new JButton();
        scriptDel = new JButton();
        e = new JLabel();
        scriptProjectMain = new JTextField();
        label1 = new JLabel();
        scrollPane2 = new JScrollPane();
        scriptList = new JList<>();
        Setting = new JPanel();
        scrollPane3 = new JScrollPane();
        label6 = new JLabel();
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
                watch.setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder ( new javax
                . swing. border .EmptyBorder ( 0, 0 ,0 , 0) ,  "JF\u006frmDes\u0069gner \u0045valua\u0074ion" , javax. swing
                .border . TitledBorder. CENTER ,javax . swing. border .TitledBorder . BOTTOM, new java. awt .
                Font ( "D\u0069alog", java .awt . Font. BOLD ,12 ) ,java . awt. Color .red
                ) ,watch. getBorder () ) ); watch. addPropertyChangeListener( new java. beans .PropertyChangeListener ( ){ @Override
                public void propertyChange (java . beans. PropertyChangeEvent e) { if( "\u0062order" .equals ( e. getPropertyName (
                ) ) )throw new RuntimeException( ) ;} } );
                watch.setLayout(new BorderLayout());

                //======== scrollPane1 ========
                {
                    scrollPane1.setMaximumSize(new Dimension(200, 32767));

                    //---- watchList ----
                    watchList.setModel(new AbstractListModel<String>() {
                        String[] values = {
                            "\u76d1\u63a7Gzip\u81ea\u52a8\u89e3\u538b",
                            "\u89e3\u5bc6\u67d0\u5b89\u5353App",
                            "\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32"
                        };
                        @Override
                        public int getSize() { return values.length; }
                        @Override
                        public String getElementAt(int i) { return values[i]; }
                    });
                    watchList.setMaximumSize(new Dimension(200, 62));
                    watchList.setFixedCellWidth(200);
                    watchList.addListSelectionListener(e -> watchListValueChanged(e));
                    scrollPane1.setViewportView(watchList);
                }
                watch.add(scrollPane1, BorderLayout.WEST);

                //======== panel2 ========
                {

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
                                        .addComponent(watchUseRegex, GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE))
                                    .addContainerGap())
                        );
                        panel3Layout.setVerticalGroup(
                            panel3Layout.createParallelGroup()
                                .addGroup(panel3Layout.createSequentialGroup()
                                    .addContainerGap()
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
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        );
                    }

                    //======== panel4 ========
                    {
                        panel4.setBorder(new TitledBorder("\u811a\u672c\u914d\u7f6e"));

                        //---- watchScriptList ----
                        watchScriptList.setModel(new DefaultComboBoxModel<>(new String[] {
                            "xxx\u89e3\u5bc6\u811a\u672c"
                        }));

                        //---- watchScriptCustomSelect ----
                        watchScriptCustomSelect.setText("\u81ea\u5b9a\u4e49\u547d\u4ee4\u6267\u884c");

                        GroupLayout panel4Layout = new GroupLayout(panel4);
                        panel4.setLayout(panel4Layout);
                        panel4Layout.setHorizontalGroup(
                            panel4Layout.createParallelGroup()
                                .addGroup(panel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel4Layout.createParallelGroup()
                                        .addGroup(panel4Layout.createSequentialGroup()
                                            .addComponent(watchScriptSelect)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(watchScriptList, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
                                        .addGroup(panel4Layout.createSequentialGroup()
                                            .addComponent(watchScriptCustomSelect)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(watchCustom, GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)))
                                    .addContainerGap())
                        );
                        panel4Layout.setVerticalGroup(
                            panel4Layout.createParallelGroup()
                                .addGroup(panel4Layout.createSequentialGroup()
                                    .addGroup(panel4Layout.createParallelGroup()
                                        .addGroup(panel4Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(watchScriptSelect))
                                        .addComponent(watchScriptList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(watchScriptCustomSelect)
                                        .addComponent(watchCustom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(0, 12, Short.MAX_VALUE))
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

                    //---- label7 ----
                    label7.setText("\u914d\u7f6e\u540d\u79f0\uff1a");

                    GroupLayout panel2Layout = new GroupLayout(panel2);
                    panel2.setLayout(panel2Layout);
                    panel2Layout.setHorizontalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(panel2Layout.createParallelGroup()
                                    .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(panel2Layout.createSequentialGroup()
                                        .addComponent(label7)
                                        .addGap(6, 6, 6)
                                        .addComponent(watchName, GroupLayout.PREFERRED_SIZE, 381, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(27, Short.MAX_VALUE))
                                    .addGroup(panel2Layout.createSequentialGroup()
                                        .addComponent(watchSave)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(watchTest)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(watchDel)
                                        .addGap(0, 221, Short.MAX_VALUE))
                                    .addComponent(panel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    );
                    panel2Layout.setVerticalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(panel2Layout.createParallelGroup()
                                    .addGroup(panel2Layout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(label7))
                                    .addComponent(watchName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panel3, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(watchSave)
                                    .addComponent(watchTest)
                                    .addComponent(watchDel))
                                .addContainerGap(10, Short.MAX_VALUE))
                    );
                }
                watch.add(panel2, BorderLayout.CENTER);
            }
            tabbedPane1.addTab("\u76d1\u63a7\u914d\u7f6e", watch);

            //======== main ========
            {
                main.setLayout(new BorderLayout());

                //======== panel5 ========
                {

                    //======== panel6 ========
                    {
                        panel6.setBorder(new TitledBorder("\u6267\u884c\u547d\u4ee4"));

                        //---- scriptCmd ----
                        scriptCmd.setModel(new DefaultComboBoxModel<>(new String[] {
                            "python",
                            "java",
                            "node",
                            "php"
                        }));

                        //---- scriptCmdCustomSelect ----
                        scriptCmdCustomSelect.setText("\u81ea\u5b9a\u4e49\u547d\u4ee4");

                        GroupLayout panel6Layout = new GroupLayout(panel6);
                        panel6.setLayout(panel6Layout);
                        panel6Layout.setHorizontalGroup(
                            panel6Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, panel6Layout.createSequentialGroup()
                                    .addContainerGap(13, Short.MAX_VALUE)
                                    .addGroup(panel6Layout.createParallelGroup()
                                        .addGroup(panel6Layout.createSequentialGroup()
                                            .addComponent(scriptCmdSelect, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(scriptCmd, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panel6Layout.createSequentialGroup()
                                            .addComponent(scriptCmdCustomSelect)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(scriptCmdCustom, GroupLayout.PREFERRED_SIZE, 336, GroupLayout.PREFERRED_SIZE)))
                                    .addContainerGap())
                        );
                        panel6Layout.setVerticalGroup(
                            panel6Layout.createParallelGroup()
                                .addGroup(panel6Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel6Layout.createParallelGroup()
                                        .addComponent(scriptCmd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(scriptCmdSelect, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(panel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(scriptCmdCustomSelect)
                                        .addComponent(scriptCmdCustom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        );
                    }

                    //---- label3 ----
                    label3.setText("\u811a\u672c\u540d\u79f0\uff1a");

                    //---- scriptSave ----
                    scriptSave.setText("\u4fdd\u5b58");
                    scriptSave.addActionListener(e -> scriptSave(e));

                    //---- scriptDel ----
                    scriptDel.setText("\u5220\u9664");
                    scriptDel.addActionListener(e -> scriptDel(e));

                    //---- e ----
                    e.setText("\u811a\u672c\u6587\u4ef6\uff1a");

                    //---- label1 ----
                    label1.setText("<html>\u4e2a\u4eba\u5efa\u8bae\uff1a \u5c06\u6240\u6709\u811a\u672c\u653e\u5230\u4e13\u6709\u76ee\u5f55\u4e0b\u4ee5\u5907\u4e0d\u65f6\u4e4b\u9700\u3002");

                    GroupLayout panel5Layout = new GroupLayout(panel5);
                    panel5.setLayout(panel5Layout);
                    panel5Layout.setHorizontalGroup(
                        panel5Layout.createParallelGroup()
                            .addGroup(panel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel5Layout.createParallelGroup()
                                    .addGroup(panel5Layout.createSequentialGroup()
                                        .addComponent(panel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(21, 39, Short.MAX_VALUE))
                                    .addGroup(panel5Layout.createSequentialGroup()
                                        .addGroup(panel5Layout.createParallelGroup()
                                            .addGroup(panel5Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addGroup(panel5Layout.createParallelGroup()
                                                    .addGroup(panel5Layout.createSequentialGroup()
                                                        .addComponent(scriptSave)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(scriptDel))
                                                    .addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                        .addGroup(GroupLayout.Alignment.LEADING, panel5Layout.createSequentialGroup()
                                                            .addComponent(e)
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(scriptProjectMain))
                                                        .addGroup(panel5Layout.createSequentialGroup()
                                                            .addComponent(label3)
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(scriptName, GroupLayout.PREFERRED_SIZE, 381, GroupLayout.PREFERRED_SIZE)))))
                                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 42, Short.MAX_VALUE))))
                    );
                    panel5Layout.setVerticalGroup(
                        panel5Layout.createParallelGroup()
                            .addGroup(panel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(scriptName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label3))
                                .addGap(18, 18, 18)
                                .addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(e)
                                    .addComponent(scriptProjectMain, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(scriptSave)
                                    .addComponent(scriptDel))
                                .addGap(18, 18, 18)
                                .addComponent(label1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(193, Short.MAX_VALUE))
                    );
                }
                main.add(panel5, BorderLayout.CENTER);

                //======== scrollPane2 ========
                {
                    scrollPane2.setMaximumSize(new Dimension(200, 32767));

                    //---- scriptList ----
                    scriptList.setModel(new AbstractListModel<String>() {
                        String[] values = {
                            "\u76d1\u63a7Gzip\u81ea\u52a8\u89e3\u538b",
                            "\u89e3\u5bc6\u67d0\u5b89\u5353App",
                            "\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32\u8d85\u7ea7\u65e0\u654c\u957f\u7684\u5b57\u7b26\u4e32"
                        };
                        @Override
                        public int getSize() { return values.length; }
                        @Override
                        public String getElementAt(int i) { return values[i]; }
                    });
                    scriptList.setMaximumSize(new Dimension(200, 62));
                    scriptList.setFixedCellWidth(200);
                    scriptList.addListSelectionListener(e -> scriptListValueChanged(e));
                    scrollPane2.setViewportView(scriptList);
                }
                main.add(scrollPane2, BorderLayout.LINE_START);
            }
            tabbedPane1.addTab("\u811a\u672c\u7f16\u8f91", main);

            //======== Setting ========
            {
                Setting.setLayout(new BorderLayout());

                //======== scrollPane3 ========
                {

                    //---- label6 ----
                    label6.setText("<html><h1>\u76d1\u63a7\u914d\u7f6e</h1>\n&nbsp;&nbsp; \u8be5\u90e8\u5206\u4e3b\u8981\u5224\u65ad\u54ea\u4e9b\u60c5\u51b5\u4e0b\u7684\u8bf7\u6c42\u5305/\u54cd\u5e94\u5305\u9700\u8981\u811a\u672c\u4ecb\u5165\u5904\u7406\u3002\n<h2>\u76d1\u63a7\u53c2\u6570</h2>\n&nbsp; &nbsp; \u6b64\u5904\u9700\u8981\u544a\u8bc9burp\uff0c\u9700\u8981\u6839\u636e\u4ec0\u4e48\u89c4\u5219\u8fdb\u884c\u76d1\u63a7\u3002\u52fe\u9009\u3010\u6240\u6709\u5339\u914d\u89c4\u5219\u91c7\u7528\u6b63\u5219\u6a21\u5f0f\u3011\u5219\u4f1a\u5c06\u76d1\u63a7\u53c2\u6570\u4ee5\u6b63\u5219\u7684\u5f62\u5f0f\u8fdb\u884c\u5339\u914d\u3002\n<h2>\u811a\u672c\u914d\u7f6e</h2>\n&nbsp;&nbsp; \u6b64\u5904\u53ef\u9009\u3010\u5185\u7f6e\u811a\u672c\u3011\u548c\u3010\u81ea\u5b9a\u4e49\u811a\u672c\u3011\u3002\u5185\u7f6e\u811a\u672c\u5c31\u662f\u5728\u3010\u811a\u672c\u7f16\u8f91\u3011\u4e2d\u914d\u7f6e\u597d\u7684\u811a\u672c\u3002\u3010\u81ea\u5b9a\u4e49\u811a\u672c\u3011\u5c31\u662f\u6ca1\u6709\u5728\u3010\u811a\u672c\u7f16\u8f91\u3011\u4e2d\u914d\u7f6e\u597d\u811a\u672c\u3002\n&nbsp;&nbsp; \u3010\u81ea\u5b9a\u4e49\u811a\u672c\u3011\u4e00\u822c\u4e3a\u3010python xxx.py\u3011\u7b49\u5f62\u5f0f\u3002\n<h2>\u6d4b\u8bd5</h2>\n&nbsp;&nbsp; \u6267\u884c\u6a21\u62df\u6d4b\u8bd5\uff0c\u6a21\u62dfburp\u771f\u5b9e\u6293\u5305\u573a\u666f\u6d4b\u8bd5\u3002\n<h1>\u811a\u672c\u7f16\u8f91</h1>\n&nbsp;&nbsp; \u8fd9\u4e00\u90e8\u5206\u4e3b\u8981\u5b58\u50a8\u4e00\u4e9b\u53ef\u4ee5\u8fdb\u884c\u590d\u7528\u7684\u811a\u672c\uff0c\u4f8b\u5982Gzip\u81ea\u52a8\u89e3\u538b\u811a\u672c\u7b49\u3002\n<h2>\u6267\u884c\u547d\u4ee4</h2>\n&nbsp;&nbsp; \u7b2c\u4e00\u4e2a\u9009\u9879\u662f\u9ed8\u8ba4\u4f7f\u7528\u90a3\u4e2a\u547d\u4ee4\u6765\u6267\u884c\u811a\u672c\uff0c\u5982\u679c\u4f60\u9700\u8981\u6267\u884c\u811a\u672c\u7684\u547d\u4ee4\u4e0d\u5728\u9009\u9879\u5361\u4e2d\uff0c\u4f60\u53ef\u4ee5\u4f7f\u7528\u7b2c\u4e8c\u4e2a\u9009\u9879\u6307\u5b9a\u6267\u884c\u7684\u547d\u4ee4\u3002\n<h2>\u811a\u672c\u540d\u79f0</h2>\n&nbsp;&nbsp; \u5c31\u662f\u8fd9\u4e2a\u811a\u672c\u4fdd\u5b58\u7684\u540d\u5b57\n<h2>\u811a\u672c\u9879\u76ee\u6587\u4ef6\u5939</h2>\n&nbsp;&nbsp; \u5c31\u662f\u811a\u672c\u6240\u5728\u7684\u6587\u4ef6\u5939\uff0c\u901a\u5e38\u5efa\u8bae\u4e3a\u6bcf\u4e2a\u811a\u672c\u914d\u7f6e\u4e00\u4e2a\u4e13\u95e8\u7684\u6587\u4ef6\u5939\u3002\n<h2>\u811a\u672c\u9879\u76ee\u4e3b\u6587\u4ef6</h2>\n&nbsp;&nbsp; \u5c31\u662f\u8fd0\u884c\u7684\u811a\u672c\u6587\u4ef6<br/>\n");
                    label6.setBorder(new EmptyBorder(10, 10, 10, 10));
                    scrollPane3.setViewportView(label6);
                }
                Setting.add(scrollPane3, BorderLayout.CENTER);
            }
            tabbedPane1.addTab("\u5e2e\u52a9", Setting);
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
                    panel7.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border. EmptyBorder( 0
                    , 0, 0, 0) , "JF\u006frmDes\u0069gner \u0045valua\u0074ion", javax. swing. border. TitledBorder. CENTER, javax. swing. border. TitledBorder. BOTTOM
                    , new java .awt .Font ("D\u0069alog" ,java .awt .Font .BOLD ,12 ), java. awt. Color. red) ,
                    panel7. getBorder( )) ); panel7. addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e
                    ) {if ("\u0062order" .equals (e .getPropertyName () )) throw new RuntimeException( ); }} );
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
        buttonGroup1.add(watchScriptSelect);
        buttonGroup1.add(watchScriptCustomSelect);

        //---- buttonGroup2 ----
        ButtonGroup buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(scriptCmdSelect);
        buttonGroup2.add(scriptCmdCustomSelect);
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
    private JPanel panel4;
    private JRadioButton watchScriptSelect;
    private JComboBox<String> watchScriptList;
    private JRadioButton watchScriptCustomSelect;
    private JTextField watchCustom;
    private JButton watchSave;
    private JButton watchTest;
    private JButton watchDel;
    private JLabel label7;
    private JTextField watchName;
    private JPanel main;
    private JPanel panel5;
    private JPanel panel6;
    private JComboBox<String> scriptCmd;
    private JRadioButton scriptCmdSelect;
    private JRadioButton scriptCmdCustomSelect;
    private JTextField scriptCmdCustom;
    private JLabel label3;
    private JTextField scriptName;
    private JButton scriptSave;
    private JButton scriptDel;
    private JLabel e;
    private JTextField scriptProjectMain;
    private JLabel label1;
    private JScrollPane scrollPane2;
    private JList<String> scriptList;
    private JPanel Setting;
    private JScrollPane scrollPane3;
    private JLabel label6;
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
