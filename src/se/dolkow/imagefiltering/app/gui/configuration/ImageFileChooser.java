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
package se.dolkow.imagefiltering.app.gui.configuration;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import se.dolkow.imagefiltering.app.gui.ImagePreviewAccessory;
import se.dolkow.imagefiltering.internationalization.Messages;


public class ImageFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;

	public ImageFileChooser() {
		this(null);
	}
	
	public ImageFileChooser(String currentpath) {
		if (currentpath != null) {
			File cfile = new File(currentpath);
			if (cfile.exists()) {
				this.setSelectedFile(cfile);
			}
		}
		this.setDialogType(JFileChooser.OPEN_DIALOG);
		this.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.setMultiSelectionEnabled(false);
		this.setFileFilter(new FileFilter() {
			private final String[] accepted = new String[]{".jpeg", ".jpg", ".png", ".gif"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			public String getDescription() {
				return Messages.getFormatted("ImageInputSettings.accepted_types", accepted); //$NON-NLS-1$
			}
			
			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				String n = f.getName().toLowerCase();
				for (String s : accepted) {
					if (n.endsWith(s)) {
						return true;
					}
				}
				return false;
			}
		});
		this.setAccessory(new ImagePreviewAccessory(this));
	}
	
}
