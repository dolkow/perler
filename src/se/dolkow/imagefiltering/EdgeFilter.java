package se.dolkow.imagefiltering;

import java.util.HashSet;
import java.util.Random;

import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;


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



public class EdgeFilter extends AbstractPixelModifier {

	private static final int[] KERNEL = {3, 10, 3};
	
	private int edgeTreshold;
	private int low, high;
	
	private FillMode mode;
	private boolean showEdges;

	private int fillParam;
	
	public EdgeFilter(ImageProducer source) {
		super(Messages.get("EdgeFilter.name"), "edge", source, Messages.get("EdgeFilter.short_description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		edgeTreshold = 120;
		low = 250;
		high = 800;
		mode = FillMode.Flat;
		showEdges = false;
		fillParam = 48;
	}
	
	
	protected int[] modifyPixels(int[] pixels, int w, int h) {
		if (w == 0 || h == 0) return pixels;
		
		
		double[] deriv = new double[pixels.length];
		sobel(pixels, deriv, w, h);
		
		color(pixels, deriv, w, h);
		
		return pixels;
	}
	
	private class Field {
		int numPixels = 0;
		long lr = 0;
		long lg = 0;
		long lb = 0;
		
		int r = 0;
		int g = 0;
		int b = 0;
		
		boolean colorUpToDate = false;
		
		final HashSet<Integer> groups;
		
		public Field(int initialGroup, int initColor) {
			groups = new HashSet<Integer>();
			groups.add(initialGroup);
			lr = r = r(initColor);
			lg = g = g(initColor);
			lb = b = b(initColor);
			numPixels = 1;
			
			colorUpToDate = false;
		}
		
		public void add(Field other) {
			groups.addAll(other.groups);
			numPixels = numPixels + other.numPixels;
			lr = lr + other.lr;
			lg = lg + other.lg;
			lb = lb + other.lb;
			colorUpToDate = false;
		}
		
		public void calculateColor(Random rnd) {			
			if (mode == FillMode.None) {
				r = g = b = 255;
				return;
			}
			
			if (mode == FillMode.Random) {
				r = 64 + rnd.nextInt(192);
				g = 64 + rnd.nextInt(192);
				b = 64 + rnd.nextInt(192);
				return;
			}
			
			if (colorUpToDate) return;
			
			
			if (numPixels > 0) {
				r = (int)Math.round(this.lr / (double)numPixels);
				g = (int)Math.round(this.lg / (double)numPixels);
				b = (int)Math.round(this.lb / (double)numPixels);
			} else {
				r = g = b = 0;
			}
			
			
			if (mode == FillMode.Gray || mode == FillMode.White) {
				//black/white!
				int intens = (int)(0.3f*r + 0.59f*g + 0.11f*b);
				
				if (mode == FillMode.Gray) {
					if (intens >= fillParam*2) intens = 255;
					else if (intens >= fillParam) intens = 192;
					else if (intens >= fillParam/2) intens = 128;
					else if (intens >= fillParam/4) intens = 64;
					else intens = 0;
				} else {
					if (intens >= fillParam) intens = 255;
					else intens = 0;
				}
				
				r = g = b = intens;
			}
			
			colorUpToDate = true;
		}

		public void addPixel(int color) {
			colorUpToDate = false;
			
			numPixels++;
			if (mode == FillMode.Random 
					|| mode == FillMode.None
					|| mode == FillMode.Passthrough) {
				return;
			}
					
			lr = lr + r(color);
			lg = lg + g(color);
			lb = lb + b(color);
		}

	}
	
	private void color(int[] pixels, double[] sobel, int w, int h) {
		Random rnd = new Random(fillParam);
		int nextg = 0;
		int[] group = new int[pixels.length];
		Field[] field = new Field[pixels.length]; //length is for worst-case number of groups
		int i=0;
		
		if (mode != FillMode.Passthrough) {
			//nothing calculated here is used for passthrough. :)
			//create group for first pixel
			field[nextg] = new Field(nextg, pixels[0]);
			group[0] = nextg++;
			
			//init first row
			for (i=1; i<w; i++) {
				if (sobel[i] >= edgeTreshold || sobel[i-1] < edgeTreshold) {
					group[i] = group[i-1]; 
					field[group[i]].addPixel(pixels[i]);
				} else {
					field[nextg] = new Field(nextg, pixels[i]);
					group[i] = nextg++;
				}
			}
			
			//init first column
			for (i=w; i<h*w; i+=w) {
				if (sobel[i] >= edgeTreshold || sobel[i-w] < edgeTreshold) {
					group[i] = group[i-w]; 
					field[group[i]].addPixel(pixels[i]);
				} else {
					field[nextg] = new Field(nextg, pixels[i]);
					group[i] = nextg++;
				}
			}
			
			i = w; //skip first row
			for (int y=1; y<h; y++) {
				i++; //skip first column
				for(int x=1; x<w; x++) {
					if (sobel[i] < edgeTreshold) {				
						int up = i-w;
						int left = i-1;
						boolean joinLeft = (sobel[left] < edgeTreshold);
						boolean joinUp = (sobel[up] < edgeTreshold);
						
						if (joinLeft) {
							group[i] = group[left];
							field[group[i]].addPixel(pixels[i]);
							if (joinUp) {
								Field gc1 = field[group[up]];
								Field gc2 = field[group[left]];
								if (gc1 != gc2) {
									for(Integer integer : gc2.groups) {
										field[integer] = gc1;
									}
									gc1.add(gc2);
								}
							}
						} else if (joinUp) {
							group[i] = group[up];
							field[group[i]].addPixel(pixels[i]);
						} else {
							field[nextg] = new Field(nextg, pixels[i]);
							group[i] = nextg++;
						}
					}
					i++;
				}
			}
			
			//fix edge pixels' groups
			i = w; //skip first row
			for (int y=1; y<h; y++) {
				i++; //skip first column
				for(int x=1; x<w; x++) {
					if (sobel[i] >= edgeTreshold) {
						//find kind-of-closest non-edge pixel:
						int r = 1;
						int j = -1;
						
						while (j < 0 && r < 10) {
							for(int k=-r; k<=r; k++) {
								int l;
								
								if (k <= x) {
									l = i - r*w + k;
									if (l >= 0 && l < pixels.length && sobel[l] < edgeTreshold) {
										j = l;
										break;
									}
									
									l = i + r*w + k;
									if (l >= 0 && l < pixels.length && sobel[l] < edgeTreshold) {
										j = l;
										break;
									}
								}
								
								if (r <= x) {
									l = i + k*w - r;
									if (l >= 0 && l < pixels.length && sobel[l] < edgeTreshold) {
										j = l;
										break;
									}
									
									l = i + k*w + r;
									if (l >= 0 && l < pixels.length && sobel[l] < edgeTreshold) {
										j = l;
										break;
									}
								}
							}
							r++;
						}
					
						if (j < 0) {
							//r became too large, had to stop for efficiency.
							j = i-1;
						}
						
						group[i] = group[j];
						field[group[i]].addPixel(pixels[i]);
					}
					i++;
				}
			}
			
			i=0;
			while(i < field.length && field[i] != null) {
				field[i].calculateColor(rnd);
				i++;
			}
		}
		
		for (i=0; i<pixels.length; i++) {
			
			if (showEdges && sobel[i] >= edgeTreshold) {
				pixels[i] = 0xff00ff;
				continue;
			}
			
			int r,g,b;
			
			if (mode == FillMode.Passthrough) {
				r = r(pixels[i]);
				g = g(pixels[i]);
				b = b(pixels[i]);
			} else {
				r = field[group[i]].r;
				g = field[group[i]].g;
				b = field[group[i]].b;
			}
			
			double v = (sobel[i] - low) / (high-low);
			if (high == low) v = 0;
			else if (v < 0d) v = 0;
			else if (v > 1d) v = 1;
			
			v = 1d - v;
			
			r = (int)(v*r);
			g = (int)(v*g);
			b = (int)(v*b);
			
			pixels[i] = (r<<16) + (g<<8) + b;
		}
	}
	
	private void sobel(int[] in, double[] deriv, int w, int h) {
		int nw = w+2;
		int nh = h+2;
		int[][] dx = new int[nh][nw];
		int[][] dy = new int[nh][nw];
		
		
		int i = 0;
		for (int y=1; y<nh-1; y++) {
			for(int x=1; x<nw-1; x++) {
				
				int v = (int)(0.3f*r(in[i]) + 0.59f*g(in[i]) + 0.11f*b(in[i]));
				
				//  1  0 -1
				//  2  0 -2
				//  1  0 -1
				dx[y-1][x-1]	-= KERNEL[0] * v;
				dx[y  ][x-1]	-= KERNEL[1] * v;
				dx[y+1][x-1]	-= KERNEL[2] * v;
				
				dx[y-1][x+1]	+= KERNEL[0] * v;
				dx[y  ][x+1]	+= KERNEL[1] * v;
				dx[y+1][x+1]	+= KERNEL[2] * v;
				
				//  1  2  1
				//  0  0  0
				// -1 -2 -1
				dy[y-1][x-1]	-= KERNEL[0] * v;
				dy[y-1][x  ]	-= KERNEL[1] * v;
				dy[y-1][x+1]	-= KERNEL[2] * v;
				
				dy[y+1][x-1]	+= KERNEL[0] * v;
				dy[y+1][x  ]	+= KERNEL[1] * v;
				dy[y+1][x+1]	+= KERNEL[2] * v;
				
				i++; //next pixel
			}
		}
		
		//fix edges
		for(int x=1; x<nw-1; x++) {
			dx[1][x] = dx[2][x];
			dy[1][x] = dy[2][x];
			
			dx[h][x] = dx[h-1][x];
			dy[h][x] = dy[h-1][x];
		}
		
		for(int y=1; y<nh-1; y++) {
			dx[y][1] = dx[y][2];
			dy[y][1] = dy[y][2];
			
			dx[y][w] = dx[y][w-1];
			dy[y][w] = dy[y][w-1];
		}
		
		i=0;
		for (int y=1; y<nh-1; y++) {
			for(int x=1; x<nw-1; x++) {
				deriv[i] = Math.sqrt(dx[y][x]*dx[y][x] + dy[y][x]*dy[y][x]);
				i++; //next pixel
			}
		}
	}



	protected boolean shouldCallModifyPixels() {
		return true;
	}
	
	public static int getMaxTreshold() {
		int s = 256*(KERNEL[0] + KERNEL[1] + KERNEL[2]);
		return (int)Math.round(Math.sqrt(s*s + s*s));
	}
	
	
	public synchronized int getEdgeTreshold() {
		return edgeTreshold;
	}
	
	public synchronized int getHighTreshold() {
		return high;
	}
	
	public synchronized int getLowTreshold() {
		return low;
	}
	
	public void setEdgeTreshold(int tres) {
		synchronized (this) {
			edgeTreshold = tres;
		}
		notifyChangeListeners();
	}
	
	public void setLowTreshold(int tres) {
		synchronized (this) {
			low = tres;
		}
		notifyChangeListeners();
	}
	
	public void setHighTreshold(int tres) {
		synchronized (this) {
			high = tres;
		}
		notifyChangeListeners();
	}
	
	public enum FillMode {
		None,
		White,
		Gray,
		Flat,
		Random,
		Passthrough
	}

	public synchronized FillMode getFillMode() {
		return mode;
	}
	
	public void setFillMode(FillMode mode) {
		synchronized (this) {
			this.mode = mode;
		}
		notifyChangeListeners();
	}
	
	public synchronized boolean getShowEdges() {
		return showEdges;
	}
	
	public void setShowEdges(boolean show) {
		synchronized (this) {
			showEdges = show;
		}
		notifyChangeListeners();
	}


	public synchronized int getFillParam() {
		return fillParam;
	}
	
	public void setFillParam(int value) {
		synchronized(this) {
			this.fillParam = value;
		}
		notifyChangeListeners();
	}


	protected void loadAttributeFromTree(Node n) throws TreeParseException {
		if (n.getName().equals("showedges")) { //$NON-NLS-1$
			String value = n.getTextContents();
			setShowEdges("1".equals(value) || "true".equals(value)); //$NON-NLS-1$ //$NON-NLS-2$
		} else if (n.getName().equals("treshold")) { //$NON-NLS-1$
			setEdgeTreshold(Integer.parseInt(n.getTextContents()));
		} else if (n.getName().equals("low")) { //$NON-NLS-1$
			setLowTreshold(Integer.parseInt(n.getTextContents()));
		} else if (n.getName().equals("high")) { //$NON-NLS-1$
			setHighTreshold(Integer.parseInt(n.getTextContents()));
		} else if (n.getName().equals("fillparam")) { //$NON-NLS-1$
			setFillParam(Integer.parseInt(n.getTextContents()));
		} else if (n.getName().equals("fillmode")) { //$NON-NLS-1$
			String s = n.getTextContents().toLowerCase();
			if (s.equals("none")) setFillMode(FillMode.None); //$NON-NLS-1$
			else if (s.equals("white")      ) setFillMode(FillMode.White); //$NON-NLS-1$
			else if (s.equals("gray")       ) setFillMode(FillMode.Gray); //$NON-NLS-1$
			else if (s.equals("flat")       ) setFillMode(FillMode.Flat); //$NON-NLS-1$
			else if (s.equals("random")     ) setFillMode(FillMode.Random); //$NON-NLS-1$
			else if (s.equals("passthrough")) setFillMode(FillMode.Passthrough); //$NON-NLS-1$
			else throw new TreeParseException(Messages.getFormatted("EdgeFilter.unknown_fill_mode", new Object[]{s, getClass().getSimpleName()})); //$NON-NLS-1$
			
		} else {
			super.loadAttributeFromTree(n);
		}
	}


	protected void saveAttributesToTree(Element parent) {
		super.saveAttributesToTree(parent);
		parent.add(new Leaf("fillmode", mode.toString().toLowerCase())); //$NON-NLS-1$
		parent.add(new Leaf("fillparam", "" + fillParam)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("treshold", "" + edgeTreshold)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("low", "" + low)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("high", "" + high)); //$NON-NLS-1$ //$NON-NLS-2$
		parent.add(new Leaf("showedges", "" + showEdges)); //$NON-NLS-1$ //$NON-NLS-2$
	}


	public String getLongDescription() {
		return Messages.get("EdgeFilter.long_description"); //$NON-NLS-1$
	}
	
	
	
}
