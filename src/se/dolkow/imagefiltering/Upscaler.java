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
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;

import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;

public class Upscaler extends AbstractImageFilter {
	
	protected int magnification = 8;
	protected Effect effect = Effect.Beads;
	protected int tileWidth = 29;
	protected int tileHeight = 29;
	protected boolean drawTiles = false;
	
	private Image pearl;
	private final Image pearlOriginal;
	
	protected Upscaler(String name, String xmlTagName, ImageProducer source) throws ImageException, InterruptedException {
		super(name, xmlTagName, source, Messages.get("Upscaler.short_description")); //$NON-NLS-1$
		
		URL bead = getClass().getResource("/resource/beadoverlay.png"); //$NON-NLS-1$
		if (bead == null) {
			throw new ImageException(Messages.getFormatted("Upscaler.missing_file", "beadoverlay.png"));  //$NON-NLS-1$//$NON-NLS-2$
		}
		pearlOriginal =  Toolkit.getDefaultToolkit().createImage(bead);
		ImageLoadWaiter ilw = new ImageLoadWaiter(pearlOriginal);
		ilw.waitForAll();
	}
	
	public Upscaler(ImageProducer source) throws ImageException, InterruptedException {
		this(Messages.get("Upscaler.name"), "upscaler", source); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected synchronized BufferedImage filter(BufferedImage bimg) throws ImageException {
		int w = bimg.getWidth();
		int h = bimg.getHeight();
		int rw = w * magnification;
		int rh = h * magnification;
		
		if (rw*rh > 5242880) {
			Object[] args = {rw, rh};
			throw new ImageTooLargeException(Messages.getFormatted("Upscaler.too_large", args)); //$NON-NLS-1$
		}
		
		BufferedImage res = allocateImage(rw, rh);
		
		Graphics2D g2d = res.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.drawImage(bimg, 0, 0, rw, rh, 0, 0, w, h, null); 
		
		
		if (effect == Effect.Beads) {
			drawBeads(g2d, rw, rh);
		}
		
		if (tileWidth > 0 && tileHeight>0 && drawTiles) {
			drawGrid(g2d, rw, rh, -1, -1, magnification*tileWidth, magnification*tileHeight, Color.WHITE);
			drawGrid(g2d, rw, rh,  0,  0, magnification*tileWidth, magnification*tileHeight, Color.GRAY);
			drawGrid(g2d, rw, rh,  1,  1, magnification*tileWidth, magnification*tileHeight, Color.BLACK);
		}
		
		if (effect == Effect.Grid) {
			drawGrid(g2d, rw, rh, magnification, magnification, Color.GRAY);
		}
		
		return res;
	}

	private synchronized void drawBeads(Graphics2D g, int rw, int rh) {
		if (pearl == null) {
			createPearlImage();
		}
		for (int y=0; y<rh; y+=magnification) {
			for (int x=0; x<rw; x+=magnification) {
				g.drawImage(pearl, x, y, null);
			}
		}
	}

	public synchronized int getMagnification() {
		return magnification;
	}
	
	public void setMagnification(int magn) {
		synchronized(this) {
			this.magnification = magn;
			clearPearlImage();
		}
		notifyChangeListeners();
	}
	
	protected synchronized void clearPearlImage() {
		pearl = null;
	}

	protected synchronized void createPearlImage() {
		pearl = pearlOriginal.getScaledInstance(magnification, magnification, Image.SCALE_SMOOTH);
		ImageLoadWaiter ilw;
		try {
			ilw = new ImageLoadWaiter(pearl);
			ilw.waitForAll();
		} catch (ImageException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void loadAttributeFromTree(Node n) throws TreeParseException {
		if ("magnification".equals(n.getName())) { //$NON-NLS-1$
			setMagnification(Integer.parseInt(n.getTextContents()));
		}
		else if ("drawtiles".equals(n.getName())) { //$NON-NLS-1$
			setDrawTiles("1".equals(n.getTextContents()) || "true".equals(n.getTextContents())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if ("tileheight".equals(n.getName())) { //$NON-NLS-1$
			setTileHeight(Integer.parseInt(n.getTextContents()));
		}
		else if ("tilewidth".equals(n.getName())) { //$NON-NLS-1$
			setTileWidth(Integer.parseInt(n.getTextContents()));
		}
		else if ("tilesize".equals(n.getName())) { //$NON-NLS-1$
			setTileWidth(Integer.parseInt(n.getTextContents()));
			setTileHeight(Integer.parseInt(n.getTextContents()));
		}
		else if ("effect".equals(n.getName())) { //$NON-NLS-1$
			String value = n.getTextContents().toLowerCase();
			if("beads".equals(value)) { //$NON-NLS-1$
				setEffect(Effect.Beads);
			} else if ("grid".equals(value)) { //$NON-NLS-1$
				setEffect(Effect.Grid);
			} else if ("none".equals(value)) { //$NON-NLS-1$
				setEffect(Effect.None);
			} else {
				throw new TreeParseException(
						Messages.getFormatted("Upscaler.unknown_effect", new Object[]{value, getClass().getSimpleName()})); //$NON-NLS-1$
			}
		} else {
			super.loadAttributeFromTree(n);
		}
	}
	
	public void setEffect(Effect effect) {
		boolean changed = false;
		synchronized (this) {
			if (this.effect != effect) {
				this.effect = effect;
				changed = true;
			}
		}
		if (changed) {
			notifyChangeListeners();
		}
	}
	
	public synchronized Effect getEffect() {
		return effect;
	}

	protected void saveAttributesToTree(Element parent) {
		super.saveAttributesToTree(parent);
		parent.add(new Leaf("magnification", ""+magnification)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("effect", effect.toString().toLowerCase())); //$NON-NLS-1$
		parent.add(new Leaf("drawtiles", "" + drawTiles)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("tilewidth", "" + tileWidth)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("tileheight", "" + tileHeight)); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public enum Effect {
		None, Beads, Grid
	}

	public synchronized boolean getDrawTiles() {
		return drawTiles;
	}
	
	public void setDrawTiles(boolean b) {
		synchronized (this) {
			drawTiles = b;
		}
		notifyChangeListeners();
	}
	
	public synchronized int getTileWidth() {
		return tileWidth;
	}
	
	public synchronized int getTileHeight() {
		return tileHeight;
	}
	
	public void setTileWidth(int w) {
		synchronized (this) {
			this.tileWidth = w;
		}
		notifyChangeListeners();
	}
	
	public void setTileHeight(int h) {
		synchronized (this) {
			this.tileHeight = h;
		}
		notifyChangeListeners();
	}

	public boolean allowMagnification() {
		return false;
	}

	public String getLongDescription() {
		return Messages.get("Upscaler.long_description");  //$NON-NLS-1$
	}
}
