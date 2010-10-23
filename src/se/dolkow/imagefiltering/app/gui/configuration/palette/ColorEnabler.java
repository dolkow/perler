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

public interface ColorEnabler {

	/**
	 * Enable/disable a color. This will enable all colors with this particular
	 * rgb value. The name parameter determines what to actually call the color.
	 * @param rgb
	 * @param name
	 * @param enabled
	 * @param moreChangesComing will more changes be made soon?
	 */
	public void setColorEnabled(int rgb, String name, boolean enabled, boolean moreChangesComing);
	
	/**
	 * Called to inform that we're done changing for now. Useful to use if you've
	 * been calling setColorEnabled with moreChangesComing=true.
	 */
	public void doneChanging();

	public boolean isColorEnabled(int rgb);
}
