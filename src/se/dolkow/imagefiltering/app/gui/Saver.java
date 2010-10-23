/*
	Copyright 2009 Snild Dolkow
	
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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;

/**
 * @author snild
 *
 */
public class Saver {
	
	protected final Menu menu;
	
	public Saver(Menu m) {
		this.menu = m;
	}
	
	/**
	 * 
	 * @param ignoreExists save without asking, even if the file exists.
	 * @return true if the file was successfully saved
	 */
	final boolean save(boolean ignoreExists) {
		try {
			File f = menu.getFile();
			if (f == null) {
				throw new IOException("file is NULL"); //$NON-NLS-1$
			}
			if (!f.getName().toLowerCase().endsWith(".plr")) { //$NON-NLS-1$
				f = new File(f.getAbsolutePath()+".plr"); //$NON-NLS-1$
			}
			if (f.exists() && !ignoreExists) {
				String[] opts = new String[]{Messages.get("Saver.cancel_option"), Messages.get("Saver.overwrite_option")}; //$NON-NLS-1$ //$NON-NLS-2$
				int overwrite = JOptionPane.showOptionDialog(null, Messages.getFormatted("Saver.overwrite_question", f.getName()), Messages.get("Saver.overwrite_window_title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[1]); //$NON-NLS-1$ //$NON-NLS-2$
				if (overwrite != JOptionPane.YES_OPTION) {
					return false;
				}
			}
			PrintWriter pw = new PrintWriter(f, "UTF-8"); //$NON-NLS-1$
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$
			Element chain = new Element("chain"); //$NON-NLS-1$
			for (ImageProducer ip : menu.producers) {
				ip.saveToTree(chain);
			}
			chain.writeXML(pw, ""); //$NON-NLS-1$
			pw.flush();
			pw.close();
			menu.setFileChanged(false);
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, Messages.get("Saver.save_failed") + ": \n" + e.getLocalizedMessage(), Messages.get("Saver.save_failed_window_title"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			return false;
		}
	}
	
}
