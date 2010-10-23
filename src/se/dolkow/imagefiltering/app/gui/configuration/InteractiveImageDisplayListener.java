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

public interface InteractiveImageDisplayListener {

	/**
	 * Called when the user clicks somewhere.
	 * @param x
	 * @param y
	 */
	public void click(int x, int y);
	
	/**
	 * Called when the user has drawn a rectangle
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void draw(int x1, int y1, int x2, int y2);
	
}
