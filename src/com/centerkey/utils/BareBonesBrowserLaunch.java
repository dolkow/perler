package com.centerkey.utils;

import java.lang.reflect.Method;
import javax.swing.JOptionPane;

import se.dolkow.imagefiltering.internationalization.Messages;

/**
 * <b>Bare Bones Browser Launch for Java</b><br>
 * Utility class to open a web page from a Swing application in the user's
 * default browser.<br>
 * Supports: Mac OS X, GNU/Linux, Unix, Windows XP/Vista<br>
 * Example Usage:<code><br> &nbsp; &nbsp;
 *    String url = "http://www.google.com/";<br> &nbsp; &nbsp;
 *    BareBonesBrowserLaunch.openURL(url);<br></code> Latest Version: <a
 * href="http://www.centerkey.com/java/browser/"
 * >www.centerkey.com/java/browser</a><br>
 * Author: Dem Pilafian<br>
 *    minor modifications by Snild Dolkow<br>
 * Public Domain Software -- Free to Use as You Like
 * 
 * @version 2.0-snild, August 3, 2009 (derived from version 2.0, May 26 2009)
 */
public class BareBonesBrowserLaunch {

	static final String[] browsers = { "iceweasel", "opera", "firefox", "konqueror", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"epiphany", "seamonkey", "galeon", "kazehakase", "mozilla", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			"netscape" }; //$NON-NLS-1$

	/**
	 * Opens the specified web page in a web browser
	 * 
	 * @param url
	 *            A web address (URL) of a web page (ex:
	 *            "http://www.google.com/")
	 */
	public static void openURL(String url) {
		String osName = System.getProperty("os.name"); //$NON-NLS-1$
		try {
			if (osName.startsWith("Mac OS")) { //$NON-NLS-1$
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager"); //$NON-NLS-1$
				Method openURL = fileMgr.getDeclaredMethod("openURL", //$NON-NLS-1$
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows")) //$NON-NLS-1$
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url); //$NON-NLS-1$
			else { // assume Unix or Linux
				boolean found = false;
				for (String browser : browsers)
					if (!found) {
						found = Runtime.getRuntime().exec(
								new String[] { "which", browser }).waitFor() == 0; //$NON-NLS-1$
						if (found)
							Runtime.getRuntime().exec(
									new String[] { browser, url });
					}
				if (!found) {
					String msg = Messages.get("BareBonesBrowserLaunch.nobrowser"); //$NON-NLS-1$
					for (String s : browsers ) {
						msg = msg + " \n" + s; //$NON-NLS-1$
					}
					throw new Exception(msg);
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					Messages.get("BareBonesBrowserLaunch.launcherror") + "\n" + e.toString()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

}