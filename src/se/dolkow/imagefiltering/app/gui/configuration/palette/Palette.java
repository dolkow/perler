package se.dolkow.imagefiltering.app.gui.configuration.palette;

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

import se.dolkow.imagefiltering.PaletteLoader;
import se.dolkow.imagefiltering.TreeParseException;
import se.dolkow.imagefiltering.internationalization.Messages;
import se.dolkow.imagefiltering.tree.Node;

public class Palette {
	protected final List<Color> colors;
	protected String name = null;
	private static int counter = 0;
	
	public Palette() {
		colors = new LinkedList<Color>();
	}
	
	public Palette(Node tree) throws TreeParseException {
		this();
		List<Entry<Integer, String>> loaded = PaletteLoader.loadPalette(tree);
		for (Entry<Integer, String> e : loaded) {
			Color c = new Color(e.getKey(), e.getValue());
			colors.add(c);
		}
	}
	
	public static Palette[] loadFromTree(Node tree) throws TreeParseException {
		if ("palette".equals(tree.getName())) { //$NON-NLS-1$
			return new Palette[]{new Palette(tree)};
		} else {
			List<Palette> palettes = new LinkedList<Palette>();
			for (Node child : tree) {
				Palette[] ps = loadFromTree(child);
				for (Palette p : ps) {
					palettes.add(p);
				}
			}
			return palettes.toArray(new Palette[palettes.size()]);
		}
	}
	
	public String getName() {
		if (name == null) {
			synchronized(Palette.class) {
				name = Messages.getFormatted("Palette.unnamed_palette_template", ++counter); //$NON-NLS-1$
			}
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addColor(Color color) {
		colors.add(color);
	}
	
	public List<Color> getColors() {
		return colors;
	}
	
	public String toString() {
		return getName();
	}
	
}
