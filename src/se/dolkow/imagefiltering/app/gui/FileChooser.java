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

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import se.dolkow.imagefiltering.internationalization.Messages;

public class FileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;

	public FileChooser(String[] extension) {
		setFileSelectionMode(JFileChooser.FILES_ONLY);
		setMultiSelectionEnabled(false);
		setFileFilter(new ExtensionFileFilter(extension));
	}
	
	public FileChooser(String extension) {
		this(new String[]{extension});
	}
	
	private static class ExtensionFileFilter extends FileFilter {
		private final String[] ext;
		private final String[] original;

		public ExtensionFileFilter(String[] extension) {
			if (extension.length < 1) {
				throw new IllegalArgumentException("Extension array size < 1"); //$NON-NLS-1$
			}
			this.original = extension;
			this.ext = new String[extension.length];
			for(int i=0; i<ext.length; ++i) {
				this.ext[i] = "." + extension[i].toLowerCase(); //$NON-NLS-1$
			}
		}
		
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			for (String ext : this.ext) {
				if (f.getName().toLowerCase().endsWith(ext)) {
					return true;
				}
			}
			return false;
		}

		public String getDescription() {
			StringBuilder sb = new StringBuilder();
			sb.append(original[0]);
			for(int i=1; i<original.length; ++i) {
				sb.append("/"); //$NON-NLS-1$
				sb.append(original[i]);
			}
			return Messages.getFormatted("FileChooser.file_types", sb.toString()); //$NON-NLS-1$
		}
	}
}
