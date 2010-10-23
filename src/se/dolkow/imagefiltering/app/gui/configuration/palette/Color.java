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

public class Color {

	protected int rgb;
	protected String name;
	
	public Color(int rgb) {
		this.rgb = rgb;
		name = Integer.toHexString(rgb);
		while (name.length() < 6) {
			name = "0" + name; //$NON-NLS-1$
		}
		name = "#" + name; //$NON-NLS-1$
	}
	
	public Color(int rgb, String name) {
		this.rgb = rgb;
		this.name = name;
	}
	
	public synchronized String getName() {
		return name;
	}
	
	public synchronized int getRGB() {
		return rgb;
	}
}
