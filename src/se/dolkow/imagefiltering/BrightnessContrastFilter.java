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




import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;

public class BrightnessContrastFilter extends AbstractPixelModifier {

	private int brightness = 0;
	private float contrast = 1.0f;
	
	public BrightnessContrastFilter(ImageProducer source) {
		super(Messages.get("BrightnessContrastFilter.name"), "brightnesscontrast", source, Messages.get("BrightnessContrastFilter.short_description"));  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}

	protected int[] modifyPixels(int[] pixels, int w, int h) {
		for (int i=0; i<pixels.length; i++) {
			int r = r(pixels[i]);
			int g = g(pixels[i]);
			int b = b(pixels[i]);
			
			r = Math.min(Math.max((int)((r-128) * contrast + 128 + brightness), 0), 255);
			g = Math.min(Math.max((int)((g-128) * contrast + 128 + brightness), 0), 255);
			b = Math.min(Math.max((int)((b-128) * contrast + 128 + brightness), 0), 255);
			
			pixels[i] = (r<<16) + (g<<8) + b;
		}
		return pixels;
	}

	public synchronized int getBrightness() {
		return brightness;
	}

	public synchronized float getContrast() {
		return contrast;
	}
	
	public void setBrightness(int brightness) {
		synchronized (this) {
			this.brightness = Math.max(Math.min(brightness, 255), -255);
		}
		notifyChangeListeners();
	}

	public void setContrast(float contrast) {
		synchronized (this) {
			this.contrast = Math.max(contrast, 0);
		}
		notifyChangeListeners();
	}

	protected void loadAttributeFromTree(Node n) throws TreeParseException {
		if ("brightness".equals(n.getName())) { //$NON-NLS-1$
			setBrightness(Integer.parseInt(n.getTextContents()));
		} else if ("contrast".equals(n.getName())) { //$NON-NLS-1$
			setContrast(Float.parseFloat(n.getTextContents()));
		} else {
			super.loadAttributeFromTree(n);
		}
	}

	protected void saveAttributesToTree(Element parent) {
		super.saveAttributesToTree(parent);
		parent.add(new Leaf("brightness", ""+brightness)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("contrast", ""+contrast)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected synchronized boolean shouldCallModifyPixels() {
		return (brightness!=0 || contrast!=1f);
	}

	public String getLongDescription() {
		return Messages.get("BrightnessContrastFilter.long_description"); //$NON-NLS-1$
	}
}
