package se.dolkow.imagefiltering.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import se.dolkow.imagefiltering.AbstractReduceColorsFilter;
import se.dolkow.imagefiltering.Cacher;
import se.dolkow.imagefiltering.ImageException;
import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.UnthreadedCacher;
import se.dolkow.imagefiltering.Upscaler;
import se.dolkow.imagefiltering.app.gui.configuration.palette.Color;

public class ColorInfoImageDisplay extends ImageDisplay {
	
	private Upscaler upscaler;
	private ColorSelectionListener listener;
	private AbstractReduceColorsFilter reduceColorsFilter;
	private ImageProducer cachedReduceColorsFilter;

	public ColorInfoImageDisplay(ImageProducer source, AbstractReduceColorsFilter reduceColorsFilter, boolean cache) {
		this(source, reduceColorsFilter, cache, true);
	}

	public ColorInfoImageDisplay(ImageProducer source,
		AbstractReduceColorsFilter reduceColorsFilter, boolean cache, boolean useLastWorking) {
		
		super(source, cache, useLastWorking);
		this.reduceColorsFilter = reduceColorsFilter;
		this.cachedReduceColorsFilter = new UnthreadedCacher(reduceColorsFilter);
		this.upscaler = getUpscaler(source);
		MouseHandler mh = new MouseHandler();
		addMouseListener(mh);
		addMouseMotionListener(mh);
	}
	
	public void setColorSelectionListener(ColorSelectionListener listener) {
		this.listener = listener;
	}

	private Upscaler getUpscaler(ImageProducer src) {
		if (src instanceof Cacher) {
			src = ((Cacher)src).getSource();
		}
		if (src instanceof Upscaler) {
			return (Upscaler)src;
		}
		else throw new IllegalArgumentException("expected an Upscaler as a source");
	}
	
	private Color getColor(int rgb) {
		rgb = 0xffffff & rgb; // discard alpha (ARGB -> RGB)
		String name = reduceColorsFilter.getColors().get(rgb);
		if (name != null) return new Color(rgb, name);
		else return new Color(rgb);
	}
	
	private class MouseHandler extends MouseAdapter implements MouseMotionListener {

		public void mouseMoved(MouseEvent e) {
			try {
				BufferedImage image = cachedReduceColorsFilter.getImage();
				int scale = upscaler.getMagnification();
				int x = (e.getX() - lastX) / scale;
				int y = (e.getY() - lastY) / scale;
				if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
					int rgb = image.getRGB(x, y);
					Color color = getColor(rgb);
					if (listener != null) listener.selectedColorChanged(color);
				}
			}
			catch (ImageException e1) {
				return;
			}
		}
	}

	public static interface ColorSelectionListener {
		public void selectedColorChanged(Color color);
	}
}
