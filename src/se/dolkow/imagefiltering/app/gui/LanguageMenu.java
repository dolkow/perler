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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import se.dolkow.imagefiltering.internationalization.Messages;

public class LanguageMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	private static final String[] LANGUAGE_CODES = new String[] {
		"da", "de", "en", "es", "fr", "it", "sv"};  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
	private static final String[] LANGUAGE_NAMES = new String[] {
		"Dansk", "Deutsch", "English", "Español", "Français", "Italiano", "Svenska"};  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
	
	public LanguageMenu() {
		super(Messages.get("LanguageMenu.title")); //$NON-NLS-1$
		
		int n = Math.min(LANGUAGE_CODES.length, LANGUAGE_NAMES.length);
		for (int i=0; i<n; i++) {
			add(new LanguageMenuItem(LANGUAGE_CODES[i], LANGUAGE_NAMES[i]));
		}
		
		add(new JSeparator());
		
		add(new AutoLanguageMenuItem());
	}
	
	private static class LanguageMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;

		public LanguageMenuItem(String langcode, String langname) {
			super(langname);
			addActionListener(new LanguageItemAction(langcode));
		}
	}
	
	private static class LanguageItemAction implements ActionListener {
		private final String languageCode;
		
		public LanguageItemAction(String langcode) {
			languageCode = langcode;
		}

		public void actionPerformed(ActionEvent e) {
			Preferences prefs = Preferences.userRoot().node(Messages.PREFS_PATH);
			prefs.put("language", languageCode); //$NON-NLS-1$
			changed(prefs, new Messages(languageCode));
		}
	}
	
	private static class AutoLanguageMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;

		public AutoLanguageMenuItem() {
			super(Messages.get("LanguageMenu.auto_choose")); //$NON-NLS-1$
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Preferences prefs = Preferences.userRoot().node(Messages.PREFS_PATH);
					prefs.remove("language"); //$NON-NLS-1$
					changed(prefs, null);
				}
			});
		}
	}
	
	private static void changed(Preferences prefs, Messages m) {
		String msg, title;
		if (m != null) {
			msg = m.getString("LanguageMenu.language_set"); //$NON-NLS-1$
			title = m.getString("LanguageMenu.language_set_title"); //$NON-NLS-1$
		} else {
			msg = Messages.get("LanguageMenu.language_set"); //$NON-NLS-1$
			title = Messages.get("LanguageMenu.language_set_title"); //$NON-NLS-1$
		}
		try {
			prefs.flush();
			JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
		} catch (BackingStoreException e1) {
			JOptionPane.showMessageDialog(null, "BackingStoreException:" + e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
