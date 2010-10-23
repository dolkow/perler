/*
	Copyright 2010 Snild Dolkow
	
	This file is part of Perler.
	
	Perler is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	Perler is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with Perler.  If not, see <http://www.gnu.org/licenses/>.
*/
package se.dolkow.imagefiltering.app.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.centerkey.utils.BareBonesBrowserLaunch;

import se.dolkow.imagefiltering.internationalization.Messages;

public class VersionCheck {
	private static final String PREFS_PATH = "se/dolkow/imagefiltering/app/gui"; //$NON-NLS-1$
	private static final String AUTO_UPDATE_KEY = "autoupdate"; //$NON-NLS-1$
	
	private static Preferences prefs;
	
	static {
		boolean existed = true;
		try {
			existed = Preferences.userRoot().nodeExists(PREFS_PATH);
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		
		prefs = Preferences.userRoot().node(PREFS_PATH);
		if (!existed) {
			int ans = JOptionPane.showConfirmDialog(null, Messages.get("VersionCheck.autoupdate_query"), Messages.get("VersionCheck.autoupdate_query_title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			setAutoCheckEnabled(ans == JOptionPane.YES_OPTION);
		}
	}
	
	public synchronized static void autoCheck() {
		boolean check = isAutoCheckEnabled();
		if (check) {
			check(false);
		}
	}
	
	private static void fail(Exception e, String msg) {
		String fail = Messages.get("VersionCheck.fail"); //$NON-NLS-1$
		
		String shown = fail + ": "; //$NON-NLS-1$
		if (e != null) {
			shown += e.getClass() + " - "; //$NON-NLS-1$
		}
		shown += msg;
		
		JOptionPane.showMessageDialog(null, shown, fail, JOptionPane.WARNING_MESSAGE);
	}
	
	private static void fail(Exception e) {
		fail(e, e.getLocalizedMessage());
	}
	
	private static void showUpdate(Version local) throws MalformedURLException {
		JTextArea textarea = new JTextArea();
		String changes = Messages.get("VersionCheck.changelog_fail"); //$NON-NLS-1$
		URL changeurl = new URL("http://dolkow.se/perler/changelog.php?since=" + local); //$NON-NLS-1$
		try {
			InputStream is = changeurl.openStream();
			StringBuilder sb = new StringBuilder();
			Scanner sc = new Scanner(is);
			while(sc.hasNextLine()) {
				sb.append(sc.nextLine());
				sb.append("\n"); //$NON-NLS-1$
			}
			changes = sb.toString();
		} catch (IOException ioe) {
			//whatever, the changelog isn't that important.
		}
		textarea.setText(changes);
		textarea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textarea);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//scroll.setMaximumSize(new Dimension(1000, 300));
		textarea.setCaretPosition(0);
		BorderLayout bl = new BorderLayout();
		bl.setVgap(6);
		JPanel p = new JPanel(bl);
		p.setMaximumSize(new Dimension(1000, 400));
		p.setPreferredSize(new Dimension(600, 400));
		p.add(new JLabel(Messages.get("VersionCheck.new_available")), BorderLayout.NORTH); //$NON-NLS-1$
		p.add(scroll, BorderLayout.CENTER);
		p.add(new JLabel(Messages.get("VersionCheck.download_query")), BorderLayout.SOUTH); //$NON-NLS-1$
		int ans = JOptionPane.showConfirmDialog(null, p, Messages.get("VersionCheck.new_available_title"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
		if (ans == JOptionPane.YES_OPTION) {
			BareBonesBrowserLaunch.openURL("http://dolkow.se/perler/"); //$NON-NLS-1$
		}
	}
	
	public synchronized static void check(boolean explicit) {
		try {
			URL latesturl = new URL("http://dolkow.se/perler/latest.php"); //$NON-NLS-1$
			
			Version local = Version.getCurrent();
				
			URLConnection latestconn = latesturl.openConnection();
			latestconn.setRequestProperty("User-Agent", "Perler-" + local);  //$NON-NLS-1$//$NON-NLS-2$
			Version latest = new Version(latestconn.getInputStream());
			
			System.out.println("Current version: " + local + ", newest version: " + latest); //$NON-NLS-1$ //$NON-NLS-2$
			
			if (local.compareTo(latest) < 0) {
				showUpdate(local);
			} else if (explicit) {
				JOptionPane.showMessageDialog(null, Messages.get("VersionCheck.no_new_version"), Messages.get("VersionCheck.no_new_version_title"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (Exception e) {
			fail(e);
		}
	}
	
	public static boolean isAutoCheckEnabled() {
		return prefs.getBoolean(AUTO_UPDATE_KEY, false);
	}
	
	public static void setAutoCheckEnabled(boolean b) {
		prefs.putBoolean(AUTO_UPDATE_KEY, b);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
