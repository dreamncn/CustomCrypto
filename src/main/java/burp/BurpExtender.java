package burp;

import java.awt.Component;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingUtilities;

public class BurpExtender implements IBurpExtender,ITab {
    public final static String extensionName = "CustomCrypto";
	public final static String version ="1.0.0";
	public static IBurpExtenderCallbacks callbacks;
	public static IExtensionHelpers helpers;
	public static PrintWriter stdout;
	public static PrintWriter stderr;
	public static MainGUI gui;

	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		BurpExtender.callbacks = callbacks;
		helpers = callbacks.getHelpers();
		stdout = new PrintWriter(callbacks.getStdout(),true);
		stderr = new PrintWriter(callbacks.getStderr(),true);
		callbacks.setExtensionName(extensionName+" "+version);
		try {
			callbacks.registerHttpListener(new BurpHttpListener());
			callbacks.registerProxyListener(new BurpHttpListener());
			gui = new MainGUI();
			SwingUtilities.invokeLater(() -> {
				BurpExtender.callbacks.addSuiteTab(BurpExtender.this);
				stdout.println(
						"[+] " + BurpExtender.extensionName + " is loaded\n"
								+ "[+] ^_^\n"
								+ "[+]\n"
								+ "[+] #####################################\n"
								+ "[+]    " + BurpExtender.extensionName + " v" + BurpExtender.version +"\n"
								+ "[+]    author: ankio\n"
								+ "[+]    email:  admin@ankio.net\n"
								+ "[+]    github: https://github.com/dreamncn\n"
								+ "[+] ####################################"
				);
			});

		} catch (IOException e) {
			print("初始化异常："+e.getMessage(),1);
			e.printStackTrace();
		}

	}
	public static void print(String msg){
		print(msg,0);
	}
	public static void print(String msg,int type){
		SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
		Date date = new Date();// 获取当前时间
		msg = String.format("[ %s ] [%s] %s\n", sdf.format(date),type==0?"*":"-",msg);
		stdout.print(msg);
		System.out.println(msg);
	}
	
	@Override
	public String getTabCaption() {
		return extensionName;
	}

	@Override
	public Component getUiComponent() {
		return gui.getRoot();
	}


}
