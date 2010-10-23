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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;



import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;

public class CroppingFilter extends AbstractImageFilter {

	protected int x1 = Integer.MIN_VALUE;
	protected int x2 = Integer.MAX_VALUE;
	protected int y1 = Integer.MIN_VALUE;
	protected int y2 = Integer.MAX_VALUE;
	
	private int oldw = -1;
	private int oldh = -1;
	
	private int aspectW = 1;
	private int aspectH = 1;
	
	public CroppingFilter(ImageProducer source) {
		super(Messages.get("CroppingFilter.name"), "cropper", source, Messages.get("CroppingFilter.short_description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	protected BufferedImage filter(BufferedImage bimg) throws ImageException {
		BufferedImage res;
		boolean changed = false;
		
		synchronized (this) {
			int sw = bimg.getWidth();
			int sh = bimg.getHeight();
			
			int oldx1, oldx2, oldy1, oldy2;
			oldx1 = x1;
			oldy1 = y1;
			oldx2 = x2;
			oldy2 = y2;
			
			if (sw != oldw || sh !=oldh) {
				if (oldw != -1 && oldh != -1) {
					//only reset cropping if we actually had a pic before.
					x1 = 0;
					y1 = 0;
					x2 = sw;
					y2 = sh;
				}
				oldw = sw;
				oldh = sh;
			}
			
			x1 = Math.min(Math.max(x1, 0), sw-1);
			x2 = Math.min(Math.max(x2, x1+1), sw);
			y1 = Math.min(Math.max(y1, 0), sh-1);
			y2 = Math.min(Math.max(y2, y1+1), sh);
			
			int w = x2 - x1;
			int h = y2 - y1;
			
			res = allocateImage(w,h);
			
			Graphics2D g2d = res.createGraphics();
			g2d.drawImage(bimg, 0, 0, w, h, x1, y1, x2, y2, null);
			
			if (oldx1 != x1 || oldx2 != x2 || oldy1 != y1 || oldy2 != y2) {
				changed = true;
			}
		}
		
		if (changed) {
			notifyChangeListeners();
		}
		
		return res;
	}

	public synchronized int getX1() {
		return x1;
	}

	public synchronized int getX2() {
		return x2;
	}

	public synchronized int getY1() {
		return y1;
	}

	public synchronized int getY2() {
		return y2;
	}
	
	public void setX1(int x1) {
		synchronized (this) {
			this.x1 = x1;
		}
		notifyChangeListeners();
	}
	
	public void setX2(int x2) {
		synchronized (this) {
			this.x2 = x2;
		}
		notifyChangeListeners();
	}
		
	public void setY1(int y1) {
		synchronized (this) {
			this.y1 = y1;
		}
		notifyChangeListeners();
	}

	public void setY2(int y2) {
		synchronized (this) {
			this.y2 = y2;
		}
		notifyChangeListeners();
	}

	protected void loadAttributeFromTree(Node n) throws TreeParseException {
		if ("rectangle".equals(n.getName())) { //$NON-NLS-1$
			Node pos;
			
			pos = n.getChild(0);
			setX1(Integer.parseInt(pos.getChild(0).getTextContents()));
			setY1(Integer.parseInt(pos.getChild(1).getTextContents()));
			
			pos = n.getChild(1);
			setX2(Integer.parseInt(pos.getChild(0).getTextContents()));
			setY2(Integer.parseInt(pos.getChild(1).getTextContents()));
		} else if ("aspectwidth".equals(n.getName())) { //$NON-NLS-1$
			setAspectWidth(Integer.parseInt(n.getTextContents()));
		} else if ("aspectheight".equals(n.getName())) { //$NON-NLS-1$
			setAspectHeight(Integer.parseInt(n.getTextContents()));
		} else {
			super.loadAttributeFromTree(n);
		}
	}
	
	protected void saveAttributesToTree(Element parent) {
		super.saveAttributesToTree(parent);
		Element rectangle = new Element("rectangle"); //$NON-NLS-1$
		Element pos1 = new Element("pos"); //$NON-NLS-1$
		Element pos2 = new Element("pos"); //$NON-NLS-1$
		pos1.add(new Leaf("x", ""+x1)); //$NON-NLS-1$ //$NON-NLS-2$
		pos1.add(new Leaf("y", ""+y1)); //$NON-NLS-1$ //$NON-NLS-2$
		pos2.add(new Leaf("x", ""+x2)); //$NON-NLS-1$ //$NON-NLS-2$
		pos2.add(new Leaf("y", ""+y2)); //$NON-NLS-1$ //$NON-NLS-2$
		rectangle.add(pos1);
		rectangle.add(pos2);
		parent.add(rectangle);
		
		parent.add(new Leaf("aspectwidth", ""+aspectW)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("aspectheight", ""+aspectH)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getLongDescription() {
		return Messages.get("CroppingFilter.long_description"); //$NON-NLS-1$
	}

	public synchronized int getAspectWidth() {
		return aspectW;
	}
	
	public synchronized int getAspectHeight() {
		return aspectH;
	}
	
	public void setAspectWidth(int w) {
		synchronized(this) {
			aspectW = w;
		}
		notifyChangeListeners();
	}
	
	public void setAspectHeight(int h) {
		synchronized(this) {
			aspectH = h;
		}
		notifyChangeListeners();
	}
}
