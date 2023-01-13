package burp;

import burp.core.*;
import burp.core.Process;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MessageEditorTab implements IMessageEditorTab {
    private ITextEditor messageEditor = null;
    private boolean isRequest = false;
    private IMessageEditorController iMessageEditorController = null;
    private void selectItem(ActionEvent e) {
        Rule rule = (Rule)selectBox.getSelectedItem();
        assert rule != null;
        try {
            Process process = new Process();
            if(Objects.equals(rule.command, "")){
                process.destroy();
                return;
            }
            BurpExtender.print(String.format("脚本: %s 执行",rule.name));

            if(isRequest){
                HttpAgreement httpAgreement = new HttpAgreement(new String(iMessageEditorController.getRequest()),null,process);
                if(BurpExtender.rules.run(rule.command, CommandType.RequestFromClient,process.getTemp())){
                    setMessage(httpAgreement.toRequest(process).getBytes(),isRequest);
                }
            }else{
                HttpAgreement httpAgreement = new HttpAgreement(new String(iMessageEditorController.getRequest()),new String(iMessageEditorController.getResponse()),process);
                if(BurpExtender.rules.run(rule.command, CommandType.ResponseFromServer,process.getTemp())){
                    setMessage(httpAgreement.toResponse(process).getBytes(),isRequest);
                }
            }
        }catch (IOException exception){
            exception.printStackTrace();
            BurpExtender.print("错误信息2："+exception.getMessage(),1);
        }
        BurpExtender.print("规则："+rule.toString());
    }
    private final JSplitPane rootPanel;
    private final JComboBox<Rule> selectBox;
    MessageEditorTab(IMessageEditorController iMessageEditorController, boolean b){
        this.iMessageEditorController  =iMessageEditorController;
        messageEditor = BurpExtender.callbacks.createTextEditor();
        messageEditor.setEditable(b);
        Component EditorComponent = messageEditor.getComponent();
        rootPanel = new JSplitPane();
        JPanel panel1 = new JPanel();
        JLabel label1 = new JLabel();
        selectBox = new JComboBox<>();

        //======== splitPane1 ========
        rootPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
        //======== panel1 ========
        panel1.setPreferredSize(new Dimension(0, 30));
        panel1.setMaximumSize(new Dimension(2147483647, 30));
        panel1.setLayout(new BorderLayout());
        //---- label1 ----
        label1.setText("\u8bf7\u9009\u62e9\u811a\u672c  ");
        panel1.add(label1, BorderLayout.WEST);

        //---- comboBox1 ----
        selectBox.addActionListener(this::selectItem);
        selectBox.setPreferredSize(new Dimension(0, 30));
        panel1.add(selectBox, BorderLayout.CENTER);

          class Model implements ComboBoxModel<Rule>{
            private final ArrayList<Rule> arrayList;
            private Rule rule = null;
            Model(){
                arrayList = BurpExtender.rules.getAll();
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

        rootPanel.setBottomComponent(EditorComponent);
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
        this.isRequest = isRequest;
        messageEditor.setText(content);
    }

    @Override
    public byte[] getMessage() {
        return messageEditor.getText();
    }

    @Override
    public boolean isModified() {
        return messageEditor.isTextModified();
    }

    @Override
    public byte[] getSelectedData() {
        return messageEditor.getSelectedText();
    }
}
