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

import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.dolkow.imagefiltering.RotateFilter;
import se.dolkow.imagefiltering.internationalization.Messages;

public class RotateSettings extends JPanel {

	private static final long serialVersionUID = 1L;
	private final RotateFilter rotate;
	
	public RotateSettings(RotateFilter f) {
		this.rotate = f;
		
		final JSlider control = new JSlider(0, 360, rotate.getRotation());
		
		Hashtable<Integer,JComponent> labels = new Hashtable<Integer, JComponent>();
		labels.put(0, new JLabel(Messages.get("RotateSettings.no_rotation"))); //$NON-NLS-1$
		labels.put(90, new JLabel(Messages.get("RotateSettings.quarter"))); //$NON-NLS-1$
		labels.put(180, new JLabel(Messages.get("RotateSettings.half_circle"))); //$NON-NLS-1$
		labels.put(270, new JLabel(Messages.get("RotateSettings.three_quarters"))); //$NON-NLS-1$
		labels.put(360, new JLabel(Messages.get("RotateSettings.full_circle"))); //$NON-NLS-1$
		control.setLabelTable(labels);
		control.setPaintLabels(true);
		control.setPaintTicks(true);
		control.setMajorTickSpacing(90);
		control.setMinorTickSpacing(15);
		
		control.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				rotate.setRotation(control.getValue());
			}
		});
		
		add(control);
	}
	
}
