package se.dolkow.imagefiltering.app.gui.configuration.palette;

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

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class Swatch extends JPanel {
	private static final long serialVersionUID = 1L;

	public Swatch(Color c) {
		this(c.getRGB());
	}

	public Swatch(int rgb) {
		super();
		setColor(rgb);
		
		Dimension dim = new Dimension(20,20);
		setMinimumSize(dim);
		setPreferredSize(dim);
		setMaximumSize(dim);
		setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
		setOpaque(true);
	}

	public void setColor(int rgb) {
		setBackground(new java.awt.Color(rgb));
	}
}
