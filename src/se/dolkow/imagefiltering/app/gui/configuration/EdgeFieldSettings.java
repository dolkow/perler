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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.dolkow.imagefiltering.EdgeFilter;
import se.dolkow.imagefiltering.internationalization.Messages;

public class EdgeFieldSettings extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final EdgeFilter ef;
	
	public EdgeFieldSettings(EdgeFilter f) {
		this.ef = f;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(Box.createVerticalGlue());
		
		JPanel p = new JPanel();
		p.add(new JLabel(Messages.get("EdgeFieldSettings.border_treshold"))); //$NON-NLS-1$
		final JSlider edge = new JSlider(0, 1000, f.getEdgeTreshold());
		edge.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ef.setEdgeTreshold(edge.getValue());
			}
		});
		p.add(edge);
		add(p);
		p = null;
		add(Box.createVerticalGlue());
		
		p = new JPanel();
		final JCheckBox edges = new JCheckBox(Messages.get("EdgeFieldSettings.show_borders"), f.getShowEdges()); //$NON-NLS-1$
		
		edges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ef.setShowEdges(edges.isSelected());
			}
		});
		
		p.add(edges);
		
		add(p);
		p = null;
		add(Box.createVerticalGlue());
	}
}
