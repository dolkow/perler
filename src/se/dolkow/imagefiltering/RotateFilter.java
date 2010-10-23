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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;



import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;

public class RotateFilter extends AbstractImageFilter {

	private int degrees = 0;
	
	public RotateFilter(ImageProducer source) {
		super(Messages.get("RotateFilter.name"), "rotate", source, Messages.get("RotateFilter.short_description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	protected synchronized BufferedImage filter(BufferedImage bimg) throws ImageException {
		int degrees = (this.degrees % 360 + 360) % 360;
		if (degrees == 0) {
			return bimg;
		}
		
		AffineTransform rotation;
		double radians = Math.PI*degrees/180d;
		rotation = AffineTransform.getRotateInstance(radians);
		int sMaxX = bimg.getWidth();
		int sMaxY = bimg.getHeight();
		int corner1x = fit(Math.cos(radians) * sMaxX);
		int corner1y = fit(Math.sin(radians) * sMaxX);
		
		double corner2len = Math.sqrt(sMaxX*sMaxX+sMaxY*sMaxY);
		double corner2angle = Math.atan((sMaxY/(double)sMaxX));
		int corner2x = fit(Math.cos(corner2angle+radians) * corner2len);
		int corner2y = fit(Math.sin(corner2angle+radians) * corner2len);
		
		int corner3x = fit(Math.cos(0.5*Math.PI+radians) * sMaxY);
		int corner3y = fit(Math.sin(0.5*Math.PI+radians) * sMaxY);
		
		int minX = Math.min(Math.min(Math.min(corner1x, corner2x), corner3x), 0);
		int maxX = Math.max(Math.max(Math.max(corner1x, corner2x), corner3x), 0);
		int minY = Math.min(Math.min(Math.min(corner1y, corner2y), corner3y), 0);
		int maxY = Math.max(Math.max(Math.max(corner1y, corner2y), corner3y), 0);
		
		int resw = maxX - minX;
		int resh = maxY - minY;
		
		BufferedImage res = allocateImage(resw, resh);
		Graphics2D g = res.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setBackground(Color.BLACK);
		g.clearRect(0, 0, resw, resh);
		
		g.transform(AffineTransform.getTranslateInstance(-minX, -minY));
		g.drawImage(bimg, rotation, null);
		
		return res;
	}
	
	/**
	 * The integer closest to 0, but larger than d
	 * @param d
	 * @return
	 */
	private int fit(double d) {
		if (d < 0 || (d - (int)d) < 0.01) {
			return (int)d;
		} else {
			return (int)(d+1);
		}
	}

	protected void saveAttributesToTree(Element parent) {
		super.saveAttributesToTree(parent);
		parent.add(new Leaf("degrees", ""+degrees)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected void loadAttributeFromTree(Node attribute) throws TreeParseException {
		if ("degrees".equals(attribute.getName())) { //$NON-NLS-1$
			setRotation(Integer.parseInt(attribute.getTextContents()));
		} else {
			super.loadAttributeFromTree(attribute);
		}
	}
	
	public synchronized int getRotation() {
		return degrees;
	}

	public void setRotation(int degrees) {
		boolean notify = false;
		synchronized(this) {
			if (this.degrees != degrees) {
				notify=true;
				this.degrees = degrees;
			}
		}
		if (notify) {
			notifyChangeListeners();
		}
	}

	public String getLongDescription() {
		return Messages.get("RotateFilter.long_description"); //$NON-NLS-1$
	}

	public boolean allowMagnification() {
		return false;
	}
}
