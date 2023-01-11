package burp;

import java.awt.*;

public class MessageEditorTabFactory implements IMessageEditorTabFactory,IMessageEditorTab {
    private IMessageEditor messageEditor = null;
    @Override
    public IMessageEditorTab createNewInstance(IMessageEditorController iMessageEditorController, boolean b) {
        messageEditor = BurpExtender.callbacks.createMessageEditor(iMessageEditorController,b);
        return this;
    }

    @Override
    public String getTabCaption() {
        return "CustomCrypto";
    }

    @Override
    public Component getUiComponent() {
         return (new EditorUI(messageEditor.getComponent().getParent())).getSplitPane1();
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
        return true;
    }

    @Override
    public byte[] getSelectedData() {
        return messageEditor.getSelectedData();
    }
}
