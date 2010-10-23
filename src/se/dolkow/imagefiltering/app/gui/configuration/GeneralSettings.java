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
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.dolkow.imagefiltering.AbstractImageFilter;
import se.dolkow.imagefiltering.app.gui.FileChooser;
import se.dolkow.imagefiltering.internationalization.Messages;

public class GeneralSettings extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final AbstractImageFilter filter;
	
	public GeneralSettings(AbstractImageFilter f) {
		this.filter = f;
		
		final JCheckBox box = new JCheckBox(Messages.get("GeneralSettings.active_checkbox_name"), filter.isActive()); //$NON-NLS-1$
		add(box);
		box.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				filter.setActive(box.isSelected());
			}
		});
		
		final JButton export = new JButton(Messages.get("GeneralSettings.export_button_text")); //$NON-NLS-1$
		add(export);
		export.addActionListener(new ExportHandler());
	}
	
	private class ExportHandler implements ActionListener {
		
		final JFileChooser jfc;
		
		public ExportHandler() {
			jfc = new FileChooser("png"); //$NON-NLS-1$
			jfc.setApproveButtonText(Messages.get("GeneralSettings.save_button_text")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent aev) {
			int ans = jfc.showSaveDialog(null);
			if (ans == JFileChooser.APPROVE_OPTION) {
				File f = jfc.getSelectedFile();
				if (f != null) {
					if (!f.getName().toLowerCase().endsWith(".png")) { //$NON-NLS-1$
						f = new File(f.getAbsolutePath() + ".png"); //$NON-NLS-1$
					}
					if (f.exists()) {
						String[] opts = new String[]{Messages.get("GeneralSettings.overwrite_option"), Messages.get("GeneralSettings.cancel_option")}; //$NON-NLS-1$ //$NON-NLS-2$
						int overwrite = JOptionPane.showOptionDialog(null, Messages.getFormatted("GeneralSettings.overwrite_question", f.getName()), Messages.get("GeneralSettings.overwrite_question_window_title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]); //$NON-NLS-1$ //$NON-NLS-2$
						if (overwrite != JOptionPane.YES_OPTION) {
							return;
						}
					}
					try {
						BufferedImage img = filter.getImage();
						boolean foundWriter = ImageIO.write(img, "png", f); //$NON-NLS-1$
						if(!foundWriter) {
							throw new Exception(Messages.get("GeneralSettings.no_png_writer")); //$NON-NLS-1$
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), Messages.get("GeneralSettings.export_error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
					}
				}
			}
		}
	}
}
