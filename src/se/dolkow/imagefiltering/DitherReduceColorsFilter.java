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




import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;

public class DitherReduceColorsFilter extends AbstractReduceColorsFilter {

	protected Change[] changes;
	protected float coeff;
	
	public DitherReduceColorsFilter(ImageProducer source) throws InterruptedException, ImageException {
		this(1f, source);
	}
	
	public DitherReduceColorsFilter(float coeff, ImageProducer source) throws InterruptedException, ImageException {
		super(Messages.get("DitherReduceColorsFilter.name"), "reduce", source); //$NON-NLS-1$ //$NON-NLS-2$
		changes = new Change[] {
				new Change(1, 0, 7f/16f),
				new Change(-1, 1, 3f/16f),
				new Change(0, 1, 5/16f),
				new Change(1, 1, 1/16f)
		};
		
		setDitherCoefficient(coeff);
	}

	public synchronized float getDitherCoefficient() {
		return coeff;
	}
	
	public void setDitherCoefficient(float coeff) {
		synchronized(this) {
			this.coeff = coeff;
		}
		notifyChangeListeners();
	}
	
	protected synchronized int[] reduceColors(int[] pixels, int w, int h) {
		//Floyd-Steinberg dithering
		
		float[] addr = new float[w*h];
		float[] addg = new float[w*h];
		float[] addb = new float[w*h];
		
		int i = 0;
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				float r = r(pixels[i]) + addr[i];
				float g = g(pixels[i]) + addg[i];
				float b = b(pixels[i]) + addb[i];
				int newc = closest(r,g,b);
				pixels[i] = newc;
				float errorr = r - r(newc);
				float errorg = g - g(newc);
				float errorb = b - b(newc);
				
				for (Change ch : changes) {
					int futureindex = i + ch.xdiff + w*ch.ydiff; 
					if (futureindex < pixels.length  && x+ch.xdiff < w && x+ch.xdiff >= 0) {
						addr[futureindex] += coeff*(ch.multiplier * errorr);
						addg[futureindex] += coeff*(ch.multiplier * errorg);
						addb[futureindex] += coeff*(ch.multiplier * errorb);
					}
				}
				
				i++;
			}
		}
		return pixels;
	}

	protected class Change {
		public int xdiff;
		public int ydiff;
		public float multiplier;
		
		public Change(int xdiff, int ydiff, float multi) {
			this.xdiff = xdiff;
			this.ydiff = ydiff;
			this.multiplier = multi;
		}
	}

	protected void loadAttributeFromTree(Node n) throws TreeParseException {
		if ("dither".equals(n.getName())) { //$NON-NLS-1$
			float v = Float.parseFloat(n.getTextContents());
			setDitherCoefficient(v);
		} else { 
			super.loadAttributeFromTree(n);
		}
	}
	
	protected void saveAttributesToTree(Element parent) {
		super.saveAttributesToTree(parent);
		parent.add(new Leaf("dither", ""+coeff)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected String getEmptyPaletteString() {
		return Messages.get("DitherReduceColorsFilter.empty_palette"); //$NON-NLS-1$
	}

	public String getLongDescription() {
		return Messages.get("DitherReduceColorsFilter.long_description");  //$NON-NLS-1$
	}
}
