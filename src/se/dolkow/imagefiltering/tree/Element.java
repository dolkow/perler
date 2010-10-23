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

public class Element implements Node, Iterable<Node> {

	private String name;
	private final List<Node> children;
	
	public Element(String name) {
		this.name=name;
		children = new LinkedList<Node>();
	}
	
	public void writeXML(Appendable ap, String tabbing) throws IOException {
		if (children.size() == 0) {
			ap.append(tabbing + "<" + name + " />\n"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			ap.append(tabbing + "<" + name + ">\n"); //$NON-NLS-1$ //$NON-NLS-2$
			for (Node child : children) {
				child.writeXML(ap, tabbing+ "  "); //$NON-NLS-1$
			}
			ap.append(tabbing + "</" + name + ">\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public Iterator<Node> iterator() {
		return children.iterator();
	}

	public String getName() {
		return name;
	}

	public String getTextContents() {
		StringBuilder sb = new StringBuilder();
		for (Node child : children) {
			sb.append(child.getTextContents());
		}
		return sb.toString();
	}

	public void add(Node node) {
		children.add(node);
	}

	public List<Node> getChildren() {
		return children;
	}

	public Node getChild(int index) throws IndexOutOfBoundsException {
		return children.get(index);
	}

	public int getNumChildren() {
		return children.size();
	}
	
}
