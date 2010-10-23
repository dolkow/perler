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
import java.awt.image.BufferedImage;



import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;

public abstract class AbstractImageFilter extends AbstractImageProducer implements ImageFilter {
	
	private boolean active = true;
	private final String name;
	private ImageProducer source;
	private final String description;
	
	public AbstractImageFilter(String name, String xmlTagName, ImageProducer source, String description) {
		super(xmlTagName);
		this.name = name;
		this.source = source;
		source.addChangeListener(this);
		this.description = description;
	}
	
	public ImageProducer getSource() {
		return source;
	}
	
	public String getDescription() {
		return description;
	}
	
	/**
	 * Filter the image and return the result.
	 * @param bimg the input image. It should <b>not</b> be modified.
	 * @return the filtered (output) image.
	 * @throws ImageException 
	 */
	protected abstract BufferedImage filter(BufferedImage bimg) throws ImageException;
	
	public final BufferedImage getLastWorkingImage() throws ImageException {
		return processImage(source.getLastWorkingImage());
	}
	
	public final BufferedImage processImage(BufferedImage bimg) throws ImageException {
		if (bimg == null) {
			return null;
		}
		try {
			if (active) {
				if( bimg.getType() != BufferedImage.TYPE_INT_RGB) {
					throw new ImageFormatException(Messages.get("AbstractImageFilter.img_type_error")); //$NON-NLS-1$
				}
				
				BufferedImage filtered = filter(bimg);
				return filtered;
			} else {
				return bimg;
			}
		} catch (OutOfMemoryError e) {
			throw new ImageException(Messages.get("AbstractImageFilter.out_of_memory") + ": " + e.getLocalizedMessage(), e);  //$NON-NLS-1$//$NON-NLS-2$
		}
	}
	
	public final BufferedImage getImage() throws ImageException {
		return processImage(source.getImage());
	}
	
	public void setActive(boolean b) {
		boolean old = active;
		this.active = b;
		if (old != b) {
			notifyChangeListeners();
		}
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void changed(ImageProducer producer) {
		notifyChangeListeners();
	}
	
	public String toString() {
		return name;
	}
	
	protected void loadAttributeFromTree(Node n) throws TreeParseException {
		if (n.getName().equals("active")) { //$NON-NLS-1$
			String value = n.getTextContents();
			setActive("1".equals(value) || "true".equals(value)); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			super.loadAttributeFromTree(n);
		}
	}

	public void cleanup() {
		super.cleanup();
		source.cleanup();
	}
	
	protected void saveAttributesToTree(Element parent) {
		parent.add(new Leaf("active", active ? "true" : "false")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	protected void drawGrid(Graphics2D g, int w, int h, int spacex, int spacey, Color c) {
		drawGrid(g, w, h, 0, 0, spacex, spacey, c);
	}
	
	protected void drawGrid(Graphics2D g, int w, int h, int offsetx, int offsety, int spacex, int spacey, Color c) {
		if (spacex > 0 && spacey > 0) {
			g.setColor(c);
			h = h + offsety;
			w = w + offsetx;
			for (int y=offsety; y<h; y+=spacey) {
				g.drawLine(0, y, w, y);
			}
			for (int x=offsetx; x<w; x+=spacex) {
				g.drawLine(x, 0, x, h);
			}
		}
	}
	
}
