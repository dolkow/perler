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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import se.dolkow.imagefiltering.internationalization.Messages;


public class ThreadedCacher extends UnthreadedCacher {
	
	protected static final PrepareThreadData ptd;
	protected static final Collection<PrepareThread> pts;
	
	static {
		ptd = new PrepareThreadData();
		pts = new LinkedList<PrepareThread>();
		for (int i=0; i<10; i++) {
			PrepareThread pt = new PrepareThread(i);
			pts.add(pt);
		}
	}
	
	private ImageException imgexc = null;
	private ImageException memexc = null;
	
	public ThreadedCacher(ImageProducer source) {
		super(source);
		ptd.enqueue(this, changeNo);
	}
	

	public BufferedImage getImage() throws ImageException {
		synchronized(this) {
			if (memexc != null) {
				throw memexc;
			} 
			if (changeNo == cachedNo) {
				if (imgexc != null) {
					throw imgexc;
				}
				if (cached != null) {
					return cached;
				}
			}
		}
		throw new CacheEmptyException(Messages.getFormatted("ThreadedCacher.empty_cacher", source)); //$NON-NLS-1$
	}
	
	public void changed(ImageProducer producer) {	
		synchronized(this) {
			imgexc = null;
			changeNo++;
			notifyAll();
			ptd.enqueue(this, changeNo);
		}
		notifyListeners();
	}

	private static class Task {
		final ThreadedCacher tc;
		final long changeNo;

		public Task(ThreadedCacher tc, long changeNo) {
			this.tc = tc;
			this.changeNo = changeNo;
		}
	}

	private static class PrepareThreadData {
		private Map<ThreadedCacher, Task> queued = new HashMap<ThreadedCacher, Task>();
		
		public synchronized void enqueue(ThreadedCacher tc, long changeNo) {
			queued.put(tc, new Task(tc, changeNo));
			notifyAll();
		}
		
		public synchronized Task next() throws InterruptedException {
			while (queued.isEmpty()) {
				wait();
			}
			Iterator<Task> it = queued.values().iterator();
			Task tsk = it.next();
			it.remove();
			
			return tsk;
		}
	}

	private static class PrepareThread extends Thread {
		
		public PrepareThread(int id) {
			super("ThreadedCacher:PrepareThread" + id); //$NON-NLS-1$
			setDaemon(true);
			start();
		}

		public void run() {
			Task tsk = null;
			try {
				while(true) {
					try {
						tsk = ptd.next();
						ThreadedCacher tc = tsk.tc;
						try {
							BufferedImage img = tc.source.getImage();
							synchronized (tc) {
								if (tc.changeNo == tsk.changeNo) {
									tc.cached = img;
									tc.cachedNo = tsk.changeNo;
								}
							}
						} catch (CacheEmptyException e) {
							synchronized (tc) {
								if (tc.changeNo == tsk.changeNo) {
									tc.imgexc = e;
									tc.cachedNo = tsk.changeNo;
								}
							}
						} catch (ImageException e) {
							synchronized (tc) {
								if (tc.changeNo == tsk.changeNo) {
									tc.cached = null;
									tc.imgexc = e;
									tc.cachedNo = tsk.changeNo;
								}
							}
						}
						tc.notifyListeners();
						tsk = null;
					} catch (OutOfMemoryError e) {
						String msg = getName() + " encountered an OutOfMemoryError"; //$NON-NLS-1$
						if (tsk != null) {
							msg += " while getting an image from " + tsk.tc.source; //$NON-NLS-1$
							synchronized(tsk.tc) {
								tsk.tc.memexc = new AllocationException(msg, e);
							}
						}
						System.err.println(msg);
					} catch (StackOverflowError e) {
						String msg = getName() + " encountered a StackOverflowError"; //$NON-NLS-1$
						if (tsk != null) {
							msg += " while getting an image from " + tsk.tc.source; //$NON-NLS-1$
							synchronized(tsk.tc) {
								tsk.tc.memexc = new ImageException(msg, e);
							}
						}
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e) {
				//just exit
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw e;
			} catch (Error e) {
				e.printStackTrace();
				throw e;
			}
			System.err.println(getName() + " exiting..."); //$NON-NLS-1$
		}
	}
	
	public void cleanup() {
		super.cleanup();
	}
}
