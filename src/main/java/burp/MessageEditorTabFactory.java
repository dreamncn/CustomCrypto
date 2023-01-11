package burp;

public class MessageEditorTabFactory implements IMessageEditorTabFactory {
    @Override
    public IMessageEditorTab createNewInstance(IMessageEditorController iMessageEditorController, boolean b) {
       return new MessageEditorTab(iMessageEditorController,  b);
    }
}
