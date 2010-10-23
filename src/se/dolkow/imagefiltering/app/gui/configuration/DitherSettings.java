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

import se.dolkow.imagefiltering.DitherReduceColorsFilter;
import se.dolkow.imagefiltering.internationalization.Messages;

public class DitherSettings extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final DitherReduceColorsFilter dither;
	
	public DitherSettings(DitherReduceColorsFilter ditherfilter) {
		this.dither = ditherfilter;
		
		final JSlider slider = new JSlider(0, 100, (int)(100*dither.getDitherCoefficient()));
		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				dither.setDitherCoefficient(0.01f * slider.getValue());
			}
		});
		
		Hashtable<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
		labels.put(0, new JLabel(Messages.get("DitherSettings.no_dithering"))); //$NON-NLS-1$
		labels.put(100, new JLabel(Messages.get("DitherSettings.full_dithering"))); //$NON-NLS-1$
		slider.setPaintLabels(true);
		slider.setLabelTable(labels);
		
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(10);
		
		add(slider);
	}
	
}
