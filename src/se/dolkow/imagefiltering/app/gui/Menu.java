package se.dolkow.imagefiltering.app.gui;

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

import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.app.PerlerApp;
import se.dolkow.imagefiltering.internationalization.Messages;

class Menu extends JMenuBar {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final ImageProducer[] producers;
	final FileChooser fc;
	private File f;
	
	protected boolean changed;
	
	final Saver saver;
	
	Menu(ImageProducer[] p, File loadedFrom, FileChooser fc) {
		this.producers = p;
		this.f = loadedFrom;
		this.saver = new Saver(this);
		
		this.fc = fc;
		
		JMenu file = new JMenu(Messages.get("Menu.file_menu_title")); //$NON-NLS-1$
		file.add(new NewMenuItem());
		file.add(new LoadMenuItem(this));
		file.add(new JSeparator());
		file.add(new SaveMenuItem(this, false));
		file.add(new SaveMenuItem(this, true));
		add(file);
		
		/*JMenu advanced = new JMenu("Advanced");
		advanced.add(new NewCustomChainMenuItem());
		advanced.add(new LoadAndCustomizeChainMenuItem(this));
		add(advanced);*/
		
		JMenu language = new LanguageMenu();
		add(language);
		
		JMenu help = new JMenu(Messages.get("Menu.help_menu_title")); //$NON-NLS-1$
		help.add(new HelpMenuItem());
		if (!PerlerApp.jnlp) {
			help.add(new JSeparator());
			help.add(new VersionCheckMenuItem());
			help.add(new VersionCheckStartupCheckbox());
		}
		help.add(new JSeparator());
		help.add(new AboutMenuItem());
		add(help);
	}
	
	boolean save() {
		return saver.save(true);
	}
	
	synchronized File getFile() {
		return f;
	}
	
	synchronized void setFile(File f) {
		this.f = f;
	}

	public synchronized void setFileChanged(boolean b) {
		changed = b;
	}
	
	public synchronized boolean isFileChanged() {
		return changed;
	}
}
