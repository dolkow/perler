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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;



import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;

public class ShrinkFilter extends AbstractImageFilter {

	private int maxw = 400;
	private int maxh = 300;
	private boolean smooth = true;
	private ShrinkResultListener resListener = null;
	
	public ShrinkFilter(ImageProducer source) {
		super(Messages.get("ShrinkFilter.name"), "shrinker", source, Messages.get("ShrinkFilter.short_description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public ShrinkFilter(ImageProducer source, int maxw, int maxh) {
		this(source);
		setMaxHeight(maxh);
		setMaxWidth(maxw);
	}

	protected synchronized BufferedImage filter(BufferedImage bimg) throws ImageException {
		int srcw = bimg.getWidth();
		int srch = bimg.getHeight();
		
		if (maxw < 1 || maxh < 1) {
			throw new ImageException(Messages.getFormatted("ShrinkFilter.invalid_size", getClass().getSimpleName())); //$NON-NLS-1$
		}
		
		float maxw = this.maxw;
		float maxh = this.maxh;
		
		int dstw = srcw;
		int dsth = srch;
		
		if (srcw > maxw || srch > maxh) {
			if (srcw/maxw > srch/maxh) {
				dstw = (int)maxw;
				dsth = Math.round(srch * dstw/(float)srcw); 
			} else {
				dsth = (int)maxh;
				dstw = Math.round(srcw * dsth/(float)srch); 
			}
		}
		
		dstw = Math.max(dstw, 1); //at least 1!
		dsth = Math.max(dsth, 1); //at least 1!
		
		if (smooth) {
			while (bimg.getWidth() > dstw*2) {
				int halfw = (int)(0.5*bimg.getWidth() + 1);
				int halfh = (int)(0.5*bimg.getHeight() + 1);
				bimg = resize(bimg, halfw, halfh);
			}
		}
		
		if (resListener != null) {
			resListener.result(dstw, dsth, this);
		}
		
		return resize(bimg, dstw, dsth);
	}
	
	private synchronized BufferedImage resize(BufferedImage bimg, int dstw, int dsth) throws AllocationException {
		//System.out.println("resize(<bimg>, " + dstw + ", " + dsth + ")");
		dstw = Math.max(dstw, 1);
		dsth = Math.max(dsth, 1);
		BufferedImage res = allocateImage(dstw, dsth);
		Graphics2D g2d = res.createGraphics();
		if (smooth) {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
		g2d.drawImage(bimg, 0, 0, dstw, dsth, null);
		return res;
	}

	public synchronized int getMaxWidth() {
		return maxw;
	}

	public synchronized int getMaxHeight() {
		return maxh;
	}
	
	public synchronized boolean getSmooth() {
		return smooth;
	}
	
	public void setMaxDimensions(int w, int h) {
		boolean changed = false;
		synchronized(this) {
			changed = (maxw!=w) || (maxh!=h);
			maxw = w;
			maxh = h;
		}
		notifyChangeListeners(changed);
	}
	
	public void setMaxWidth(int w) {
		boolean changed = false;
		synchronized (this) {
			changed = (maxw!=w);
			maxw = w;
		}
		notifyChangeListeners(changed);
	}

	public void setMaxHeight(int h) {
		boolean changed = false;
		synchronized (this) {
			changed = (maxh!=h);
			maxh = h;
		}
		notifyChangeListeners(changed);
	}
	
	public void setSmooth(boolean b) {
		boolean changed = false;
		synchronized (this) {
			changed = (smooth != b);
			smooth = b;
		}
		notifyChangeListeners(changed);
	}

	protected void loadAttributeFromTree(Node n) throws TreeParseException {
		if ("maxwidth".equals(n.getName())) { //$NON-NLS-1$
			setMaxWidth(Integer.parseInt(n.getTextContents()));
		} else if ("maxheight".equals(n.getName())) { //$NON-NLS-1$
			setMaxHeight(Integer.parseInt(n.getTextContents()));
		} else if ("smooth".equals(n.getName())) { //$NON-NLS-1$
			setSmooth("1".equals(n.getTextContents()) || "true".equals(n.getTextContents())); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			super.loadAttributeFromTree(n);
		}
	}
	
	protected void saveAttributesToTree(Element parent) {
		super.saveAttributesToTree(parent);
		parent.add(new Leaf("maxwidth", ""+maxw)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("maxheight", ""+maxh)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("smooth", smooth ? "true" : "false")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public String getLongDescription() {
		return Messages.get("ShrinkFilter.long_description");  //$NON-NLS-1$
	}
	
	public synchronized void setResultListener(ShrinkResultListener l) {
		resListener = l;
	}
	
	
}
