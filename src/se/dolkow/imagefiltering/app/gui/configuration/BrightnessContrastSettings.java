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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.dolkow.imagefiltering.BrightnessContrastFilter;

public class BrightnessContrastSettings extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final BrightnessContrastFilter filter;
	private static final int acc = 200; // contrast slider accuracy. Should be a multiple of 4, so that the ticks can be painted properly.
	
	public BrightnessContrastSettings(BrightnessContrastFilter f) {
		this.filter = f;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		final JSlider brightness = new JSlider(-255, 255, f.getBrightness());
		final JSlider contrast = new JSlider(0, 2*acc, (int)(acc*Math.pow(f.getContrast(), 1/7d)));
		
		brightness.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				filter.setBrightness(brightness.getValue());
			}
		});
		
		contrast.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int v = contrast.getValue();
				float c = (float)Math.pow(v / (float)acc, 7);
				filter.setContrast( c );
			}
		});
		
		Hashtable<Integer, JComponent> bLabels = new Hashtable<Integer, JComponent>();
		bLabels.put(0, new JLabel("0")); //$NON-NLS-1$
		bLabels.put(-255, new JLabel("-255")); //$NON-NLS-1$
		bLabels.put(255, new JLabel("+255")); //$NON-NLS-1$
		brightness.setLabelTable(bLabels);
		brightness.setPaintLabels(true);
		brightness.setMajorTickSpacing(16);
		brightness.setPaintTicks(true);
		
		Hashtable<Integer, JComponent> cLabels = new Hashtable<Integer, JComponent>();
		cLabels.put(0, new JLabel("0%")); //$NON-NLS-1$
		cLabels.put(acc, new JLabel("100%")); //$NON-NLS-1$
		cLabels.put(acc*2, new JLabel("∞%")); //$NON-NLS-1$
		contrast.setLabelTable(cLabels);
		contrast.setPaintLabels(true);
		contrast.setPaintTicks(true);
		contrast.setMajorTickSpacing(acc/4);
		contrast.setPaintTicks(true);
		
		add(brightness);
		add(contrast);
		add(Box.createVerticalGlue());
	}
}
