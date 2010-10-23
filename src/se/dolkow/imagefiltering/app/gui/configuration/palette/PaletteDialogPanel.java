package se.dolkow.imagefiltering.app.gui.configuration.palette;

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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import se.dolkow.imagefiltering.AbstractReduceColorsFilter;
import se.dolkow.imagefiltering.app.gui.FileChooser;
import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Node;
import se.dolkow.imagefiltering.xml.XMLParser;
import se.dolkow.imagefiltering.xml.XMLParserException;

public class PaletteDialogPanel extends JPanel implements ColorEnabler {

	private static final long serialVersionUID = 1L;
	private final List<EditPalettePanel> editpanels;
	private final JPanel editContainer;
	private final CardLayout editLayout;
	protected final AbstractReduceColorsFilter filter;
	private final JComboBox combo;
	private final List<Palette> palettes;
	
	public PaletteDialogPanel(List<Palette> palettes, AbstractReduceColorsFilter filter) throws XMLParserException {
		if (palettes.size() < 1) {
			throw new IllegalArgumentException("0-size palette list"); //$NON-NLS-1$
		}
		setLayout(new BorderLayout());
		
		this.palettes = palettes;
		
		combo = new JComboBox();
		combo.setEditable(false);
		combo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				editLayout.show(editContainer, combo.getSelectedItem().toString());
			}
		});
		
		this.filter = filter;
		editpanels = new LinkedList<EditPalettePanel>();
		
		editLayout = new CardLayout();
		this.editContainer = new JPanel(editLayout);
		
		for (Palette p : palettes) {
			addPalette(p);
		}
		
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout());
		JPanel northwest = new JPanel();
		northwest.setLayout(new BorderLayout());
		northwest.add(new JLabel(Messages.get("PaletteDialogPanel.source") + ": "), BorderLayout.WEST);  //$NON-NLS-1$//$NON-NLS-2$
		northwest.add(Box.createHorizontalStrut(2), BorderLayout.CENTER);
		northwest.add(combo, BorderLayout.EAST);
		north.add(northwest, BorderLayout.WEST);
		north.add(Box.createHorizontalStrut(20), BorderLayout.CENTER);
		
		JButton imprt = new JButton(Messages.get("PaletteDialogPanel.import") + "...");  //$NON-NLS-1$//$NON-NLS-2$
		imprt.addActionListener(new ImportAction());
		north.add(imprt, BorderLayout.EAST);
		
		add(north, BorderLayout.NORTH);
		add(editContainer, BorderLayout.CENTER);
		
		setMinimumSize(new Dimension(300,400));
		setPreferredSize(new Dimension(1000,500));
		setPreferredSize(new Dimension(500,500));
	}

	public void setColorEnabled(int rgb, String name, boolean enabled, boolean more) {
		for (EditPalettePanel epp : editpanels) {
			epp.setColorEnabled(rgb, enabled);
		}
		if (enabled) {
			filter.addColor(rgb, name, more);
		} else {
			filter.removeColor(rgb, more);
		}
	}

	public void doneChanging() {
		filter.doneChanging();
	}
	
	private class ImportAction implements ActionListener {
		private final FileChooser fc;
		private final XMLParser xmlp;
		
		public ImportAction() throws XMLParserException {
			fc = new FileChooser(new String[]{"plr", "xml"}); //$NON-NLS-1$ //$NON-NLS-2$
			xmlp = XMLParser.getInstance();
		}
		
		public void actionPerformed(ActionEvent ae) {
			int ans = fc.showOpenDialog(null);
			if (ans == FileChooser.APPROVE_OPTION) {
				File f = fc.getSelectedFile();
				if (f != null && f.exists()) {
					try {
						Node tree = xmlp.parse(f);
						Palette[] ps = Palette.loadFromTree(tree);
						if (ps.length == 1) {
							ps[0].setName(f.getName());
						} else {
							int i = 1;
							for(Palette p : ps) {
								p.setName(f.getName() + " " + i++); //$NON-NLS-1$
							}
						}
						for (Palette p : ps) {
							palettes.add(p);
							addPalette(p);
						}
						editContainer.validate();
						combo.setSelectedIndex(combo.getItemCount()-1);
						editLayout.show(editContainer, ps[ps.length-1].toString());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, Messages.get("PaletteDialogPanel.import_error") + ": \n" + e.getLocalizedMessage(), Messages.get("General.error"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					}
				}
			}
		}
	}

	private void addPalette(Palette p) {
		EditPalettePanel epp = new EditPalettePanel(p, this);
		editpanels.add(epp);
		editContainer.add(epp, p.toString());
		combo.addItem(p.toString());
	}

	public boolean isColorEnabled(int rgb) {
		return filter.hasColor(rgb);
	}

}
