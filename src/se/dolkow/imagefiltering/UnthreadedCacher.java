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

public class UnthreadedCacher implements Cacher {
	private Collection<ImageProducerListener> listeners = new LinkedList<ImageProducerListener>();
	
	protected final ImageProducer source;
	protected BufferedImage cached = null;
	protected long cachedNo = -1;
	protected long changeNo = 0;
	
	public UnthreadedCacher(ImageProducer source) {
		source.addChangeListener(this);
		this.source = source;
	}
	
	public void addChangeListener(ImageProducerListener listener) {
		synchronized(listeners) {
			listeners.add(listener);
		}
	}
	
	public void removeChangeListener(ImageProducerListener listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}

	public synchronized BufferedImage getImage() throws ImageException {
		try {
			while (changeNo != cachedNo || cached == null) {
				cachedNo = changeNo;
				cached = source.getImage();
			}
			return cached;
		} catch (CacheEmptyException e) {
			throw e;
		} catch (ImageException e) {
			cached = null;
			throw e;
		}
	}

	public void changed(ImageProducer producer) {
		synchronized (this) {
			changeNo++;
		}
		notifyListeners();
	}
	
	protected void notifyListeners() {
		synchronized (listeners) {
			ImageProducerListener[] lstnrs = listeners.toArray(new ImageProducerListener[listeners.size()]);
			for (ImageProducerListener lstnr : lstnrs) {
				lstnr.changed(this);
			}
		}
	}
	
	public String toString() {
		return source + " (" + Messages.get("UnthreadedCacher.cached") + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public ImageProducer getSource() {
		return source;
	}

	public void cleanup() {
		source.cleanup();
	}

	public void loadFromTree(Node n) throws TreeParseException {
		for (Node setting : n) {
			System.out.println("WARNING: unhandled element in " + getClass().getSimpleName() + ".loadFromTree(): " + setting); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void saveToTree(Element parent) {
		source.saveToTree(parent);
		parent.add(new Element("cacher")); //$NON-NLS-1$
	}

	public synchronized BufferedImage getLastWorkingImage() throws ImageException {
		return cached;
	}

	public boolean allowMagnification() {
		return source.allowMagnification();
	}

	public String getDescription() {
		return source.getDescription();
	}

	public String getLongDescription() {
		return source.getLongDescription();
	}
}
