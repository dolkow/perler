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
package se.dolkow.imagefiltering.app.gui.configuration;

import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import se.dolkow.imagefiltering.ImageProducer;


public class AboutPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public AboutPanel(ImageProducer producer) {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JTextArea text = new JTextArea(producer.getLongDescription());
		text.setEditable(false);
		text.setOpaque(false);
		text.setWrapStyleWord(true);
		text.setLineWrap(true);
		text.setMargin(new Insets(4, 6, 4, 6));
		
		JScrollPane scroll = new JScrollPane(text);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setOpaque(false);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		
		add(scroll);
	}
	
}
