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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import se.dolkow.imagefiltering.internationalization.Messages;

public class EditPalettePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected final Palette plt;
	protected final ColorEnabler cen;
	
	protected final Map<JCheckBox,Integer> boxes = new HashMap<JCheckBox, Integer>();
	
	public EditPalettePanel(Palette p, ColorEnabler cen) {
		this.plt = p;
		this.cen = cen;
		
		setLayout(new BorderLayout());
		
		JPanel colors = new JPanel();
		colors.setLayout(new BoxLayout(colors, BoxLayout.Y_AXIS));
		
		CheckBoxListener listener = new CheckBoxListener(this);
		
		for (Color c : plt.getColors()) {
			Box b =  new Box(BoxLayout.X_AXIS);
			b.setMaximumSize(new Dimension(10000,22));
			JCheckBox check = new JCheckBox(c.getName(), cen.isColorEnabled(c.getRGB()));
			Swatch swatch = new Swatch(c);
			
			boxes.put(check, c.getRGB());
			check.addItemListener(listener);
			
			b.add(Box.createHorizontalStrut(5));
			b.add(swatch);
			b.add(Box.createHorizontalStrut(5));
			b.add(check);
			b.add(Box.createHorizontalGlue());
			colors.add(b);
		}
		colors.add(Box.createVerticalGlue());
		
		
		JScrollPane scroll = new JScrollPane(colors, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		add(scroll, BorderLayout.CENTER);
		
		JButton none = new JButton(Messages.get("EditPalettePanel.choose_none")); //$NON-NLS-1$
		none.addActionListener(new SetAllAction(false, this));
		JButton all = new JButton(Messages.get("EditPalettePanel.choose_all")); //$NON-NLS-1$
		all.addActionListener(new SetAllAction(true, this));
		
		Box buttons = new Box(BoxLayout.X_AXIS);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(none);
		buttons.add(Box.createHorizontalStrut(8));
		buttons.add(all);
		buttons.add(Box.createHorizontalStrut(5));
		
		add(buttons, BorderLayout.SOUTH);
	}

	public void setColorEnabled(int rgb, boolean enabled) {
		for (Entry<JCheckBox,Integer> e : boxes.entrySet()) {
			if (e.getValue().equals(rgb)) {
				e.getKey().setSelected(enabled);
			}
		}
	}
	
	private static class SetAllAction implements ActionListener {
		private final EditPalettePanel epp;
		private final boolean setTo;
		
		public SetAllAction(boolean setTo, EditPalettePanel epp) {
			this.setTo = setTo;
			this.epp = epp;
		}
		
		public void actionPerformed(ActionEvent e) {
			for(Color c : epp.plt.colors) {
				epp.cen.setColorEnabled(c.rgb, c.name, setTo, true);
			}
			epp.cen.doneChanging();
		}
	}
	
	private static class CheckBoxListener implements ItemListener {
		private final EditPalettePanel epp;

		public CheckBoxListener(EditPalettePanel epp) {
			this.epp = epp;
		}

		public void itemStateChanged(ItemEvent e) {
			Integer rgb = epp.boxes.get(e.getSource());
			if (rgb != null) {
				for(Color c : epp.plt.getColors()) {
					if (rgb.equals(c.getRGB())) {
						epp.cen.setColorEnabled(c.getRGB(), c.getName(), ((JCheckBox)e.getSource()).isSelected(), false);
					}
				}
			}
		}
	}
	
}
