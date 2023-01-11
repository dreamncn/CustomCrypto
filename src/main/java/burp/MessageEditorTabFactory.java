package burp;

import java.util.HashMap;

public class MessageEditorTabFactory implements IMessageEditorTabFactory {
    private static HashMap<IMessageEditorController,MessageEditorTab> hashMap = new HashMap<>();
    @Override
    public IMessageEditorTab createNewInstance(IMessageEditorController iMessageEditorController, boolean b) {
       return new MessageEditorTab(iMessageEditorController,  b);
    }
}
