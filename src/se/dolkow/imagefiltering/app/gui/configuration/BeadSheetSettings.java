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
package se.dolkow.imagefiltering.app.gui.configuration;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import se.dolkow.imagefiltering.AbstractReduceColorsFilter;
import se.dolkow.imagefiltering.ImageException;
import se.dolkow.imagefiltering.app.gui.DirectoryChooser;
import se.dolkow.imagefiltering.internationalization.Messages;

/**
 * @author snild
 *
 */
public class BeadSheetSettings extends JPanel {
	private static final long serialVersionUID = 1L;
	protected final AbstractReduceColorsFilter filter;
	protected final Settings settings;
	
	public BeadSheetSettings(AbstractReduceColorsFilter f) {
		this.filter = f;
		settings = new Settings();
		
		JButton export = new JButton(Messages.get("BeadSheetSettings.export_button_text")); //$NON-NLS-1$
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = JOptionPane.showConfirmDialog(null, settings, Messages.get("BeadSheetSettings.export_dialog_title"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
				if (ans == JOptionPane.OK_OPTION) {
					try {
						export();
					} catch (Throwable e1) {
						JOptionPane.showMessageDialog(null, e1.getClass().getSimpleName() + ": " + e1.getLocalizedMessage(), Messages.get("BeadSheetSettings.export_error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		});
		
		add(export);
	}
	
	protected void export() throws ImageException, IOException {
		String[] options = new String[]{Messages.get("BeadSheetSettings.replace"), Messages.get("BeadSheetSettings.replace_all"), Messages.get("BeadSheetSettings.cancel_export")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String path = settings.getPath();
		
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		String basename = settings.getBaseName();
		boolean split = settings.getSplit();
		int tileWidth = settings.getTileWidth();
		int tileHeight = settings.getTileHeight();
		if (!split) {
			tileWidth = Integer.MAX_VALUE;
			tileHeight= Integer.MAX_VALUE;
		}
		BufferedImage[][] sheets = filter.getBeadSheets(tileWidth, tileHeight);
		
		boolean replaceAll = false;
		for (int i=0; i<sheets.length; i++) {
			for (int j=0; j<sheets[i].length; j++) {
				File out = new File(path + basename + "-" + i + "-" + j + ".png"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				boolean write = true;
				if (out.exists() && !replaceAll) {
					String arg = out.getName();
					String replace = Messages.getFormatted("BeadSheetSettings.replace_query", arg); //$NON-NLS-1$
					int ans = JOptionPane.showOptionDialog(null, replace, Messages.get("BeadSheetSettings.replace_query_title"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]); //$NON-NLS-1$
					//YES_OPTION changes nothing.
					if (ans == JOptionPane.CANCEL_OPTION) {
						write = false;
					} else if (ans == JOptionPane.NO_OPTION) {
						replaceAll = true;
					}
				}
				
				if (write) {
					boolean foundWriter = ImageIO.write(sheets[i][j], "png", out); //$NON-NLS-1$
					if(!foundWriter) {
						throw new ImageException(Messages.get("BeadSheetSettings.no_png_writer")); //$NON-NLS-1$
					}
				}
			}
		}
	}
	
	protected class Settings extends JPanel {
		private static final long serialVersionUID = 1L;
		private final JSpinner tileWidth;
		private final JSpinner tileHeight;
		private final JCheckBox split;
		private final JLabel path;
		private final JTextField basename;
		private final DirectoryChooser dc;
		
		public String getPath() {
			return dc.getSelectedFile().getAbsolutePath();
		}
		
		public String getBaseName() {
			return basename.getText();
		}
		
		public boolean getSplit() {
			return split.isSelected();
		}
		
		public int getTileHeight() {
			return (Integer)tileHeight.getValue();
		}
		
		public int getTileWidth() {
			return (Integer)tileWidth.getValue();
		}
		
		public Settings() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			dc = new DirectoryChooser();
			dc.setSelectedFile(new File("")); //$NON-NLS-1$
			path = new JLabel(dc.getSelectedFile().getAbsolutePath());
			path.setHorizontalTextPosition(SwingConstants.LEFT);
			JPanel pathPanel = new JPanel(new BorderLayout());
			
			JButton browse = new JButton(Messages.get("BeadSheetSettings.browse_button")); //$NON-NLS-1$
			browse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dc.showOpenDialog(null);
					path.setText(dc.getSelectedFile().getAbsolutePath());
				}
			});
			
			pathPanel.add(new JLabel(Messages.get("BeadSheetSettings.target_dir")), BorderLayout.WEST);  //$NON-NLS-1$
			pathPanel.add(path, BorderLayout.CENTER);
			pathPanel.add(browse, BorderLayout.EAST);
			
			JPanel basenamePanel = new JPanel(new BorderLayout());
			Box basenameBox = Box.createHorizontalBox();
			basename = new JTextField(Messages.get("BeadSheetSettings.default_basename")); //$NON-NLS-1$
			basename.setPreferredSize(new Dimension(150, 24));
			basenameBox.add(new JLabel(Messages.get("BeadSheetSettings.base_name"))); //$NON-NLS-1$
			basenameBox.add(basename);
			basenamePanel.add(basenameBox, BorderLayout.WEST);
			
			Box splitbox = new Box(BoxLayout.X_AXIS);
			split = new JCheckBox(Messages.get("BeadSheetSettings.split_into_tiles_description")); //$NON-NLS-1$
			split.setSelected(true);
			
			tileHeight = new JSpinner();
			tileHeight.setEditor(new JSpinner.NumberEditor(tileHeight, "#")); //$NON-NLS-1$
			tileHeight.setValue(29);
			tileHeight.setMaximumSize(new Dimension(50,25));
			
			tileWidth = new JSpinner();
			tileWidth.setEditor(new JSpinner.NumberEditor(tileWidth, "#")); //$NON-NLS-1$
			tileWidth.setValue(29);
			tileWidth.setMaximumSize(new Dimension(50,25));
			
			splitbox.add(split);
			splitbox.add(new JLabel(Messages.get("BeadSheetSettings.with_width"))); //$NON-NLS-1$
			splitbox.add(tileWidth);
			splitbox.add(new JLabel(Messages.get("BeadSheetSettings.and_height"))); //$NON-NLS-1$
			splitbox.add(tileHeight);
			splitbox.add(Box.createHorizontalGlue());
			
			add(pathPanel);
			add(basenamePanel);
			add(splitbox);
			add(Box.createVerticalGlue());
			add(Box.createHorizontalStrut(800));
			
		}
	}
}
