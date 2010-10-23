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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import se.dolkow.imagefiltering.ImageProducer;
import se.dolkow.imagefiltering.gui.ImageDisplay;

public class InteractiveImageDisplay extends ImageDisplay {

	
	private static final long serialVersionUID = 1L;
	protected InteractiveImageDisplayListener listener = null;
	private final boolean enableRectangle;
	
	private int aspect1 = 1;
	private int aspect2 = 1;
	
	private final NormalInputHandler ih_normal = new NormalInputHandler();
	private final MoveInputHandler ih_move = new MoveInputHandler();
	
	protected int x1, x2, y1, y2;
	
	public InteractiveImageDisplay(ImageProducer source) {
		this(source, true);
		setEnableMagnify(false);
	}

	public InteractiveImageDisplay(ImageProducer source, boolean enableRectangle) {
		super(source, true);
		this.enableRectangle = enableRectangle;
		x1=y1=-100;
		x2=y2=1000000;
		MouseHandler mh = new MouseHandler();
		addMouseListener(mh);
		addMouseMotionListener(mh);
	}

	public void setInteractionListener(InteractiveImageDisplayListener l) {
		this.listener = l;
	}
	
	public void setRectangle(int x1, int y1, int x2, int y2) {
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
		
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		if (enableRectangle) {
			repaint();
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (enableRectangle) {
			g.setColor(Color.CYAN);
			g.drawRect(lastX+x1, lastY+y1, x2-x1, y2-y1);
		}
	}
	
	private abstract class InputHandler {
		protected int ix1, iy1, ix2, iy2;
		protected int initxpos, initypos;
		
		public void init(int ix1, int iy1, int ix2, int iy2, int xpos, int ypos) {
			this.ix1 = ix1;
			this.iy1 = iy1;
			this.ix2 = ix2;
			this.iy2 = iy2;
			this.initxpos = xpos;
			this.initypos = ypos;
		}
		public abstract void mouseDragged(int x, int y, MouseEvent e);
		public abstract void mousePressed(int x, int y, MouseEvent e);
		public final void mouseReleased(int x, int y, MouseEvent e) {
			mouseDragged(x, y, e);
			
		}
	}
	
	private class MoveInputHandler extends InputHandler {
		public void mouseDragged(int x, int y, MouseEvent e) {
			int xdiff = x - initxpos;
			int ydiff = y - initypos;
			setRectangle(ix1+xdiff, iy1+ydiff, ix2+xdiff, iy2+ydiff);
		}

		public void mousePressed(int x, int y, MouseEvent e) {
			
		}
	}
	
	private class NormalInputHandler extends InputHandler {

		public void mouseDragged(int x, int y, MouseEvent e) {
			if (e.isShiftDown()) {
				synchronized(InteractiveImageDisplay.this) {
					int w = x - initxpos;
					int h = y - initypos;
					
					
					if (Math.abs(w) >= Math.abs(h)) {
						int aw = Math.max(aspect1, aspect2);
						int ah = Math.min(aspect1, aspect2);
						y = (int)Math.round(initypos + w*Math.signum(w)*Math.signum(h) * ah / (double)aw);
					} else {
						int ah = Math.max(aspect1, aspect2);
						int aw = Math.min(aspect1, aspect2);
						x = (int)Math.round(initxpos + h*Math.signum(w)*Math.signum(h) * aw / (double)ah);
						//x = initxpos + h*(int)(Math.signum(w)*Math.signum(h));
					}
				}
			}
			setRectangle(initxpos, initypos, x, y);
		}

		public void init(int ix1, int iy1, int ix2, int iy2, int xpos, int ypos) {
			super.init(ix1, iy1, ix2, iy2, ix1, iy1);
		}
		
		public void mousePressed(int x, int y, MouseEvent e) {
			initxpos = x;
			initypos = y;
			setRectangle(x, y, x, y);
		}
	}

	private class MouseHandler extends MouseAdapter implements MouseMotionListener {
		
		private InputHandler lastIH = null;
		
		public void mouseClicked(MouseEvent e) {
			if (listener != null) {
				listener.click(e.getX()-lastX, e.getY()-lastY);
			}
		}
		
		private InputHandler _chooseIH(MouseEvent e) {
			if (e.isAltDown() || e.isControlDown() || e.isMetaDown()) {
				return ih_move;
			}
			return ih_normal;
		}
		
		private InputHandler chooseIH(MouseEvent e) {
			InputHandler ih = _chooseIH(e);
			if (lastIH != ih) {
				int xpos = e.getX()-lastX;
				int ypos = e.getY()-lastY;
				ih.init(x1, y1, x2, y2, xpos, ypos);
				lastIH = ih;
			}
			return ih;
		}

		public void mouseDragged(MouseEvent e) {
			chooseIH(e).mouseDragged(e.getX()-lastX, e.getY()-lastY, e);
			if (listener != null) {
				listener.draw(x1, y1, x2, y2);
			}
		}
		
		public void mousePressed(MouseEvent e) {
			int xpos = e.getX() - lastX;
			int ypos = e.getY() - lastY;
			InputHandler ih = chooseIH(e);
			ih.mousePressed(xpos, ypos, e);
			if (listener != null) {
				listener.draw(x1, y1, x2, y2);
			}
		}
		
		public void mouseReleased(MouseEvent e) {
			InputHandler ih = chooseIH(e);
			ih.mouseReleased(e.getX()-lastX, e.getY()-lastY, e);
			if (listener != null) {
				listener.draw(x1, y1, x2, y2);
			}
			lastIH = null;
		}

		public void mouseMoved(MouseEvent e) {
		}
	}

	public synchronized void setAspect1(int a1) {
		this.aspect1 = a1;
	}
	public synchronized void setAspect2(int a2) {
		this.aspect2 = a2;
	}
	
	

}
