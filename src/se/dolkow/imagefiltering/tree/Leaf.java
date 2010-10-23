package se.dolkow.imagefiltering.tree;

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

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Leaf implements Node {

	private static final List<Node> children = new LinkedList<Node>();
	private String text;
	private String name;
	
	public Leaf(String name, String text) {
		this.text = text;
		this.name = name;
	}
	
	public void writeXML(Appendable ap, String tabbing) throws IOException {
		ap.append(tabbing + "<" + name + ">" + text + "</" + name + ">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	public String getTextContents() {
		return text;
	};

	public String getName() {
		return name;
	}

	public List<Node> getChildren() {
		return children;
	}

	public Iterator<Node> iterator() {
		return children.iterator();
	}

	public Node getChild(int index) throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Leaves don't have children"); //$NON-NLS-1$
	}

	public int getNumChildren() {
		return 0;
	}

}
