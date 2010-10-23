package se.dolkow.imagefiltering;

import se.dolkow.imagefiltering.internationalization.Messages;

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



public class Grayscale extends AbstractPixelModifier {

	public Grayscale(ImageProducer source) {
		super(Messages.get("Grayscale.name"), "grayscale", source, Messages.get("Grayscale.short_description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	protected int[] modifyPixels(int[] pixels, int w, int h) {
		for (int i=0; i<pixels.length; i++) {
			int v = (int)(0.3f*r(pixels[i]) + 0.59f*g(pixels[i]) + 0.11f*b(pixels[i]));
			pixels[i] = v + (v<<8) + (v<<16);
		}
		return pixels;
	}

	protected boolean shouldCallModifyPixels() {
		return true;
	}

	public String getLongDescription() {
		return Messages.get("Grayscale.long_description"); //$NON-NLS-1$
	}
}
