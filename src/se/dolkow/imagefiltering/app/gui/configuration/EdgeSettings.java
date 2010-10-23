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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.dolkow.imagefiltering.EdgeFilter;

public class EdgeSettings extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final EdgeFilter filter;
	
	public EdgeSettings(EdgeFilter f) {
		this.filter = f;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		
		
		final JSlider low = new JSlider(0, EdgeFilter.getMaxTreshold(), f.getLowTreshold());
		final JSlider high = new JSlider(0, EdgeFilter.getMaxTreshold(), f.getHighTreshold());
		
		low.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				filter.setLowTreshold(low.getValue());
			}
		});
		
		high.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				filter.setHighTreshold(high.getValue());
			}
		});
		
		add(low);
		add(high);
		
		add(Box.createVerticalGlue());
	}
}
