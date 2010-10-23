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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import se.dolkow.imagefiltering.internationalization.Messages;

/**
 * @author snild
 *
 */

public class SaveMenuItem extends JMenuItem {
	
	private static final long serialVersionUID = 1L;
	protected final Menu menu;
	protected final boolean saveAs;
	
	public SaveMenuItem(Menu menu, boolean saveAs) {
		super(saveAs ? Messages.get("SaveMenuItem.save_chain_as") : Messages.get("SaveMenuItem.save_chain"), KeyEvent.VK_S); //$NON-NLS-1$ //$NON-NLS-2$
		this.menu = menu;
		this.saveAs = saveAs;
		if(!saveAs) {
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		}
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				clicked();
			}
		});
	}
	
	protected void clicked() {
		File f = menu.getFile();
		if (!saveAs && f != null) {
			menu.saver.save(true);
		} else {
			JFileChooser jfc = menu.fc;
			jfc.setApproveButtonText(Messages.get("SaveMenuItem.save_button_text")); //$NON-NLS-1$
			int ans = jfc.showSaveDialog(null);
			if (ans == JFileChooser.APPROVE_OPTION) {
				f = jfc.getSelectedFile();
				menu.setFile(f);
				menu.saver.save(false);
			}
		}
	}
}
