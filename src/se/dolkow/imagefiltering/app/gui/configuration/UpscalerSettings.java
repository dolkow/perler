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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.dolkow.imagefiltering.Upscaler;
import se.dolkow.imagefiltering.internationalization.Messages;

public class UpscalerSettings extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Upscaler sc;
	
	public UpscalerSettings(Upscaler f) {
		this.sc = f;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(Box.createVerticalGlue());
		
		final JSlider slider = new JSlider(1, 20, sc.getMagnification());
		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (slider.getValue() != sc.getMagnification()) {
					sc.setMagnification(slider.getValue());
				}
			}
		});
		
		Hashtable<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
		labels.put(1, new JLabel("1x")); //$NON-NLS-1$
		labels.put(10, new JLabel("10x")); //$NON-NLS-1$
		labels.put(20, new JLabel("20x")); //$NON-NLS-1$
		slider.setPaintLabels(true);
		slider.setLabelTable(labels);
		
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1);
		
		add(slider);
		add(Box.createVerticalGlue());
		
		JPanel lowerContainer = new JPanel();
		final JComboBox combo = new JComboBox(Upscaler.Effect.values());
		combo.setEditable(false);
		combo.setSelectedItem(sc.getEffect());
		
		combo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				sc.setEffect((Upscaler.Effect)combo.getSelectedItem());
			}
		});
		
		lowerContainer.add(new JLabel(Messages.get("UpscalerSettings.effect") + ": "));  //$NON-NLS-1$//$NON-NLS-2$
		lowerContainer.add(combo);
		lowerContainer.add(Box.createHorizontalStrut(10));
		
		final JCheckBox tiles = new JCheckBox(Messages.get("UpscalerSettings.split_into_tiles"), f.getDrawTiles()); //$NON-NLS-1$
		final JSpinner tileWidth = new JSpinner();
		final JSpinner tileHeight= new JSpinner();
		tileWidth.setEditor(new JSpinner.NumberEditor(tileWidth, "#")); //$NON-NLS-1$
		tileWidth.setValue(f.getTileWidth());
		tileHeight.setEditor(new JSpinner.NumberEditor(tileHeight, "#")); //$NON-NLS-1$
		tileHeight.setValue(f.getTileHeight());
		
		tiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sc.setDrawTiles(tiles.isSelected());
			}
		});
		
		tileWidth.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sc.setTileWidth((Integer)tileWidth.getValue());
			}
		});
		tileHeight.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sc.setTileHeight((Integer)tileHeight.getValue());
			}
		});
		
		lowerContainer.add(tiles);
		lowerContainer.add(tileWidth);
		lowerContainer.add(new JLabel("x")); //$NON-NLS-1$
		lowerContainer.add(tileHeight);
		
		add(lowerContainer);
		
		add(Box.createVerticalGlue());
	}
}
