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

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;

import javax.swing.JPanel;

import se.dolkow.imagefiltering.internationalization.Messages;

public class ImageLoadWaiter {
	
	private static final long serialVersionUID = 1L;

	private static final Component c = new JPanel();
	
	private final MediaTracker mt;
	
	public ImageLoadWaiter() {
		mt = new MediaTracker(c);
	}
	
	public ImageLoadWaiter(Image img) {
		mt = new MediaTracker(c);
		addImage(img);
	}
	
	public void addImage(Image img) {
		mt.addImage(img, 0);
	}
	
	public void removeImage(Image img) {
		mt.removeImage(img);
	}
	
	public void waitForAll() throws InterruptedException, ImageException {
		if (mt.waitForAll(0)) {
			return;
		} else {
			throw new ImageException(Messages.get("ImageLoadWaiter.error")); //$NON-NLS-1$
		}
	}
}
