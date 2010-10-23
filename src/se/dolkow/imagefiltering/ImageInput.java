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
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;

public class ImageInput extends AbstractImageProducer {
	
	private String currentPath = null;
	
	public ImageInput(String path) {
		this();
		if (path != null) {
			setPath(path);
		}
	}
	
	public ImageInput() {
		super("loader"); //$NON-NLS-1$
	}

	public void setPath(String path) {
		boolean changed = false;
		synchronized(this) {
			if ((path == null && currentPath != null ) ||
					(path != null && !path.equals(currentPath)) ) {
				currentPath = path;
				changed = true;
			}
		}
		if (changed) {
			notifyChangeListeners();
		}
	}
	
	protected BufferedImage loadImage() throws ImageException {
		BufferedImage bimg;
		try {
			String path;
			synchronized(this) {
				path = currentPath;
			}
			
			if (path == null) {
				throw new ImageException(Messages.get("ImageInput.no_image")); //$NON-NLS-1$
			}
			
			Image img = Toolkit.getDefaultToolkit().createImage(path);
			ImageLoadWaiter ilw = new ImageLoadWaiter(img);
			ilw.waitForAll();
			
			
			bimg = allocateImage(img.getWidth(null), img.getHeight(null));
			Graphics2D g2d = bimg.createGraphics();
			g2d.drawImage(img, 0, 0, null);
			
		} catch (InterruptedException e) {
			throw new NoImageReadyException(Messages.get("ImageInput.interrupted")); //$NON-NLS-1$
		} catch (OutOfMemoryError e) {
			throw new AllocationException(Messages.get("ImageInput.large_image"), e); //$NON-NLS-1$
		}
		
		return bimg;
	}
	
	public String getCurrentPath() {
		synchronized(this) {
			return currentPath;
		}
	}
	
	public BufferedImage getImage() throws ImageException {
		return loadImage();
	}
	
	public String toString() {
		return Messages.get("ImageInput.name"); //$NON-NLS-1$
	}

	protected void loadAttributeFromTree(Node n) {
		if ("path".equals(n.getName())) { //$NON-NLS-1$
			setPath(n.getTextContents());
		}
	}
	
	public synchronized void saveAttributesToTree(Element parent) {
		if (currentPath != null) {
			parent.add(new Leaf("path", currentPath)); //$NON-NLS-1$
		}
	}

	public String getDescription() {
		return Messages.get("ImageInput.description"); //$NON-NLS-1$
	}

	public String getLongDescription() {
		return getDescription();
	}
}
