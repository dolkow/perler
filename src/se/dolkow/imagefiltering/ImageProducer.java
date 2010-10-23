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

import java.awt.image.BufferedImage;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Node;

public interface ImageProducer {
	
	/**
	 * Returns the produced image. Blocks until the image is available. The 
	 * BufferedImage should be of TYPE_INT_RGB.
	 * @return a BufferedImage.
	 * @throws ImageException 
	 */
	public BufferedImage getImage() throws ImageException;
	
	/**
	 * Should we allow this producer's image to be magnified on display?
	 * @return
	 */
	public boolean allowMagnification();
	
	public void addChangeListener(ImageProducerListener listener);
	public void removeChangeListener(ImageProducerListener listener);
	
	/**
	 * Should clean up its threads and resources (if this is a filter,
	 * this includes recursively calling cleanup on its source). 
	 */
	public void cleanup();

	public void saveToTree(Element parent);
	public void loadFromTree(Node n) throws TreeParseException;

	/**
	 * Try to create an image by using the closest, cached image as a base.
	 * @return the created image, or null if none.
	 */
	public BufferedImage getLastWorkingImage() throws ImageException;

	/**
	 * @return a short description of this producer and what it does.
	 */
	public String getDescription();

	/**
	 * @return a long description of this producer and what it does.
	 */
	public String getLongDescription();
}
