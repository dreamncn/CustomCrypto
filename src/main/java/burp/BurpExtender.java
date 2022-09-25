package burp;

import java.awt.Component;
import java.io.PrintWriter;
import javax.swing.SwingUtilities;

public class BurpExtender implements IBurpExtender,ITab {
    public final static String extensionName = "CustomCrypto";
	public final static String version ="0.0.3";
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
		
	}
	


	//
	// 实现ITab
	//
	
	@Override
	public String getTabCaption() {
		return extensionName;
	}

	@Override
	public Component getUiComponent() {
		return gui.getRoot();
	}


}
