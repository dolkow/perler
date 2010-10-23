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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.dolkow.imagefiltering.EdgeFilter;
import se.dolkow.imagefiltering.internationalization.Messages;

public class EdgeFillSettings extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final EdgeFilter ef;
	
	public EdgeFillSettings(EdgeFilter f) {
		this.ef = f;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(Box.createVerticalGlue());
		
		JPanel p = new JPanel();
		final JComboBox combo = new JComboBox(EdgeFilter.FillMode.values());
		final JSlider param = new JSlider(0, 255, f.getFillParam());
		combo.setEditable(false);
		
		combo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					EdgeFilter.FillMode mode = (EdgeFilter.FillMode)combo.getSelectedItem(); 
					ef.setFillMode(mode);
					switch(mode) {
					case Gray:
					case White:
					case Random:
						param.setEnabled(true);
						break;
					default:
						param.setEnabled(false);
					}
				}
			}
		});
		combo.setSelectedItem(ef.getFillMode());
		
		p.add(new JLabel(Messages.get("EdgeFillSettings.fill_mode"))); //$NON-NLS-1$
		p.add(combo);
		
		add(p);
		p = null;
		add(Box.createVerticalGlue());
		
		p = new JPanel();
		p.add(new JLabel(Messages.get("EdgeFillSettings.fill_parameter"))); //$NON-NLS-1$
		param.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ef.setFillParam(param.getValue());
			}
		});
		p.add(param);
		
		add(p);
		add(Box.createVerticalGlue());
	}
}
