package burp;

import javax.swing.*;

/**
 * 测试GUI,需要debug=true
 */
public class Main {
    public static boolean debug = true;
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // 确保一个漂亮的外观风格
                JFrame.setDefaultLookAndFeelDecorated(false);
                // 创建及设置窗口
                JFrame frame = new JFrame("测试GUI");
                MainGUI main =  new MainGUI();
                frame.getContentPane().add(main.getRoot());
                // 显示窗口
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
