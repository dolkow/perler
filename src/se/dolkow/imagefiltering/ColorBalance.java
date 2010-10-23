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
package se.dolkow.imagefiltering;

import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;

/**
 * @author snild
 *
 */
public class ColorBalance extends AbstractPixelModifier {

	int rc, gc, bc;

	public ColorBalance(ImageProducer source) {
		super(Messages.get("ColorBalance.name"), "balance", source, Messages.get("ColorBalance.short_description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		rc = gc = bc = 100;
	}

	protected int[] modifyPixels(int[] pixels, int w, int h) throws ImageException {
		if (rc == 100 && gc == 100 && bc == 100) {
			return pixels;
		}
		for (int i=0; i<pixels.length; i++) {
			int r = Math.min(Math.round(r(pixels[i])*rc*0.01f), 255);
			int g = Math.min(Math.round(g(pixels[i])*gc*0.01f), 255);
			int b = Math.min(Math.round(b(pixels[i])*bc*0.01f), 255);
			pixels[i] = rgb(r,g,b);
		}
		return pixels;
	}

	public synchronized int getRedScale() {
		return rc;
	}

	public void setRedScale(int rc) {
		synchronized(this) {
			this.rc = inAllowedInterval(rc);
		}
		notifyChangeListeners();
	}

	public synchronized int getGreenScale() {
		return gc;
	}

	public void setGreenScale(int gc) {
		synchronized(this) {
			this.gc = inAllowedInterval(gc);
		}
		notifyChangeListeners();
	}

	public synchronized int getBlueScale() {
		return bc;
	}

	public void setBlueScale(int bc) {
		synchronized(this) {
			this.bc = inAllowedInterval(bc);
		}
		notifyChangeListeners();
	}
	
	private static int inAllowedInterval(int v) {
		return Math.min(Math.max(v,0), 200);
	}

	protected void loadAttributeFromTree(Node n) throws TreeParseException {
		if ("r".equals(n.getName())) { //$NON-NLS-1$
			setRedScale(Integer.parseInt(n.getTextContents()));
		} else if ("g".equals(n.getName())) { //$NON-NLS-1$
			setGreenScale(Integer.parseInt(n.getTextContents()));
		} else if ("b".equals(n.getName())) { //$NON-NLS-1$
			setBlueScale(Integer.parseInt(n.getTextContents()));
		} else {
			super.loadAttributeFromTree(n);
		}
	}

	protected void saveAttributesToTree(Element parent) {
		super.saveAttributesToTree(parent);
		parent.add(new Leaf("r", ""+rc)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("g", ""+gc)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("b", ""+bc)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected synchronized boolean shouldCallModifyPixels() {
		return (rc!=100 || gc!=100 || bc!=100);
	}

	public String getLongDescription() {
		return Messages.get("ColorBalance.long_description");  //$NON-NLS-1$
	}
	
	
}
