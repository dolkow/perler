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

import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.dolkow.imagefiltering.CroppingFilter;
import se.dolkow.imagefiltering.internationalization.Messages;

public class CropSettings extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final CroppingFilter crop;
	private final InteractiveImageDisplay disp;
	
	public CropSettings(CroppingFilter cf, InteractiveImageDisplay d) {
		this.crop = cf;
		this.disp = d;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JTextArea jta= new JTextArea(Messages.get("CropSettings.crop_instructions")); //$NON-NLS-1$
		jta.setEditable(false);
		jta.setOpaque(false);
		jta.setWrapStyleWord(true);
		jta.setLineWrap(true);
		jta.setMargin(new Insets(4, 6, 4, 6));
		add(jta);
		
		
		JPanel ratioP = new JPanel();
		
		final JSpinner w = new JSpinner();
		final JSpinner h = new JSpinner();
		
		w.setEditor(new JSpinner.NumberEditor(w, "#")); //$NON-NLS-1$
		h.setEditor(new JSpinner.NumberEditor(h, "#")); //$NON-NLS-1$

		
		w.setValue(crop.getAspectWidth());
		h.setValue(crop.getAspectHeight());
		
		w.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				crop.setAspectWidth((Integer)w.getValue());
				disp.setAspect2((Integer)w.getValue());
			}
		});
		h.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				crop.setAspectHeight((Integer)h.getValue());
				disp.setAspect1((Integer)h.getValue());
			}
		});
		
		ratioP.add(Box.createHorizontalStrut(10));
		ratioP.add(new JLabel(Messages.get("CropSettings.ratio") + ": ")); //$NON-NLS-1$ //$NON-NLS-2$
		ratioP.add(w);
		ratioP.add(new JLabel(" " + Messages.get("CropSettings.to") + " ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		ratioP.add(h);
		ratioP.add(Box.createHorizontalStrut(10));
		
		add(ratioP);
	}
	
}
