package se.dolkow.imagefiltering;

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

import java.awt.image.BufferedImage;

public abstract class AbstractPixelModifier extends AbstractImageFilter {

	public AbstractPixelModifier(String name, String xmlTagName, ImageProducer source, String description) {
		super(name, xmlTagName, source, description);
	}
	
	protected synchronized final BufferedImage filter(BufferedImage bimg) throws ImageException {
		if (shouldCallModifyPixels()) {
			int w = bimg.getWidth();
			int h = bimg.getHeight();
			BufferedImage res = allocateImage(w, h);
			
			int[] pixels = new int[w*h];
			bimg.getRGB(0, 0, w, h, pixels, 0, w);
			
			pixels = modifyPixels(pixels, w, h);
			
			res.setRGB(0, 0, w, h, pixels, 0, w);
			
			return res;
		} else {
			return bimg;
		}
	}
	
	/**
	 * Change the pixels of an image. Using the pixels array as the returned data array
	 * is allowed. 
	 * @param pixels the source data. Modification <i>is</i> allowed.
	 * @param w width of the pixel arrays.
	 * @param h height of the pixel arrays.
	 * @return a pixel array with the result data, of the same dimensions as the input array.
	 * @throws ImageException 
	 */
	protected abstract int[] modifyPixels(int[] pixels, int w, int h) throws ImageException;
	
	/**
	 * Used by the getImage implementation to determine if it needs to call
	 * the modifyPixels() function. The concrete implementations can use this 
	 * to speed up the throughput when its settings would result in an output
	 * image identical to the input. <br><br>
	 * Note: The implementations don't have to consider the <b>active</b>
	 * switch. This is done automatically in the filter() method.
	 * @return true if the modifyPixels() function should be used 
	 * to deliver output.
	 */
	protected abstract boolean shouldCallModifyPixels();
	
	protected static final int r(int color) {
		return (color & 0xFF0000)>>16;
	}

	protected static final int g(int color) {
		return (color & 0x00FF00)>>8;
	}
	
	protected static final int b(int color) {
		return (color & 0x0000FF);
	}
	
	protected static final int rgb(int r, int g, int b) {
		return (r << 16) + (g << 8) + b;
	}
}
