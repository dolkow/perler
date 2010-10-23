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

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import se.dolkow.imagefiltering.tree.Node;

public class PaletteLoader {

	public static List<Entry<Integer, String>> loadPalette(Node tree) throws TreeParseException {
		if (!"palette".equals(tree.getName())) { //$NON-NLS-1$
			throw new TreeParseException("Supplied tree node isn't a palette node."); //$NON-NLS-1$
		}
		List<Entry<Integer, String>> l = new LinkedList<Entry<Integer, String>>();
		for (Node color : tree) {
			int rgb[] = new int[3];
			
			for (int i=0; i<3; i++) {
				rgb[i] = Integer.parseInt(color.getChild(i).getTextContents()); 
			}
			
			int rgbi = (rgb[0]<<16) + (rgb[1]<<8) + rgb[2];
			
			String name;
			if (color.getNumChildren() >= 4) {
				name = color.getChild(3).getTextContents();
			} else {
				name = Integer.toHexString(rgbi);
				while (name.length() < 6) {
					name = "0" + name; //$NON-NLS-1$
				}
				name = "#" + name; //$NON-NLS-1$
			}
			l.add(new Color(rgbi, name));
		}
		return l;
	}
	
	private static class Color implements Entry<Integer, String> {

		private String s;
		private Integer i;
		
		public Color(Integer i, String s) {
			this.i = i;
			this.s = s;
		}
		
		public Integer getKey() {
			return i;
		}

		public String getValue() {
			return s;
		}

		public String setValue(String value) {
			String old = s;
			this.s = value;
			return old;
		}
	}
	
}
