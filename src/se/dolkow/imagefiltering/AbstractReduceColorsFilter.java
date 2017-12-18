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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Element;
import se.dolkow.imagefiltering.tree.Leaf;
import se.dolkow.imagefiltering.tree.Node;

public abstract class AbstractReduceColorsFilter extends AbstractPixelModifier {

	private static final int mult = 44;
	
	protected Map<Integer, String> colors = new HashMap<Integer, String>(); //rgb -> name
	protected Map<Integer, MutableInteger> usage = new HashMap<Integer, MutableInteger>(); //rgb -> use count
	
	protected final Map<Character, Image> alphanum = new TreeMap<Character, Image>(); //char -> charimage
	
	public AbstractReduceColorsFilter(String name, String xmlTagName, ImageProducer source) throws InterruptedException, ImageException {
		super(name, xmlTagName, source, Messages.get("AbstractReduceColorsFilter.description")); //$NON-NLS-1$
		
		char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z'};
		ImageLoadWaiter ilw = new ImageLoadWaiter();
		for (char c : chars) {
			String path = "/resource/alphanum/" + c + ".png"; //$NON-NLS-1$ //$NON-NLS-2$
			URL url = AbstractReduceColorsFilter.class.getResource(path);
			if (url == null) {
				throw new ImageException(Messages.get("AbstractReduceColorsFilter.missing_resource") + " " + path);  //$NON-NLS-1$//$NON-NLS-2$
			}
			Image img = Toolkit.getDefaultToolkit().createImage(url);
			alphanum.put(c, img);
			ilw.addImage(img);
		}
		ilw.waitForAll();
	}
	
	protected final int[] modifyPixels(int[] pixels, int w, int h) throws NoImageReadyException {
		synchronized (colors) {
			usage = new HashMap<Integer, MutableInteger>();
			if (colors.isEmpty()) {
				throw new NoImageReadyException(getEmptyPaletteString());
			}
			return reduceColors(pixels, w, h);
		}
	}
	
	protected abstract String getEmptyPaletteString();
	
	
	public BufferedImage[][] getBeadSheets(int tileWidth, int tileHeight) throws ImageException {
		try {
			synchronized(colors) {
				BufferedImage simg = getImage();
				int w = simg.getWidth();
				int h = simg.getHeight();
				
				int[] sourcepixels = new int[w*h];
				simg.getRGB(0, 0, w, h, sourcepixels, 0, w);
				
				int horizparts = w / tileWidth;
				int vertparts = h / tileHeight;
				if (w % tileWidth != 0) {
					horizparts += 1;
				}
				if (h % tileHeight != 0) {
					vertparts += 1;
				}
				
				BufferedImage[][] res = new BufferedImage[horizparts][vertparts];
				
				for (int hpart=0; hpart < horizparts; hpart++) {
					for (int vpart=0; vpart < vertparts; vpart++) {
						res[hpart][vpart] = createBeadSheet(sourcepixels,
								hpart*tileWidth, vpart*tileHeight,
								Math.min((hpart+1)*tileWidth, w),
								Math.min((vpart+1)*tileHeight, h),
								w
							);
					}
				}
				
				return res;
			}
		} catch (OutOfMemoryError e) {
			throw new AllocationException(e);
		}
	}
	
	public BufferedImage getBeadSheet() throws ImageException {
		return getBeadSheets(Integer.MAX_VALUE,Integer.MAX_VALUE)[0][0];
	}
	
	protected BufferedImage createBeadSheet(int[] pixels, int xstart, int ystart, int xend, int yend, int stride) throws AllocationException {
		try {
			synchronized(colors) {
				final int xdiff = xend-xstart;
				final int ydiff = yend-ystart;
				BufferedImage sheet = allocateImage(xdiff*mult+1, ydiff*mult+1);
				
				final Graphics2D g = sheet.createGraphics();
				g.setBackground(Color.WHITE);
				g.clearRect(0, 0, sheet.getWidth(), sheet.getHeight());
				
				int i=0;
				for (int iiy=0; iiy<ydiff; iiy++) {
					int iy = iiy * mult;
					i = stride*(ystart+iiy) + xstart;
					final int y = iy + 2;
					for (int ix=0; ix<xdiff*mult; ix+=mult) {
						final int color = pixels[i] & 0xFFFFFF;
						String name = colors.get(color);
						if (name == null) {
							name = "???"; //$NON-NLS-1$
						}
						
						final char[] chars = name.substring(0, 3).trim().toLowerCase().toCharArray();
						int x = ix + 2 + (3-chars.length) * 2;
						for (char c : chars) {
							Image img = alphanum.get(c);
							if (img == null) {
								g.setColor(Color.RED);
								g.fillRect(x, y, 3, 20);
							} else {
								g.drawImage(img, x, y, null);
							}
							g.setColor(new Color(color));
							g.fillRect(ix+3, iy+22, 39, 20);
							
							x+=14;
						}
						
						i++;
					}
				}
				
				drawGrid(g, sheet.getWidth(), sheet.getHeight(), mult, mult, Color.GRAY);
				
				return sheet;
			}
		} catch (OutOfMemoryError e) {
			throw new AllocationException(e);
		}
	}
	
	public Map<Integer, MutableInteger> getColorUsage() {
		synchronized (colors) {
			return usage;
		}
	}
	
	/**
	 * Reduce the colors of an image. The result should only use colors from the
	 * <pre>colors</pre> list. Using the pixels array as the returned data array
	 * is allowed. 
	 * @param pixels the source data. Modification <i>is</i> allowed.
	 * @param w width of the pixel arrays.
	 * @param h height of the pixel arrays.
	 * @return a pixel array with the result data, of the same dimensions as the input array.
	 */
	protected abstract int[] reduceColors(int[] pixels, int w, int h);

	protected int closest(int color) {
		synchronized (colors) {
			int r = r(color);
			int g = g(color);
			int b = b(color);
			
			return closest(r, g, b);
		}
	}
	
	protected int closest(float r, float g, float b) {
		synchronized (colors) {
			int closest = 0xFF00FF;
			float closestDiff = Float.POSITIVE_INFINITY;
			
			for (int c : colors.keySet()) {
				float dr = r - r(c);
				float dg = g - g(c);
				float db = b - b(c);
				float diff = dr*dr + dg*dg + db*db;
				if (diff < closestDiff) {
					closestDiff = diff;
					closest = c;
				}
			}
			
			MutableInteger count = usage.get(closest);
			if (count != null) {
				count.val++;
			} else {
				usage.put(closest, new MutableInteger(1));
			}
			
			return closest; 
		}
	}

	public final Map<Integer, String> getColors() {
		return colors;
	}
	
	public final void addColor(int rgb, boolean moreChangesComing) {
		addColor(rgb, null, moreChangesComing);
	}
	
	public final void addColor(int rgb, String name, boolean moreChangesComing) {
		if (name == null) {
			name = Integer.toHexString(rgb);
			while (name.length() < 6) {
				name = "0" + name; //$NON-NLS-1$
			}
			name = "#" + name; //$NON-NLS-1$
		}
		synchronized (colors) {
			colors.put(rgb, name);
		}
		if (!moreChangesComing) {
			notifyChangeListeners();
		}
	}
	
	public final void removeColor(int rgb, boolean moreChangesComing) {
		synchronized (colors) {
			colors.remove(rgb);
		}
		if (!moreChangesComing) {
			notifyChangeListeners();
		}
	}
	
	public final void doneChanging() {
		notifyChangeListeners();
	}
	
	public final void clearColors() {
		synchronized (colors) {
			colors.clear();
		}
		notifyChangeListeners();
	}

	protected void loadAttributeFromTree(Node n) throws TreeParseException {
		if ("palette".equals(n.getName())) { //$NON-NLS-1$
			List<Entry<Integer,String>> cs = PaletteLoader.loadPalette(n);
			for (Entry<Integer,String> e : cs) {
				addColor(e.getKey(), e.getValue(), true);
			}
			doneChanging();
		} else {
			super.loadAttributeFromTree(n);
		}
	}
	
	protected void saveAttributesToTree(Element parent) {
		super.saveAttributesToTree(parent);
		Element palette = new Element("palette"); //$NON-NLS-1$
		parent.add(palette);
		for (Map.Entry<Integer,String> e : colors.entrySet()) {
			int clr = e.getKey();

			Element color = new Element("color"); //$NON-NLS-1$
			color.add(new Leaf("r", ""+r(clr))); //$NON-NLS-1$ //$NON-NLS-2$
			color.add(new Leaf("g", ""+g(clr))); //$NON-NLS-1$ //$NON-NLS-2$
			color.add(new Leaf("b", ""+b(clr))); //$NON-NLS-1$ //$NON-NLS-2$
			color.add(new Leaf("name", e.getValue())); //$NON-NLS-1$
			palette.add(color);
		}
	}

	public boolean hasColor(int rgb) {
		synchronized(colors) {
			return colors.containsKey(rgb);
		}
	}
	
	public boolean shouldCallModifyPixels() {
		return true;
	}
}
