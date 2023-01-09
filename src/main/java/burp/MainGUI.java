/*
 * Created by JFormDesigner on Mon Sep 19 12:24:48 CST 2022
 */

package burp;

import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;
import burp.core.Rule;
import burp.core.Rules;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;


/**
 * 插件的GUI实现
 * @author ankio
 */
public class MainGUI extends JPanel {
    private final Rules rules;
    /**
     * 构造函数
     */ MainGUI() {
        //初始化UI
        initComponents();
        //初始化数据
        rules = new Rules();
        initData();

    }



    /**
     * 初始化数据
     */
    private void initData(){
        rules.readRule();
        autoRun.setSelected(rules.getAuto());
        ArrayList<String> arrayList = new ArrayList<>();
        for (Rule rule:rules.getAll()) {
            arrayList.add(rule.name);
        }
        BurpExtender.print("数据："+arrayList);
        watchList.setListData(arrayList.toArray(new String[0]));
        initTable();
    }

   

    /**
     * 获取根View
     */
    public JTabbedPane getRoot(){
        return tabbedPane1;
    }

    private void watchListValueChanged(ListSelectionEvent e) {
        select = watchList.getSelectedIndex();
        BurpExtender.print("选择："+select);
        if (select == -1) return;

        Rule rule = rules.getRule(select);
        if(rule==null)return;
        initTable();
        watchName.setText(rule.name);
        watchCustom.setText(rule.command);
        for (String s:rule.method) {
            switch (s){
                case "patch":watchPATCH.setSelected(true);break;
                case "delete":watchDELETE.setSelected(true);break;
                case "get":watchGET.setSelected(true);break;
                case "post":watchPOST.setSelected(true);break;
                case "put":watchPUT.setSelected(true);break;
            }
        }
        watchReqBodyInclude.setText(rule.body);
        watchReqHeadInclude.setText(rule.header);
        watchUrlInclude.setText(rule.url);
        watchUseRegex.setSelected(rule.regex);
    }

    private void autoRunStateChanged(ChangeEvent e) {
        rules.setAuto(autoRun.isSelected());
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        tabbedPane1 = new JTabbedPane();
        panel1 = new JPanel();
        panel6 = new JPanel();
        autoRun = new JCheckBox();
        splitPane1 = new JSplitPane();
        watchList = new JList<>();
        panel2 = new JPanel();
        panel3 = new JPanel();
        watchUrlInclude = new JTextField();
        watchReqHeadInclude = new JTextField();
        watchReqBodyInclude = new JTextField();
        watchUseRegex = new JCheckBox();
        panel5 = new JPanel();
        watchGET = new JCheckBox();
        watchPOST = new JCheckBox();
        watchPATCH = new JCheckBox();
        watchPUT = new JCheckBox();
        watchDELETE = new JCheckBox();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        panel4 = new JPanel();
        watchCustom = new JTextField();
        label8 = new JLabel();
        label7 = new JLabel();
        watchName = new JTextField();
        watchSave = new JButton();
        watchDel = new JButton();
        Setting = new JPanel();
        label1 = new JLabel();

        //======== tabbedPane1 ========
        {

            //======== panel1 ========
            {
                panel1.setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder ( new javax . swing.
                border .EmptyBorder ( 0, 0 ,0 , 0) ,  "JF\u006frm\u0044es\u0069gn\u0065r \u0045va\u006cua\u0074io\u006e" , javax. swing .border . TitledBorder. CENTER
                ,javax . swing. border .TitledBorder . BOTTOM, new java. awt .Font ( "D\u0069al\u006fg", java .awt . Font
                . BOLD ,12 ) ,java . awt. Color .red ) ,panel1. getBorder () ) ); panel1. addPropertyChangeListener(
                new java. beans .PropertyChangeListener ( ){ @Override public void propertyChange (java . beans. PropertyChangeEvent e) { if( "\u0062or\u0064er"
                .equals ( e. getPropertyName () ) )throw new RuntimeException( ) ;} } );
                panel1.setLayout(new BorderLayout());

                //======== panel6 ========
                {
                    panel6.setLayout(new FlowLayout());

                    //---- autoRun ----
                    autoRun.setText("\u81ea\u52a8\u6267\u884c\u811a\u672c");
                    autoRun.addChangeListener(e -> autoRunStateChanged(e));
                    panel6.add(autoRun);
                }
                panel1.add(panel6, BorderLayout.NORTH);

                //======== splitPane1 ========
                {
                    splitPane1.setDividerLocation(200);

                    //---- watchList ----
                    watchList.setMaximumSize(new Dimension(200, 62));
                    watchList.setFixedCellWidth(200);
                    watchList.setBorder(LineBorder.createBlackLineBorder());
                    watchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    watchList.addListSelectionListener(e -> watchListValueChanged(e));
                    splitPane1.setLeftComponent(watchList);

                    //======== panel2 ========
                    {
                        panel2.setBorder(new EmptyBorder(20, 20, 20, 20));

                        //======== panel3 ========
                        {
                            panel3.setBorder(new TitledBorder("\u76d1\u63a7\u53c2\u6570\uff08\u81ea\u52a8\u6267\u884c\u811a\u672c\u9700\u8981\u914d\u7f6e\uff09"));

                            //---- watchUseRegex ----
                            watchUseRegex.setText("\u6240\u6709\u5339\u914d\u89c4\u5219\u91c7\u7528\u6b63\u5219\u6a21\u5f0f");

                            //======== panel5 ========
                            {
                                panel5.setBorder(new TitledBorder("\u8bf7\u6c42\u7c7b\u578b"));

                                //---- watchGET ----
                                watchGET.setText("GET");

                                //---- watchPOST ----
                                watchPOST.setText("POST");

                                //---- watchPATCH ----
                                watchPATCH.setText("PATCH");

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
                                            .addComponent(watchPATCH)
                                            .addGap(18, 18, 18)
                                            .addComponent(watchPUT)
                                            .addGap(18, 18, 18)
                                            .addComponent(watchDELETE)
                                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                );
                                panel5Layout.setVerticalGroup(
                                    panel5Layout.createParallelGroup()
                                        .addGroup(panel5Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(watchGET)
                                                .addComponent(watchPOST)
                                                .addComponent(watchPATCH)
                                                .addComponent(watchPUT)
                                                .addComponent(watchDELETE))
                                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                );
                            }

                            //---- label2 ----
                            label2.setText("URL\u5305\u542b");

                            //---- label3 ----
                            label3.setText("\u8bf7\u6c42\u5934\u5305\u542b");

                            //---- label4 ----
                            label4.setText("\u8bf7\u6c42\u4f53\u5305\u542b");

                            GroupLayout panel3Layout = new GroupLayout(panel3);
                            panel3.setLayout(panel3Layout);
                            panel3Layout.setHorizontalGroup(
                                panel3Layout.createParallelGroup()
                                    .addGroup(panel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(panel3Layout.createParallelGroup()
                                            .addComponent(panel5, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                                            .addComponent(watchUseRegex, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(panel3Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(label2)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(watchUrlInclude))
                                            .addGroup(panel3Layout.createSequentialGroup()
                                                .addGroup(panel3Layout.createParallelGroup()
                                                    .addGroup(panel3Layout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addComponent(label3, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(11, 11, 11))
                                                    .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                                                        .addComponent(label4, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)))
                                                .addGroup(panel3Layout.createParallelGroup()
                                                    .addComponent(watchReqHeadInclude)
                                                    .addComponent(watchReqBodyInclude))))
                                        .addContainerGap())
                            );
                            panel3Layout.setVerticalGroup(
                                panel3Layout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(panel5, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 96, Short.MAX_VALUE)
                                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(label2)
                                            .addComponent(watchUrlInclude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(watchReqHeadInclude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label3))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(watchReqBodyInclude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label4))
                                        .addGap(18, 18, 18)
                                        .addComponent(watchUseRegex)
                                        .addGap(57, 57, 57))
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
                                                .addComponent(watchCustom, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                                            .addGroup(panel4Layout.createSequentialGroup()
                                                .addComponent(label7)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(watchName, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)))
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
                                        .addGap(0, 3, Short.MAX_VALUE))
                            );
                        }

                        //---- watchSave ----
                        watchSave.setText("\u4fdd\u5b58");
                        watchSave.addActionListener(e -> watchSave(e));

                        //---- watchDel ----
                        watchDel.setText("\u5220\u9664");
                        watchDel.addActionListener(e -> watchDel(e));

                        GroupLayout panel2Layout = new GroupLayout(panel2);
                        panel2.setLayout(panel2Layout);
                        panel2Layout.setHorizontalGroup(
                            panel2Layout.createParallelGroup()
                                .addGroup(panel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel2Layout.createParallelGroup()
                                        .addComponent(panel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panel3, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                        .addGroup(panel2Layout.createSequentialGroup()
                                            .addComponent(watchSave)
                                            .addGap(18, 18, 18)
                                            .addComponent(watchDel)
                                            .addGap(0, 101, Short.MAX_VALUE)))
                                    .addContainerGap())
                        );
                        panel2Layout.setVerticalGroup(
                            panel2Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                    .addComponent(panel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(watchSave)
                                        .addComponent(watchDel))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        );
                    }
                    splitPane1.setRightComponent(panel2);
                }
                panel1.add(splitPane1, BorderLayout.CENTER);
            }
            tabbedPane1.addTab("\u811a\u672c\u914d\u7f6e", panel1);

            //======== Setting ========
            {
                Setting.setBorder(new EmptyBorder(20, 20, 20, 20));
                Setting.setLayout(new BorderLayout());

                //---- label1 ----
                label1.setText("<Html><h1>CustomCrypto</h1><h2>burp\u81ea\u5b9a\u4e49\u52a0\u89e3\u5bc6\u63d2\u4ef6</h2><h3>Github: https://github.com/dreamncn/CustomCrypto</h3><h3>Site: https://ankio.net</h3><h3>Powered by Ankio 2023.</h3>");
                Setting.add(label1, BorderLayout.NORTH);
            }
            tabbedPane1.addTab("\u5173\u4e8e", Setting);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initTable(){

        watchPATCH.setSelected(false);
        watchDELETE.setSelected(false);
        watchGET.setSelected(false);
        watchPOST.setSelected(false);
        watchPUT.setSelected(false);
        watchName.setText("");
        watchCustom.setText("");
        watchReqBodyInclude.setText("");
        watchReqHeadInclude.setText("");
        watchUrlInclude.setText("");
        watchUseRegex.setSelected(false);
    }
    private int select = -1;

    /**
     * 显示错误信息
     */
    private void showMsg(String msg){
        JOptionPane.showMessageDialog(null, msg, "错误",
                JOptionPane.ERROR_MESSAGE);
    }
    private void watchSave(ActionEvent e) {
        Rule rule = new Rule();
        rule.command = watchCustom.getText();
        rule.name = watchName.getText();
        if(rule.name==null || rule.name.isEmpty()){
            showMsg("必须填写脚本名称");
            return;
        }
        rule.body = watchReqBodyInclude.getText();
        rule.header = watchReqHeadInclude.getText();
        rule.url = watchUrlInclude.getText();
        rule.regex = watchUseRegex.isSelected();
        ArrayList<String> strings = new ArrayList<>();
        if(watchPATCH.isSelected())strings.add("patch");
        if(watchDELETE.isSelected())strings.add("delete");
        if(watchGET.isSelected())strings.add("get");
        if(watchPOST.isSelected())strings.add("post");
        if(watchPUT.isSelected())strings.add("put");
        rule.method = strings;
        if(select!=-1){
            rules.update(select,rule);
        }else{
            rules.add(rule);
        }
        select = -1;
        initData();

    }

    private void watchDel(ActionEvent e) {
        if(select!=-1){
            rules.del(select);
            select = -1;
            initData();
        }
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JPanel panel6;
    private JCheckBox autoRun;
    private JSplitPane splitPane1;
    private JList<String> watchList;
    private JPanel panel2;
    private JPanel panel3;
    private JTextField watchUrlInclude;
    private JTextField watchReqHeadInclude;
    private JTextField watchReqBodyInclude;
    private JCheckBox watchUseRegex;
    private JPanel panel5;
    private JCheckBox watchGET;
    private JCheckBox watchPOST;
    private JCheckBox watchPATCH;
    private JCheckBox watchPUT;
    private JCheckBox watchDELETE;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JPanel panel4;
    private JTextField watchCustom;
    private JLabel label8;
    private JLabel label7;
    private JTextField watchName;
    private JButton watchSave;
    private JButton watchDel;
    private JPanel Setting;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
