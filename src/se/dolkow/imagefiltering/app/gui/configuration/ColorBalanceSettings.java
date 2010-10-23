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

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.dolkow.imagefiltering.ColorBalance;
import se.dolkow.imagefiltering.internationalization.Messages;

public class ColorBalanceSettings extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ColorBalance filter;
	
	public ColorBalanceSettings(ColorBalance f) {
		this.filter = f;
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		final JSlider rsld = new JSlider(0, 200, (int)(filter.getRedScale()));
		final JSlider gsld = new JSlider(0, 200, (int)(filter.getGreenScale()));
		final JSlider bsld = new JSlider(0, 200, (int)(filter.getBlueScale()));
		
		rsld.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				filter.setRedScale(rsld.getValue());
			}
		});
		
		gsld.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				filter.setGreenScale(gsld.getValue());
			}
		});
		
		bsld.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				filter.setBlueScale(bsld.getValue());
			}
		});
		
		JSlider[] sld = new JSlider[]{rsld, gsld, bsld};
		String[] lo = new String[]{Messages.get("ColorBalanceSettings.cyan"), Messages.get("ColorBalanceSettings.magenta"), Messages.get("ColorBalanceSettings.yellow")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String[] hi = new String[]{Messages.get("ColorBalanceSettings.red"), Messages.get("ColorBalanceSettings.green"), Messages.get("ColorBalanceSettings.blue")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		for (int i=0; i<sld.length; i++) {
			Hashtable<Integer, JComponent> d = new Hashtable<Integer, JComponent>();
			d.put(0, new JLabel(lo[i]));
			d.put(200, new JLabel(hi[i]));
			sld[i].setPaintLabels(true);
			sld[i].setLabelTable(d);
			sld[i].setOrientation(JSlider.HORIZONTAL);
			sld[i].setMajorTickSpacing(50);
			sld[i].setPaintTicks(true);
			add(sld[i]);
		}
	}
}
