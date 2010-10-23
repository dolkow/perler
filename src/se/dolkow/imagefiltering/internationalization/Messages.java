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
package se.dolkow.imagefiltering.internationalization;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Messages {
	public static final String PREFS_PATH = "se/dolkow/imagefiltering/internationalization";  //$NON-NLS-1$
	
	static final String JAR_BUNDLE_NAME = "se.dolkow.imagefiltering.internationalization.perler-translation"; //$NON-NLS-1$

	private ResourceBundle fromJAR = new EmptyBundle();
	private ResourceBundle external = new EmptyBundle();
	private ResourceBundle fallback = new EmptyBundle();
	
	private static Messages instance;
	
	static {
		instance = new Messages(loadPreferredLanguage());
	}
	
	private static String loadPreferredLanguage() {
		Preferences prefs = Preferences.userRoot().node(PREFS_PATH);
		Locale loc = Locale.getDefault();
		String lang = prefs.get("language", loc.getLanguage()).toLowerCase(); //$NON-NLS-1$
		return lang;
	}
	
	public Messages() {
		this(Locale.getDefault());
	}
	
	public Messages(String lang) {
		this(new Locale(lang, Locale.getDefault().getCountry()));
	}
		
	public Messages(Locale loc) {
		System.out.println("Loading messages for " + loc); //$NON-NLS-1$
		
		fromJAR = new EmptyBundle();
		external = new EmptyBundle();
		try {
			String bundleName = "perler-translation"; //$NON-NLS-1$
			external = ResourceBundle.getBundle(bundleName, loc);
		} catch (MissingResourceException e) {
			//Do nothing
		}
		
		fromJAR = ResourceBundle.getBundle(JAR_BUNDLE_NAME, loc);
		
		if (external instanceof EmptyBundle) {
			System.out.println("Results: no external translation found, internal translation is " + fromJAR.getLocale()); //$NON-NLS-1$
		} else {
			System.out.println("Results: external translation is " + external.getLocale() + ", internal translation is " + fromJAR.getLocale()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		fallback = ResourceBundle.getBundle(JAR_BUNDLE_NAME, new Locale("en")); //$NON-NLS-1$
	}
	
	public String getString(String key) {
		try {
			return external.getString(key);
		} catch (MissingResourceException e1) {
			//fall-through..
		}
		
		try {
			return fromJAR.getString(key);
		} catch (MissingResourceException e2) {
			//fall-through..
		}
		
		try {
			return fallback.getString(key);
		} catch (MissingResourceException e3) {
			//fall-through..
		}
		
		return "!! Missing translation for '" + key + "'!!"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static String get(String key) {
		return instance.getString(key);
	}
	
	public static String format(String text, Object[] args) {
		MessageFormat formatter = new MessageFormat(text, Locale.getDefault());
		return formatter.format(args);
	}
	
	public static String format(String text, Object arg) {
		Object[] args = new Object[]{arg};
		return format(text, args);
	}
	
	public static String getFormatted(String key, Object[] args) {
		String text = get(key);
		return format(text, args);
	}
	
	public static String getFormatted(String key, Object arg) {
		Object[] args = new Object[]{arg};
		return getFormatted(key, args);
	}
	
	static class EmptyBundle extends ResourceBundle {
		public Enumeration<String> getKeys() {
			return new Enumeration<String>() {
				public boolean hasMoreElements() {
					return false;
				}
				public String nextElement() {
					return null;
				}
			};
		}
		protected Object handleGetObject(String key) {
			return null;
		}
	}
}
