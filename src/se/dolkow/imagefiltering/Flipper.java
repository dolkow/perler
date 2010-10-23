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

public class Flipper extends AbstractPixelModifier {
	protected FlipMode mode = FlipMode.None;

	public Flipper(ImageProducer source) {
		super(Messages.get("Flipper.name"), "flipper", source, Messages.get("Flipper.short_description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	protected synchronized int[] modifyPixels(int[] pixels, int w, int h) throws ImageException {
		if (mode == FlipMode.None) {
			return pixels;
		}
		
		int[] res = new int[pixels.length];
		
		if (mode == FlipMode.Horizontal) {
			for (int y = 0; y<h; y++) {
				int rowOffset = y * w;
				for (int x = 0; x<w; x++) {
					int respos = rowOffset + w-(x+1);
					res[respos] = pixels[rowOffset + x];
				}
			}
		} else if (mode == FlipMode.Vertical) {
			for (int y = 0; y<h; y++) {
				int rowOffset = y * w;
				int resRowOffset = (h-(y+1)) * w;
				for (int x = 0; x<w; x++) {
					res[resRowOffset+x] = pixels[rowOffset+x];
				}
			}
		}
		
		return res;
	}

	protected void saveAttributesToTree(Element parent) {
		super.saveAttributesToTree(parent);
		parent.add(new Leaf("mode", mode.toString().toLowerCase())); //$NON-NLS-1$
	}

	protected void loadAttributeFromTree(Node n) throws TreeParseException {
		if ("mode".equals(n.getName())) { //$NON-NLS-1$
			String value = n.getTextContents();
			if ("horizontal".equals(value)) { //$NON-NLS-1$
				setFlipMode(FlipMode.Horizontal);
			} else if ("vertical".equals(value)) { //$NON-NLS-1$
				setFlipMode(FlipMode.Vertical);
			} else if ("none".equals(value)) { //$NON-NLS-1$
				setFlipMode(FlipMode.None);
			} else {
				System.out.println(Messages.get("Flipper.flip_mode_warning") + value); //$NON-NLS-1$
			}
		} else {
			super.loadAttributeFromTree(n);
		}
	}

	public void setFlipMode(FlipMode mode) {
		boolean changed = false;
		synchronized (this) {
			if (this.mode != mode) {
				this.mode = mode;
				changed = true;
			}
		}
		
		if (changed) {
			notifyChangeListeners();
		}
	}
	
	public synchronized FlipMode getFlipMode() {
		return mode;
	}
	
	public synchronized boolean shouldCallModifyPixels() {
		return mode != FlipMode.None;
	}
	
	public static enum FlipMode {
		Horizontal, Vertical, None
	}

	public String getLongDescription() {
		return Messages.get("Flipper.long_description"); //$NON-NLS-1$
	}
}
