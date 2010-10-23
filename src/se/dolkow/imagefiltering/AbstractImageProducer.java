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
import java.util.Collection;
import java.util.LinkedList;

import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Node;

public abstract class AbstractImageProducer implements ImageProducer {

	private final String xmlTagName;
	private Collection<ImageProducerListener> listeners = new LinkedList<ImageProducerListener>();
	
	
	public AbstractImageProducer(String xmlTagName) {
		this.xmlTagName = xmlTagName;
	}
	
	public BufferedImage getLastWorkingImage() throws ImageException {
		return null;
	}
	
	public final void addChangeListener(ImageProducerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	public final void removeChangeListener(ImageProducerListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	
	protected final void notifyChangeListeners(boolean changed) {
		if (changed) {
			notifyChangeListeners();
		}
	}
	
	protected final void notifyChangeListeners() {
		synchronized (listeners) {
			ImageProducerListener[] lstnrs = listeners.toArray(new ImageProducerListener[listeners.size()]);
			for (ImageProducerListener lstnr : lstnrs) {
				lstnr.changed(this);
			}
		}
	}
	
	/**
	 * Set an attribute while loading from DOM. Overriding classes should
	 * always call super.loadAttributeFromXML if and only if they don't 
	 * handle that particular node. 
	 * @param node
	 */
	protected void loadAttributeFromTree(Node attributeNode) throws TreeParseException {
		Object[] args = new Object[]{attributeNode.getName(), attributeNode.getTextContents(), getClass().getSimpleName()};
		throw new TreeParseException(Messages.getFormatted("AbstractImageProducer.unhandled_setting", args)); //$NON-NLS-1$
	}
	
	/**
	 * This method is <i>NOT</i> thread safe, and may introduce race conditions
	 * and/or deadlocks for other threads using this filter (or a filter in the
	 * same chain). Use this method immediately after creating the object, before
	 * adding any listeners to it.
	 */
	public final void loadFromTree(Node element) throws TreeParseException {
		if (!xmlTagName.equals(element.getName())) {
			Object[] args = new Object[]{xmlTagName, element.getName()};
			throw new TreeParseException(Messages.getFormatted("AbstractImageProducer.unexpected_tag", args)); //$NON-NLS-1$
		}
		for (Node child : element) {
			loadAttributeFromTree(child);
		}
	}
	
	public synchronized final void saveToTree(Element parent) {
		Element producer = new Element(xmlTagName);
		saveAttributesToTree(producer);
		parent.add(producer);
	}
	
	/**
	 * Recommended instead of creating new BufferedImages yourself. Catches 
	 * OutOfMemoryError.
	 * @param w
	 * @param h
	 * @return the allocated BufferedImage
	 * @throws AllocationException if an OutOfMemoryError occurs.
	 */
	protected static BufferedImage allocateImage(int w, int h) throws AllocationException {
		try {
			return new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		} catch (OutOfMemoryError e) {
			throw new AllocationException(Messages.get("AbstractImageProducer.outofmemory_large_image"), e); //$NON-NLS-1$
		}
	}
	
	/**
	 * Add settings elements to parent.
	 * Classes overriding this method should call super.saveAttributesToTree().
	 * @param parent the element to add settings to. 
	 */
	protected abstract void saveAttributesToTree(Element parent);
	
	public void cleanup() {
	}
	
	public boolean allowMagnification() {
		return true;
	}
}
