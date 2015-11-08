package se.dolkow.imagefiltering.gui;

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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import se.dolkow.imagefiltering.CacheEmptyException;
import se.dolkow.imagefiltering.ImageException;
import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.ImageProducerListener;
import se.dolkow.imagefiltering.UnthreadedCacher;
import se.dolkow.imagefiltering.internationalization.Messages;

public class ImageDisplay extends JPanel implements ImageProducerListener {
	private static final long serialVersionUID = 1L;
	private Dimension oldImgDim = new Dimension();
	protected int lastX = 0;
	protected int lastY = 0;
	private ImageProducer source;
	private final boolean useLastWorking;
	private boolean enableMagnify = true;
	
	/**
	 * Create an ImageDisplay without a cacher.
	 * @param source
	 */
	public ImageDisplay(ImageProducer source) {
		this(source, false, true);
	}
	
	public ImageDisplay(ImageProducer source, boolean cache) {
		this(source, cache, true);
	}
	
	public ImageDisplay(ImageProducer source, boolean cache, boolean useLastWorking) {
		super(true);
		
		this.useLastWorking = useLastWorking;
		this.enableMagnify = source.allowMagnification();
		
		if (cache) {
			this.source = new UnthreadedCacher(source);
		} else {
			this.source = source;
		}
		this.source.addChangeListener(this);
	}

	public void paintComponent(Graphics g) {
		BufferedImage bimg = null;
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		boolean success = false;
		String failmsg = Messages.get("ImageDisplay.no_image"); //$NON-NLS-1$
		try {
			bimg = source.getImage();
			success = true;
		} catch (CacheEmptyException e) {
			failmsg = e.getLocalizedMessage();
			if (useLastWorking) {
				try {
					bimg = source.getLastWorkingImage();
				} catch (ImageException ie) {
					bimg = null; //getting the last working image didn't work.
				}
			}
		} catch (ImageException e) {
			bimg = null;
			failmsg = e.getLocalizedMessage();
		}
		
		super.paintComponent(g);
		if (bimg != null) {
			int imgw = bimg.getWidth();
			int imgh = bimg.getHeight();
			
			if (oldImgDim.width != imgw || oldImgDim.height != imgh) {
				oldImgDim.width = imgw;
				oldImgDim.height = imgh;
				setPreferredSize(oldImgDim);
				revalidate();
			}
			
			if (enableMagnify) {
				int mag = 1;
				int availw = getWidth();
				int availh = getHeight();
				
				while (imgw*(mag+1) <= availw && imgh*(mag+1) <= availh && mag<4 ) {
					mag++;
				}
				
				imgw *= mag;
				imgh *= mag;
				
				if (mag > 1) {
					if (success) {
						failmsg = ""; //$NON-NLS-1$
					}
					success = false;
					failmsg += Messages.getFormatted("ImageDisplay.enlarged", mag);  //$NON-NLS-1$
				}
			}
			
			lastX = (getWidth() - imgw)/2;
			lastY = (getHeight() - imgh)/2;
			g.drawImage(bimg, lastX, lastY, imgw, imgh, null);
		}
		
		if (!success) {
			g.setColor(Color.WHITE);
			for (int i=0; i<3; i++) {
				for (int j=0; j<3; j++) {
					g.drawString(failmsg, 9+i, 19+j);
				}
			}
			g.setColor(Color.BLACK);
			g.drawString(failmsg, 10, 20);
		}
		else paintOverlay(g);
	}
	
	protected void paintOverlay(Graphics g) {
	}

	protected void setEnableMagnify(boolean b) {
		synchronized(this) {
			enableMagnify = b;
		}
		repaint();
	}

	public void changed(ImageProducer producer) {
		repaint();
	}
}
