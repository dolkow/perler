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

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("nls")
public class TranslationCheck {

	public static void main(String[] args) {
		String[] translations = new String[]{"es", "sv", "it", "da"};
		
		ResourceBundle base = new Messages.EmptyBundle();
		
		try {
			base = ResourceBundle.getBundle(Messages.JAR_BUNDLE_NAME, new Locale("en", "US"));  //$NON-NLS-1$//$NON-NLS-2$
		} catch (MissingResourceException e) {
			System.out.println("Cannot find base language: " + e.getLocalizedMessage());
			System.exit(1);
		}
		
		Set<String> baseKeys = getKeys(base);
		
		for(String lang : translations) {
			System.out.println("Checking translation " + lang);
			boolean ok = true;
			try {
				ResourceBundle translation = ResourceBundle.getBundle(Messages.JAR_BUNDLE_NAME, new Locale(lang));
				if (!translation.getLocale().getLanguage().equals(lang)) {
					System.out.println("\tERROR: Missing translation for " + lang);
					continue;
				}
				Set<String> langKeys = getKeys(translation);
				
				for (String k : baseKeys) {
					if (langKeys.contains(k)) {
						if (translation.getString(k).equals(base.getString(k))) {
							System.out.println("\tSame value for key " + k + " (" + base.getString(k) + ")");
						}
					} else {
						System.out.println("\tMissing key        " + k);
						ok = false;
					}
				}
				
				for (String k : langKeys) {
					if (!langKeys.contains(k)) {
						System.out.println("\tExtra key          " + k);
						ok = false;
					}
				}
				
				if (ok) {
					System.out.println("\tDone");
				}
				
			} catch (MissingResourceException e) {
				System.out.println("\tCannot open resource for " + lang + ": " + e.getLocalizedMessage());
			}
			
		}
		System.out.println();
		System.out.println("Checked all translations. Bye!");
	}
	
	private static Set<String> getKeys(ResourceBundle rb) {
		Set<String> keys = new TreeSet<String>();
		Enumeration<String> en = rb.getKeys();
		while (en.hasMoreElements()) {
			String k = en.nextElement();
			keys.add(k);
		}
		return keys;
	}

}
