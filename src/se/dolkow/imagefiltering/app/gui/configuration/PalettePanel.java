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

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import se.dolkow.imagefiltering.AbstractReduceColorsFilter;
import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.ImageProducerListener;

public class PalettePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	protected final AbstractReduceColorsFilter filter;
	
	public PalettePanel(AbstractReduceColorsFilter f) {
		this.filter = f;
		
		filter.addChangeListener(new ImageProducerListener() {
			public void changed(ImageProducer producer) {
				repaint();
			}
		});
		setOpaque(true);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Map<Integer, String> colors = filter.getColors();
		synchronized ( colors ) {
			int h = getHeight();
			float cw = getWidth() / (float)colors.size();
			int i = 1;
			int oldx=0;
			int x = 0;
			for (Entry<Integer,String> color : colors.entrySet()) {
				g.setColor(new Color(color.getKey()));
				x = (int)(i * cw);
				g.fillRect(oldx, 0, x-oldx, h);
				oldx = x;
				i++;
			}
		}
	}
	
}
