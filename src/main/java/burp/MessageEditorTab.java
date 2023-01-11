package burp;

import burp.core.Rule;
import burp.core.Rules;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class MessageEditorTab implements IMessageEditorTab {
    private IMessageEditor messageEditor = null;
    private void selectItem(ActionEvent e) {
        Rule rule = (Rule)selectBox.getSelectedItem();
        assert rule != null;
        BurpExtender.print("规则："+rule.toString());
    }
    private final JSplitPane rootPanel;
    private final JComboBox<Rule> selectBox;
    MessageEditorTab(IMessageEditorController iMessageEditorController, boolean b){
        messageEditor = BurpExtender.callbacks.createMessageEditor(iMessageEditorController,false);
        rootPanel = new JSplitPane();
        JPanel panel1 = new JPanel();
        JLabel label1 = new JLabel();
        selectBox = new JComboBox<>();
        JLabel label2 = new JLabel();

        //======== splitPane1 ========
        rootPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
        //======== panel1 ========
        panel1.setMinimumSize(new Dimension(149, 60));
        panel1.setPreferredSize(new Dimension(75, 30));
        panel1.setMaximumSize(new Dimension(2147483647, 60));
        panel1.setLayout(new BorderLayout());
        //---- label1 ----
        label1.setText("\u8bf7\u9009\u62e9\u811a\u672c  ");
        label1.setPreferredSize(new Dimension(100, 30));
        panel1.add(label1, BorderLayout.WEST);

        //---- comboBox1 ----
        selectBox.setPreferredSize(new Dimension(0, 15));
        selectBox.addActionListener(this::selectItem);
        panel1.add(selectBox, BorderLayout.CENTER);

        //---- label2 ----
        label2.setText("\u53d1\u9001\u6570\u636e\u5305\u8bf7\u4e0d\u8981\u4f7f\u7528\u89e3\u5bc6\u540e\u7684\u7ed3\u679c\u3002");
        panel1.add(label2, BorderLayout.NORTH);


        class Model implements ComboBoxModel<Rule>{
            private final ArrayList<Rule> arrayList;
            private Rule rule = null;
            Model(){
                arrayList = (new Rules()).getAll();
            }
            @Override
            public void setSelectedItem(Object anItem) {
                rule = (Rule) anItem;
            }
            @Override
            public Object getSelectedItem() {
                return rule;
            }
            @Override
            public int getSize() {
                return arrayList.size();
            }

            @Override
            public Rule getElementAt(int index) {
                return arrayList.get(index);
            }

            @Override
            public void addListDataListener(ListDataListener l) {

            }

            @Override
            public void removeListDataListener(ListDataListener l) {

            }
        }
        selectBox.setModel(new Model());

        rootPanel.setTopComponent(panel1);
        rootPanel.setBottomComponent(messageEditor.getComponent());
    }
    @Override
    public String getTabCaption() {
        return "CustomCrypto";
    }

    @Override
    public Component getUiComponent() {
         return rootPanel;
    }

    @Override
    public boolean isEnabled(byte[] bytes, boolean b) {
        return true;
    }

    @Override
    public void setMessage(byte[] content, boolean isRequest) {
        messageEditor.setMessage(content,isRequest);
    }

    @Override
    public byte[] getMessage() {
        return messageEditor.getMessage();
    }

    @Override
    public boolean isModified() {
        return messageEditor.isMessageModified();
    }

    @Override
    public byte[] getSelectedData() {
        return messageEditor.getSelectedData();
    }
}
