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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.dolkow.imagefiltering.ShrinkFilter;
import se.dolkow.imagefiltering.ShrinkResultListener;
import se.dolkow.imagefiltering.internationalization.Messages;

public class ShrinkerSettings extends JPanel {

	private static final long serialVersionUID = 1L;
	private final ShrinkFilter shrinker;
	private final JLabel actual;
	
	public ShrinkerSettings(ShrinkFilter sf) {
		this.shrinker = sf;
		actual = new JLabel(Messages.get("ShrinkerSettings.result_size") + ": ?");  //$NON-NLS-1$//$NON-NLS-2$
		
		shrinker.setResultListener(new ShrinkResultListener() {
			public synchronized void result(int w, int h, ShrinkFilter shrinker) {
				actual.setText(Messages.get("ShrinkerSettings.result_size") + ": " +   //$NON-NLS-1$//$NON-NLS-2$
						w + " x " + h); //$NON-NLS-1$
			}
		});
		
		setLayout(new BorderLayout());
		
		add(createSettings(), BorderLayout.CENTER);
		add(actual, BorderLayout.SOUTH);
		
	}
	
	private JPanel createSettings() {
		JPanel p = new JPanel();
		final JSpinner w = new JSpinner();
		final JSpinner h = new JSpinner();
		
		w.setEditor(new JSpinner.NumberEditor(w, "#")); //$NON-NLS-1$
		h.setEditor(new JSpinner.NumberEditor(h, "#")); //$NON-NLS-1$

		
		w.setValue(shrinker.getMaxWidth());
		h.setValue(shrinker.getMaxHeight());
		
		w.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				shrinker.setMaxWidth((Integer)w.getValue());
			}
		});
		h.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				shrinker.setMaxHeight((Integer)h.getValue());
			}
		});
		
		final JCheckBox smooth = new JCheckBox(Messages.get("ShrinkerSettings.smooth_scaling"), shrinker.getSmooth()); //$NON-NLS-1$
		smooth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shrinker.setSmooth(smooth.isSelected());
			}
		});
		
		p.add(Box.createHorizontalStrut(10));
		p.add(new JLabel(Messages.get("ShrinkerSettings.max_width") + ": "));  //$NON-NLS-1$//$NON-NLS-2$
		p.add(w);
		p.add(Box.createHorizontalStrut(10));
		p.add(new JLabel(Messages.get("ShrinkerSettings.max_height") + ": "));  //$NON-NLS-1$//$NON-NLS-2$
		p.add(h);
		p.add(Box.createHorizontalStrut(10));
		p.add(smooth);
		p.add(Box.createHorizontalStrut(10));
		
		return p;
	}
	
}
