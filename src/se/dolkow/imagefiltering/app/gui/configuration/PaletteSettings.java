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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import org.xml.sax.SAXException;

import se.dolkow.imagefiltering.AbstractReduceColorsFilter;
import se.dolkow.imagefiltering.TreeParseException;
import se.dolkow.imagefiltering.app.gui.configuration.palette.Color;
import se.dolkow.imagefiltering.app.gui.configuration.palette.Palette;
import se.dolkow.imagefiltering.app.gui.configuration.palette.PaletteDialogPanel;
import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Node;
import se.dolkow.imagefiltering.xml.XMLParser;
import se.dolkow.imagefiltering.xml.XMLParserException;

public class PaletteSettings extends PalettePanel {

	private static final long serialVersionUID = 1L;
	private final List<Palette> palettes;
	private final Palette custom;
	
	public PaletteSettings(AbstractReduceColorsFilter f) throws XMLParserException, SAXException, IOException, URISyntaxException, TreeParseException {
		super(f);
		
		palettes = new LinkedList<Palette>();
		
		Map<Integer,String> colors = f.getColors();
		synchronized (colors) {
			if (colors.size() > 0) {
				Palette p = new Palette();
				for (Entry<Integer,String> e : colors.entrySet()) {
					p.addColor(new Color(e.getKey(), e.getValue()));
				}
				p.setName(Messages.get("PaletteSettings.loaded_palette_name")); //$NON-NLS-1$
				palettes.add(p);
			}
		}
		
		XMLParser xmlp = XMLParser.getInstance();
		for (String s : new String[]{"hama-builtin.xml", "nabbi-builtin.xml", "perlerbeads-builtin.xml", "artkal-serie-s-5mm-hard.xml", "artkal-serie-r-5mm-soft.xml", "artkal-serie-c-2.6mm-hard.xml", "artkal-serie-a-2.6mm-soft.xml"}) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			String path = "/resource/" + s; //$NON-NLS-1$
			URL url = getClass().getResource(path);
			if (url == null) {
				throw new MissingResourceException(Messages.getFormatted("PaletteSettings.missing_resource", path), getClass().getSimpleName(), path); //$NON-NLS-1$
			}
			Node tree = xmlp.parse(url.toURI().toString());
			Palette [] ps = Palette.loadFromTree(tree);
			if (ps.length == 1) {
				ps[0].setName(s);
				palettes.add(ps[0]);
			} else {
				int count = 0;
				for (Palette p : ps) {
					p.setName(s + " " + (++count)); //$NON-NLS-1$
					palettes.add(p);
				}
			}
		}
		
		custom = new Palette();
		custom.setName(Messages.get("PaletteSettings.custom_palette_name")); //$NON-NLS-1$
		palettes.add(custom);
		
		JButton edit = new JButton(Messages.get("PaletteSettings.edit_palette")); //$NON-NLS-1$
		edit.addActionListener(new PaletteEditingActionListener());
		
		JButton custom = new JButton(Messages.get("PaletteSettings.add_custom_color")); //$NON-NLS-1$
		custom.addActionListener(new CustomColorCreatingActionListener());
		
		add(edit);
		add(Box.createHorizontalStrut(5));
		add(custom);
	}
	
	private class PaletteEditingActionListener implements ActionListener {
		public void actionPerformed(ActionEvent aev) {
			Map<Integer, String> palette = filter.getColors();
			Map<Integer, String> oldPalette;
			synchronized(palette) {
				 oldPalette = new HashMap<Integer,String>(filter.getColors());
			}
			try {
				PaletteDialogPanel p = new PaletteDialogPanel(palettes, filter);
				int ans = JOptionPane.showConfirmDialog(null, p, Messages.get("PaletteSettings.palette_settings_window_title"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
				if (ans != JOptionPane.OK_OPTION) {
					synchronized(palette) {
						palette.clear();
						palette.putAll(oldPalette);
					}
					filter.doneChanging();
				}
			} catch (XMLParserException e) {
				JOptionPane.showMessageDialog(null, "Error when creating PaletteDialogPanel" + ": \n" + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}
	
	private class CustomColorCreatingActionListener implements ActionListener {
		private java.awt.Color lastColor = java.awt.Color.BLACK;
		public void actionPerformed(ActionEvent e) {
			java.awt.Color chosen = JColorChooser.showDialog(null, Messages.get("PaletteSettings.choose_custom_color"), lastColor); //$NON-NLS-1$
			if (chosen != null) {
				lastColor = chosen;
				int rgb = lastColor.getRGB();
				String name = JOptionPane.showInputDialog(Messages.get("PaletteSettings.name_custom_color") + ": ");  //$NON-NLS-1$//$NON-NLS-2$
				if (name != null) {
					name = name.trim();
					if (name.length() == 0) {
						name = Integer.toHexString(rgb);
						while (name.length() < 6) {
							name = "0" + name; //$NON-NLS-1$
						}
						name = "#" + name; //$NON-NLS-1$
					}
					custom.addColor(new Color(rgb, name));
					filter.addColor(rgb, false);
				}
			}
		}
	}
}
