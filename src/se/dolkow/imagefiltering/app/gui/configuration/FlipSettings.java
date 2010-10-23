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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.dolkow.imagefiltering.Flipper;
import se.dolkow.imagefiltering.internationalization.Messages;

public class FlipSettings extends JPanel {

	private static final long serialVersionUID = 430346749324397775L;
	private final Flipper flipper;
	
	public FlipSettings(Flipper f) {
		this.flipper = f;
		
		final JComboBox combo = new JComboBox(Flipper.FlipMode.values());
		combo.setEditable(false);
		combo.setSelectedItem(flipper.getFlipMode());
		
		combo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				flipper.setFlipMode((Flipper.FlipMode)combo.getSelectedItem());
			}
		});
		
		add(new JLabel(Messages.get("FlipSettingsmode"))); //$NON-NLS-1$
		add(combo);
	}
	
}
