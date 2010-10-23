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
package se.dolkow.imagefiltering.app.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import se.dolkow.imagefiltering.ImageInput;
import se.dolkow.imagefiltering.ShrinkFilter;
import se.dolkow.imagefiltering.ThreadedCacher;
import se.dolkow.imagefiltering.gui.ImageDisplay;

/**
 * @author snild
 *
 */
public class ImagePreviewAccessory extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private final ImageInput loader;
	private final ShrinkFilter shrink;
	private final ThreadedCacher cacher;
	
	public ImagePreviewAccessory(JFileChooser jfc) {
		setDoubleBuffered(true);
		setPreferredSize(new Dimension(200,-1));
		
		setLayout(new BorderLayout());
		
		loader = new ImageInput();
		shrink = new ShrinkFilter(loader, 200, 200);
		cacher = new ThreadedCacher(shrink);
		
		ImageDisplay disp = new ImageDisplay(cacher, false, false);
		disp.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				shrink.setMaxDimensions(e.getComponent().getWidth(), e.getComponent().getHeight());
			}
			
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
		});
		add(disp, BorderLayout.CENTER);
		
		jfc.addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, this);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		File f = (File)evt.getNewValue();
		
		if (f == null) {
			loader.setPath(null);
		} else {
			loader.setPath(f.getAbsolutePath());
		}
	}
	
	
}
