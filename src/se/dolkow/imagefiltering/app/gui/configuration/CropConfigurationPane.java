package se.dolkow.imagefiltering.app.gui.configuration;

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

import se.dolkow.imagefiltering.CroppingFilter;
import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.ImageProducerListener;
import se.dolkow.imagefiltering.internationalization.Messages;

public class CropConfigurationPane extends SimpleConfigurationPane implements InteractiveImageDisplayListener, ImageProducerListener {
	private static final long serialVersionUID = 1L;
	private final CroppingFilter crop;
	private final InteractiveImageDisplay disp;
	
	protected CropConfigurationPane(CroppingFilter producer, InteractiveImageDisplay display) {
		super(producer, display);
		producer.addChangeListener(this);
		this.disp = display;
		disp.setInteractionListener(this);
		this.crop = producer;
		disp.setRectangle(crop.getX1(), crop.getY1(), crop.getX2(), crop.getY2());
		disp.setAspect1(producer.getAspectHeight());
		disp.setAspect2(producer.getAspectWidth());
		
		addSettingsTab(Messages.get("CropConfigurationPane.cropping_tab"), new CropSettings(producer, disp)); //$NON-NLS-1$
	}
	
	public static CropConfigurationPane create(CroppingFilter p) {
		InteractiveImageDisplay disp = new InteractiveImageDisplay(p.getSource());
		return new CropConfigurationPane(p, disp);
	}

	public void click(int x, int y) {
		crop.setX1(Integer.MIN_VALUE); crop.setX2(Integer.MAX_VALUE);
		crop.setY1(Integer.MIN_VALUE); crop.setY2(Integer.MAX_VALUE);
		disp.setRectangle(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public void draw(int x1, int y1, int x2, int y2) {
		if (x2 < x1) {
			int tmp = x2;
			x2 = x1;
			x1 = tmp;
		}
		if (y2 < y1) {
			int tmp = y2;
			y2 = y1;
			y1 = tmp;
		}
		crop.setX1(x1);
		crop.setY1(y1);
		crop.setX2(x2);
		crop.setY2(y2);
	}

	public void changed(ImageProducer producer) {
		disp.setRectangle(crop.getX1(), crop.getY1(), crop.getX2(), crop.getY2());
	}
}
