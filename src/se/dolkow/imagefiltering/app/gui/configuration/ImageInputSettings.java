package se.dolkow.imagefiltering.app.gui.configuration;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import se.dolkow.imagefiltering.ImageInput;
import se.dolkow.imagefiltering.internationalization.Messages;

public class ImageInputSettings extends JPanel {
	private static final long serialVersionUID = 1L;

	private final ImageInput input;
	
	private final JFileChooser jfc;
	
	private final JLabel current;
	private final JButton browse;
	
	public ImageInputSettings(ImageInput imageinput) {
		this.input = imageinput;
		
		String cpath = input.getCurrentPath();
		jfc = new ImageFileChooser(cpath);
		
		current = new JLabel(Messages.get("ImageInputSettings.current") + ": " + (cpath==null ? "?" : cpath));  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		
		browse = new JButton(Messages.get("ImageInputSettings.browse")); //$NON-NLS-1$
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int ans = jfc.showOpenDialog(ImageInputSettings.this);
				if (ans != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File f = jfc.getSelectedFile();
				if (f != null) {
					try {
						input.setPath(f.getAbsolutePath());
						current.setText(Messages.get("ImageInputSettings.current") + ": " + f.getAbsolutePath());  //$NON-NLS-1$//$NON-NLS-2$
					} catch (Exception e) {
						JOptionPane.showMessageDialog(ImageInputSettings.this, Messages.get("ImageInputSettings.load_failed") + ": " + e.getLocalizedMessage(), Messages.get("ImageInputSettings.load_failed_window_title"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					}
				}
			}
		});
		
		add(current);
		add(browse);
	}
	
}
